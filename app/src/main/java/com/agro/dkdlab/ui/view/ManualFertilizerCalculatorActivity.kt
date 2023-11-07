package com.agro.dkdlab.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.agro.dkdlab.R
import com.agro.dkdlab.custom.Util
import com.agro.dkdlab.custom.Util.fromJson
import com.agro.dkdlab.custom.Util.getProgress
import com.agro.dkdlab.custom.Util.json
import com.agro.dkdlab.custom.Util.showAlert
import com.agro.dkdlab.custom.Util.showToast
import com.agro.dkdlab.databinding.ActivityManualFertilizerCalculatorBinding
import com.agro.dkdlab.network.config.Status
import com.agro.dkdlab.network.model.SoilSampleModel
import com.agro.dkdlab.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManualFertilizerCalculatorActivity : AppCompatActivity() {
    lateinit var binding:ActivityManualFertilizerCalculatorBinding
    private val viewModel by viewModels<MainViewModel>()
    private var reportData: SoilSampleModel?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityManualFertilizerCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.include.textTitle.text="Fertilizer Calculator"
        binding.include.imageViewBack.setOnClickListener {
            finish()
        }
        reportData=intent.getStringExtra("reportData")?.fromJson()
        binding.editTextFarm.setText(reportData?.farmSize)
        bindNValue()
        bindPValue()
        bindKValue()
        bindOCValue()
        bindPhValue()
        binding.btnCalculate.setOnClickListener {
            clickCalculator()
        }
    }
    private fun findSelectionPosition(str: String):Int{
       /* var a=reportData?.testParametersModel?.find { it.testType==str }
        return list.indexOf(a?.result)*/
        return when(str){
            "Nitrogen"-> Util.listNPK.indexOf(reportData?.nrangName)
            "Phosphorus"-> Util.listNPK.indexOf(reportData?.prangName)
            "Potassium"-> Util.listNPK.indexOf(reportData?.krangName)
            "Organic Carbon"-> Util.listNPK.indexOf(reportData?.ocRangName)
            "Copper"-> Util.listPh.indexOf(reportData?.copperRangName)
            "Manganese"-> Util.listPh.indexOf(reportData?.maganeseRangName)
            "Iron"-> Util.listPh.indexOf(reportData?.ironRangName)
            "Zinc"-> Util.listPh.indexOf(reportData?.zincRangName)
            "Boron"-> Util.listPh.indexOf(reportData?.boronRangName)
            "Sulphur"-> Util.listPh.indexOf(reportData?.sulphurRangName)
            "pH"-> Util.listPh.indexOf(reportData?.phRangName)
            else -> 0
        }
    }
    private fun bindNValue() {
        val adapter = ArrayAdapter(this, R.layout.spinner_source_layout, Util.listNPK)
        adapter.setDropDownViewResource(R.layout.spinner_source_layout)
        binding.spinnerNValue.adapter = adapter
        binding.spinnerNValue.setSelection(findSelectionPosition("Nitrogen"))
        binding.spinnerNValue.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long, ) {
                binding.spinnerNValue.tag = Util.listNPK[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }
    private fun bindPValue() {
        val adapter = ArrayAdapter(this, R.layout.spinner_source_layout, Util.listNPK)
        adapter.setDropDownViewResource(R.layout.spinner_source_layout)
        binding.spinnerPValue.adapter = adapter
        binding.spinnerPValue.setSelection(findSelectionPosition("Phosphorus"))
        binding.spinnerPValue.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long, ) {
                binding.spinnerPValue.tag = Util.listNPK[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }
    private fun bindKValue() {
        val adapter = ArrayAdapter(this, R.layout.spinner_source_layout, Util.listNPK)
        adapter.setDropDownViewResource(R.layout.spinner_source_layout)
        binding.spinnerKValue.adapter = adapter
        binding.spinnerKValue.setSelection(findSelectionPosition("Potassium"))
        binding.spinnerKValue.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long, ) {
                binding.spinnerKValue.tag = Util.listNPK[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }
    private fun bindOCValue() {
        val adapter = ArrayAdapter(this, R.layout.spinner_source_layout, Util.listNPK)
        adapter.setDropDownViewResource(R.layout.spinner_source_layout)
        binding.spinnerOCValue.adapter = adapter
        binding.spinnerOCValue.setSelection(findSelectionPosition("Organic Carbon"))
        binding.spinnerOCValue.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long, ) {
                binding.spinnerOCValue.tag = Util.listNPK[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }

    private fun bindPhValue() {
        val adapter = ArrayAdapter(this, R.layout.spinner_source_layout, Util.listPh)
        adapter.setDropDownViewResource(R.layout.spinner_source_layout)
        binding.spinnerPhValue.adapter = adapter
        binding.spinnerPhValue.setSelection(findSelectionPosition("pH"))
        binding.spinnerPhValue.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long, ) {
                binding.spinnerPhValue.tag = Util.listPh[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }
    private fun clickCalculator() {
        when {
            binding.editTextFarm.text.toString().isEmpty()->{
                binding.inputLayoutFarm.error="Please fill farm size"
                binding.inputLayoutFarm.requestFocus()
            }
            binding.spinnerNValue.selectedItem.toString().isEmpty() || binding.spinnerNValue.selectedItemPosition == 0 -> showToast("Please select nitrogen value")
            binding.spinnerPValue.selectedItem.toString().isEmpty() || binding.spinnerPValue.selectedItemPosition == 0 -> showToast("Please select phosphorus value")
            binding.spinnerKValue.selectedItem.toString().isEmpty() || binding.spinnerKValue.selectedItemPosition == 0 -> showToast("Please select potassium value")
            binding.spinnerOCValue.selectedItem.toString().isEmpty() || binding.spinnerOCValue.selectedItemPosition == 0 -> showToast("Please select organic carbon value")
            else -> {
                fertCalculateManual()
            }
        }
    }

    private fun fertCalculateManual() {
        val pd = getProgress()
        viewModel.storeFertCalcManual(
            farmSize = binding.editTextFarm.text.toString(),
            nRangName =  binding.spinnerNValue.selectedItem.toString(),
            pRangName = binding.spinnerPValue.selectedItem.toString(),
            kRangName = binding.spinnerKValue.selectedItem.toString(),
            ocRangName = binding.spinnerOCValue.selectedItem.toString(),
            phRangName = binding.spinnerPhValue.selectedItem.toString()
        ).observe(this){
            it?.let { resource->
                when (resource.status) {
                    Status.SUCCESS -> {
                        pd.dismiss()
                        startActivity(Intent(this,FertilizerRecommendationActivity::class.java).putExtra("recommendation",it.data?.json()).putExtra("farmSize",binding.editTextFarm.text.toString()))
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
}