package com.agro.dkdlab.ui.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.agro.dkdlab.R
import com.agro.dkdlab.custom.Util.getProgress
import com.agro.dkdlab.custom.Util.showAlert
import com.agro.dkdlab.databinding.ActivityReportResult2Binding
import com.agro.dkdlab.network.config.Status
import com.agro.dkdlab.network.model.CreateSampleModel
import com.agro.dkdlab.network.model.SoilSampleModel
import com.agro.dkdlab.ui.view.fragment.ReportCreateFragment
import com.agro.dkdlab.ui.view.fragment.ReportResultFragment
import com.agro.dkdlab.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportResultFragmentActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding:ActivityReportResult2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityReportResult2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.include.textTitle.text="Soil test report"
        if(intent.getBooleanExtra("updateReport",false)){
            val args = Bundle()
            args.putString("selectedValue", intent.getStringExtra("selectedValue"))
            args.putString("reportData", intent.getStringExtra("reportData"))
            val fragment: Fragment = ReportResultFragment()
            fragment.arguments = args
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment).commitNow()


        }else{
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ReportCreateFragment.newInstance())
                .commitNow()
        }

        binding.include.imageViewBack.setOnClickListener {
            if (consoleTxt != "Processing...") {
                val fragment =  supportFragmentManager.findFragmentByTag("reportFragment")
                if (fragment == null) {
                    finish()
                } else {
                    supportFragmentManager.popBackStack()
                }
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        consoleTxt = ""
    }

    override fun onBackPressed() {
        if (consoleTxt != "Processing..."){
            super.onBackPressed()
        }
    }

    companion object{
        var consoleTxt = ""
    }

    fun updateReport(body: Map<String, String>, cb: ((CreateSampleModel) ->Unit)? = null){
        var pd = getProgress()
        viewModel.updateSoilSampleByBarCode(body).observe(this){
            it?.let { resource->
                when (resource.status) {
                    Status.SUCCESS -> {
                        pd.dismiss()
                        cb?.invoke(it.data!!)
                    }
                    Status.ERROR -> {
                        pd.dismiss()
                        if (it.message == "internet") {
                            showAlert(getString(R.string.network_error),getString(R.string.no_internet))
                        } else {
                            showAlert(getString(R.string.error), it.message.toString())
                        }
                    }
                    else -> {}
                }
            }
        }
    }

     fun getSample(barcode:String,cb:((SoilSampleModel?)->Unit)?=null) {
        var pd = getProgress()
        viewModel.soilSampleByBarCode(barcode).observe(this){
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        pd.dismiss()
                        cb?.invoke(it.data)
                    }
                    Status.ERROR -> {
                        pd.dismiss()
                        if (it.message == "internet") {
                            showAlert(getString(R.string.network_error),getString(R.string.no_internet))
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