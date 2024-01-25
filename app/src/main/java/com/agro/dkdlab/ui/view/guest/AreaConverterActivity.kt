package com.agro.dkdlab.ui.view.guest

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.agro.dkdlab.R
import com.agro.dkdlab.app.MyApp
import com.agro.dkdlab.constant.categoryList
import com.agro.dkdlab.custom.Util.getProgress
import com.agro.dkdlab.custom.Util.hideKeyboard
import com.agro.dkdlab.custom.Util.json
import com.agro.dkdlab.custom.Util.showAlert
import com.agro.dkdlab.custom.Util.showToast
import com.agro.dkdlab.custom.validateForError
import com.agro.dkdlab.databinding.ActivityAreaConverterBinding
import com.agro.dkdlab.network.config.Status
import com.agro.dkdlab.network.model.SoilSampleModel
import com.agro.dkdlab.network.model.UnitResponse
import com.agro.dkdlab.network.tool.ErrorUtils
import com.agro.dkdlab.ui.viewmodel.SoilSampleViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.appbarlayout.*
import java.util.*


@AndroidEntryPoint
class AreaConverterActivity : AppCompatActivity() {
    lateinit var binding: ActivityAreaConverterBinding
    private val soilSampleViewModel by viewModels<SoilSampleViewModel>()
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAreaConverterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        textTitle.text = "Soil Sample"
        hideKeyboard()
        imageViewBack.setOnClickListener {
            finish()
        }
        binding.inputLayoutLandSize.validateForError()

        binding.btnConverter.setOnClickListener {
            validateForm()
        }
        getCrop()
    }
    private fun getCrop() {
        val pd = getProgress()
        soilSampleViewModel.landConverterUnit().observe(this){
            it?.let { resource->
                when (resource.status) {
                    Status.SUCCESS -> {
                        pd.dismiss()
                        if(it.data!=null){
                            Log.e("unitModel==>", it.data!!.json())
                            bindCategory(it.data)
                        }
                    }
                    Status.ERROR -> {
                        pd.dismiss()
                        if (it.message == "internet") {
                            showAlert(getString(R.string.network_error),getString(R.string.no_internet))
                        } else {
                            showAlert(getString(R.string.error), it.message.toString())
                        }
                    }
                    else -> {
                        pd.dismiss()
                    }
                }
            }
        }
    }

    private fun bindCategory(data: List<UnitResponse>) {
//        val list: List<String> = data
        val adapter = ArrayAdapter(this, R.layout.spinner_source_layout, data.map { it.value1 })
        adapter.setDropDownViewResource(R.layout.spinner_source_layout)
        binding.spinnerCategory.adapter = adapter
        binding.spinnerCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long,
                ) {
                }
            }
    }


    @RequiresApi(Build.VERSION_CODES.P)
    private fun validateForm() {
        when {
            binding.editLandSize.text.toString().trim().isEmpty() -> {
                binding.inputLayoutLandSize.requestFocus()
                binding.inputLayoutLandSize.error =resources.getString(R.string.enter_land_size)
            }
            binding.spinnerCategory.selectedItem.toString().isEmpty() || binding.spinnerCategory.selectedItemPosition == 0 -> showToast(getString(R.string.select_category))
            else -> landConverter()
        }

    }

    private fun landConverter() {
        val pd = getProgress()
        val body = HashMap<String, String>()
        body["areaToBeConverterd"] = binding.editLandSize.text.toString()
        body["unit"] = binding.spinnerCategory.selectedItem.toString()
        Log.e("converterParams", body.json())

        soilSampleViewModel.landConverter(body).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        pd.dismiss()
                        if (it.data?.isSuccessful == true && it.data.body() != null) {
                            Log.d("converterResponse:", it.data.body()!!.json())
                            binding.txtConvertArea.text="${it.data.body()!!.convertedArea} Acre"
                            binding.txtRemark.text="Remark: ${it.data.body()!!.remarks}"
                        } else {
                            val apiError = ErrorUtils.parseError(it.data!!)
                            showAlert(
                                getString(R.string.error),
                                if (it.data?.code() == 500) getString(R.string.internal_server_error) else apiError.message
                            )
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
                            showAlert(getString(R.string.error), it?.message.toString())
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}