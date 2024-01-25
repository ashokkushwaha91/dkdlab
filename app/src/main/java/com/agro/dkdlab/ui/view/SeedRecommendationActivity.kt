package com.agro.dkdlab.ui.view

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.agro.dkdlab.R
import com.agro.dkdlab.app.MyApp
import com.agro.dkdlab.constant.ContextUtils
import com.agro.dkdlab.custom.Util.fromJson
import com.agro.dkdlab.custom.Util.fromListJson
import com.agro.dkdlab.custom.Util.getProgress
import com.agro.dkdlab.custom.Util.json
import com.agro.dkdlab.custom.Util.showAlert
import com.agro.dkdlab.network.config.Status
import com.agro.dkdlab.network.model.FertCropModel
import com.agro.dkdlab.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_seed_recommendation.edtCrop
import kotlinx.android.synthetic.main.activity_seed_recommendation.layoutSeedRecomandation
import kotlinx.android.synthetic.main.activity_seed_recommendation.textSeedWeight
import kotlinx.android.synthetic.main.appbarlayout.textTitle
import java.util.Locale


@AndroidEntryPoint
class SeedRecommendationActivity : AppCompatActivity() {
    private var language = "English"
    var cropModel: List<FertCropModel>? = null
//    var farm: FarmValueLocal? = null
    private val viewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seed_recommendation)

        textTitle.text = getString(R.string.seed_calculation)
        cropModel = MyApp.get().getCropData().fromListJson()
//        farm = intent.getStringExtra("farmDetails").toString().fromJson()

        language = MyApp.get().getLanguage()
//        txt_formValue.text=farm?.farmSize
//        layout1_title.visibility=if(farm?.farmSize.isNullOrEmpty()) View.GONE else View.VISIBLE
       /* bindCrop(cropModel)*/
        edtCrop.setOnClickListener {
                cropModel?.let {
                    Log.e("cropData",it.json())
                    startActivityForResult(Intent(applicationContext,
                        CropListActivity::class.java).putExtra(
                        "cropList", it.json()), 102)
                }
        }

    }

    private fun getAdapter(list: List<String>): ArrayAdapter<String> {
        val adapter = ArrayAdapter(this, R.layout.spinner_source_layout, list)
        adapter.setDropDownViewResource(R.layout.spinner_source_layout)
        return adapter
    }

    fun getSeedRecomandation(farmId: String, cropId: String, stateCode: String) {
        val pd = getProgress()
        viewModel.getseedValueCalculation(
            farmId = farmId,
            cropId = cropId,
            stateCode = stateCode
        ).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        pd.dismiss()
                        if (it.data != null) {
                            layoutSeedRecomandation.visibility = if (it.data.isNotEmpty()) View.VISIBLE else View.GONE
                            Log.e("seedCalculation: ",it.data!!.json())
                            if(it.data.isNotEmpty()) {
                                textSeedWeight.text = it.data.first()
                            }
                        }
                    }

                    Status.ERROR -> {
                        pd.dismiss()
                        if (it.message == "internet") {
                            showAlert(
                                getString(R.string.network_error),
                                getString(R.string.no_internet)
                            )
                        } else {
                            showAlert(getString(R.string.error), it.message.toString())
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun back(view: View) {
        finish()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 102 && resultCode === RESULT_OK) {
            var cropData: FertCropModel=data?.getStringExtra("cropData")?.fromJson()!!
            cropData?.let {
                edtCrop.setText(cropData.cropName)
//                getSeedRecomandation(farm?.farmId.toString(), cropData.id,cropData.stateCode.toString())
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    override fun attachBaseContext(newBase: Context?) {
        val locale = Locale(MyApp.get().getLocale())
        val localeUpdatedContext: ContextWrapper = ContextUtils.updateLocale(newBase!!, locale)
        super.attachBaseContext(localeUpdatedContext)
    }
}