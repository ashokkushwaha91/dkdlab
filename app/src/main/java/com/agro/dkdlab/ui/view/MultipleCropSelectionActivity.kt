package com.agro.dkdlab.ui.view

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.agro.dkdlab.R
import com.agro.dkdlab.app.MyApp
import com.agro.dkdlab.constant.ContextUtils
import com.agro.dkdlab.constant.cropListHaridwarEng
import com.agro.dkdlab.custom.Util.fromListJson
import com.agro.dkdlab.custom.Util.getProgress
import com.agro.dkdlab.custom.Util.hideKeyboard
import com.agro.dkdlab.custom.Util.json
import com.agro.dkdlab.custom.Util.readJsonFromAsset
import com.agro.dkdlab.custom.Util.showAlert
import com.agro.dkdlab.custom.clearSearch
import com.agro.dkdlab.custom.searchValue
import com.agro.dkdlab.network.config.Status
import com.agro.dkdlab.network.model.CropModel
import com.agro.dkdlab.network.tool.ErrorUtils
import com.agro.dkdlab.ui.adapter.MultipleCropSelectionAdapter
import com.agro.dkdlab.ui.viewmodel.CropViewModel
import com.agro.dkdlab.ui.viewmodel.ReportViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_crop.*
import kotlinx.android.synthetic.main.appbarlayout.*
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class MultipleCropSelectionActivity : AppCompatActivity() {
    private var cropAdapter= MultipleCropSelectionAdapter()
    private val cropViewModel: CropViewModel by viewModels()
    private var selectedCropData: List<CropModel>? = null
    private var otherCrop=""
    private val viewModel: ReportViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)

        textTitle.text = getString(R.string.select_crop)
        selectedCropData = intent.getStringExtra("selectedCropData")?.fromListJson()
        otherCrop = intent.getStringExtra("otherCrop").toString()
        hideKeyboard()
        cropAdapter.apply {
            setItemClick {
                val intent = Intent()
                intent.putExtra("cropData", it.json())
                setResult(RESULT_OK, intent)
                finish()
            }
            onVisibilityCheck {
                emptyCropList.visibility=if(cropAdapter.itemCount==0)View.VISIBLE else View.GONE
                recyclerViewCrop.visibility=if(cropAdapter.itemCount==0)View.GONE else View.VISIBLE

            }
        }
        textOk.visibility=VISIBLE
        imageViewBack.setOnClickListener {
            finish()
        }
        recyclerViewCrop.layoutManager = LinearLayoutManager(this)
        recyclerViewCrop.adapter = cropAdapter

        editTextSearchCrop.searchValue {
            cropAdapter.filter.filter(it)
        }
        editTextSearchCrop.clearSearch()

        swipeRefresh.setOnRefreshListener {
            getCrop()
        }
        textOk.setOnClickListener {
                val intent = Intent()
                intent.putExtra("cropData",cropAdapter.getSelectedItems().json())
                setResult(RESULT_OK, intent)
                finish()
        }
        getCrop()
    }

    private fun getCrop() {
        if(MyApp.get().getProfile()?.type=="Guest") {
            var pd=getProgress()
            cropViewModel?.let {
                cropViewModel!!.getCropMasterList("ST02").observe(this) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                pd.dismiss()
                                swipeRefresh.isRefreshing = false
                                if (it.data?.isSuccessful == true && it.data.body() != null) {
                                    swipeRefresh.isRefreshing = false
                                    setCrop(it.data.body()!!)
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
                                swipeRefresh.isRefreshing = false
                                if (it.message == "internet") {
                                    showAlert(
                                        getString(R.string.network_error),
                                        getString(R.string.no_internet)
                                    )
                                } else {
                                    showAlert(getString(R.string.error), it?.message.toString())
                                }
                            }
                            else->{}
                        }
                    }

                }
            }
        }else{
            lifecycleScope.launch {
                viewModel.getCrop().observe(this@MultipleCropSelectionActivity) { cropData ->
                    Log.e("cripData", "$cropData")
                    if (cropData.isEmpty()){
                        try {
                            var fileName= "json/crop.json"
                            val json = MyApp.get().readJsonFromAsset(fileName)
                            json?.let { it ->
                                val data = it.fromListJson<List<CropModel>>()
                                viewModel.insertCrop(data)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }else{
                        setCrop(cropData)
                    }

                }
            }
        }
    }

    private fun setCrop(list: List<CropModel>) {
        val sortedList = list.sortedWith(compareBy { it.cropName }).toMutableList()
        sortedList.add(CropModel(id="0", cropName = "Other Crop"))
        sortedList.add(CropModel(id = "0", cropName = "No Crop"))
        cropAdapter.setCropData(sortedList,selectedCropData,otherCrop)
    }
}