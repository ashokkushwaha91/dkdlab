package com.agro.dkdlab.ui.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.agro.dkdlab.R
import com.agro.dkdlab.app.MyApp
import com.agro.dkdlab.custom.Util.fromJson
import com.agro.dkdlab.custom.Util.getDateTime
import com.agro.dkdlab.custom.Util.isValidMobile
import com.agro.dkdlab.custom.Util.showToast
import com.agro.dkdlab.custom.Util.toExceptNPKValue
import com.agro.dkdlab.custom.Util.toNPKValue
import com.agro.dkdlab.custom.validateForError
import com.agro.dkdlab.databinding.FragmentMainBinding
import com.agro.dkdlab.network.model.SoilSampleModel
import com.agro.dkdlab.ui.database.entities.ReportEntity
import com.agro.dkdlab.ui.database.entities.TestParametersModel
import com.agro.dkdlab.ui.view.ReportResultFragmentActivity
import com.agro.dkdlab.ui.view.ReportResultFragmentActivity.Companion.consoleTxt
import com.agro.dkdlab.ui.viewmodel.ReportViewModel
import com.google.android.material.textfield.TextInputLayout
import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReportResultFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val inputArray = listOf<Int>().toMutableList()
    private var soilSample:SoilSampleModel?=null
    private  var selectedValue=""
    private val viewModel: ReportViewModel by viewModels()
    private var reportData: ReportEntity?=null
    var barcodeExist=false
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val root: View = binding.root
        Log.e( "onCreateView: ","onCreateView")
        _binding!!.inputLayoutBarCode.setEndIconOnClickListener {
            barcodeLauncher.launch(ScanOptions())
        }
        _binding!!.btnCheck.setOnClickListener {
            isFinished=false
            sendQuery()
        }
        selectedValue = requireArguments().getString("selectedValue").toString()
        reportData = requireArguments().getString("reportData")?.fromJson()
        _binding!!.txtResult.text=selectedValue
        reportData?.let {
            dataBind(it)
        }

        _binding!!.inputLayoutAgentName.validateForError()
        _binding!!.inputLayoutMobile.validateForError()
        _binding!!.inputLayoutBarCode.validateForError()
        _binding!!.inputLayoutFarmerName.validateForError()
        _binding!!.inputLayoutFarmerMobile.validateForError()
        _binding!!.editBarCode.doAfterTextChanged {
            viewModel.getReportByBarcode(_binding!!.editBarCode.text.toString()).observe(viewLifecycleOwner) { datas ->
                if(datas!=null) {
                    barcodeExist=true
                    _binding!!.inputLayoutBarCode.requestFocus()
                    _binding!!.inputLayoutBarCode.error = resources.getString(R.string.barcode_exist)
                }
                else {
                    barcodeExist=false
                    _binding!!.inputLayoutBarCode.requestFocus()
                    _binding!!.inputLayoutBarCode.error = null
                }
            }
        }
        _binding!!.btnSave.setOnClickListener {
            when{
                barcodeExist-> {
                    _binding!!.inputLayoutBarCode.requestFocus()
                    _binding!!.inputLayoutBarCode.error = resources.getString(R.string.barcode_exist)
                }
                _binding!!.editBarCode.text.toString().trim().isEmpty() -> {
                    _binding!!.inputLayoutBarCode.requestFocus()
                    _binding!!.inputLayoutBarCode.error = resources.getString(R.string.enter_scan_barcode)
                }
                _binding!!.editTextAgentName.text.toString().trim().isEmpty() -> {
                    _binding!!.inputLayoutAgentName.requestFocus()
                    _binding!!.inputLayoutAgentName.error = resources.getString(R.string.enter_agent_name)
                }
                _binding!!.editTextMobile.text.toString().trim().isEmpty() -> {
                    _binding!!.inputLayoutMobile.requestFocus()
                    _binding!!.inputLayoutMobile.error = resources.getString(R.string.enter_mobile_number)
                }
                _binding!!.editTextMobile.text.toString().trim().length < 10 -> {
                    _binding!!.inputLayoutMobile.editText?.requestFocus()
                    _binding!!.inputLayoutMobile.error = getString(R.string.enter_10_digit_mobile_number)
                }

                _binding!!.editTextMobile.toString().trim().isNotBlank() && !isValidMobile(_binding!!.editTextMobile.text.toString()) -> {
                    _binding!!.inputLayoutMobile.editText?.requestFocus()
                    _binding!!.inputLayoutMobile.error = resources.getString(R.string.please_enter_valid_mobile_number)
                }

                _binding!!.editResult.text.toString().trim().isEmpty() -> {
                    _binding!!.editResult.requestFocus()
                    _binding!!.editResult.error = resources.getString(R.string.enter_result)
                }

                _binding!!.editTextFarmerMobile.text.toString().trim().isNotEmpty() && _binding!!.editTextFarmerMobile.text.toString().trim().length < 10 -> {
                    _binding!!.inputLayoutFarmerMobile.editText?.requestFocus()
                    _binding!!.inputLayoutFarmerMobile.error = getString(R.string.enter_10_digit_mobile_number)
                }

                _binding!!.editTextFarmerMobile.text.toString().trim().isNotEmpty() && _binding!!.editTextFarmerMobile.toString().trim().isNotBlank() && !isValidMobile(_binding!!.editTextFarmerMobile.text.toString()) -> {
                    _binding!!.inputLayoutFarmerMobile.editText?.requestFocus()
                    _binding!!.inputLayoutFarmerMobile.error = resources.getString(R.string.please_enter_valid_mobile_number)
                }
                else->{
                    updateSoilSample()
                }
            }
        }

        sendQuery()

        MyApp.get().readDataCB = {
            val act = requireActivity()
            if (act != null && isAdded)
                act.runOnUiThread {
                    processResult(it)
                }
        }
        return root
    }
    private fun sendQuery(){
        when(selectedValue){
            "Nitrogen"-> MyApp.get().send("N")
            "Phosphorus"-> MyApp.get().send("P")
            "Potassium"-> MyApp.get().send("K")
            "Organic Carbon"-> MyApp.get().send("C")
            "Copper"-> MyApp.get().send("Copper")
            "Manganese"-> MyApp.get().send("Manganese")
            "Iron"-> MyApp.get().send("Iron")
            "Zinc"-> MyApp.get().send("Zinc")
            "Boron"-> MyApp.get().send("Boron")
            "Sulphur"-> MyApp.get().send("Sulphur")
        }
    }
    private fun dataBind(reportModel: ReportEntity) {
//        _binding!!.editBarCode.focusable=false
        _binding!!.editBarCode.isEnabled=false
        _binding!!.inputLayoutBarCode.endIconMode = TextInputLayout.END_ICON_NONE;
        _binding!!.editBarCode.setText(reportModel.sampleBarCode)
        _binding!!.editTextAgentName.setText(reportModel.name)
        _binding!!.editTextMobile.setText(reportModel.mobile)
        _binding!!.editTextFarmerName.setText(reportModel.farmerName)
        _binding!!.editTextFarmerMobile.setText(reportModel.farmerMobile)
    }

    override fun onDestroy() {
        MyApp.get().send("0")
        MyApp.get().readDataCB = null
        isFinished = false
        inputArray.clear()
        super.onDestroy()
    }

    private var isFinished = false
    private fun processResult(it: String){
        try {
            if (it.isNumeric() && !isFinished){
                if(inputArray.size < 15){
//                    _binding!!.editResult.append(it)
                    _binding!!.editResult.setText("Processing...")
                    consoleTxt = "Processing..."
                    inputArray.add(it.toInt())
                }else if (inputArray.size > 14){
                    for (i in 0..3){
                        inputArray.removeAt(0)
                    }
                    val result = getFinalResult()
                    Log.e("finalResult", "$result")
                    MyApp.get().send("0")
//                    _binding!!.editResult.setText("$result")
                    consoleTxt = result.toString()
                    when(selectedValue){
                        "Nitrogen","Phosphorus","Potassium","Organic Carbon"->  _binding!!.editResult.setText("${result.toString().toNPKValue()}")
                        "Copper","Manganese","Iron","Zinc","Boron","Sulphur"-> _binding!!.editResult.setText("${result.toString().toExceptNPKValue()}")
                    }

                    inputArray.clear()
                    isFinished = true
                }
            }
        }catch (e: Exception){
            Log.e("exception", e.message.toString())
        }
    }

    private fun String.isNumeric(): Boolean {
        return this.all { char -> char.isDigit() }
    }

    private val barcodeLauncher = registerForActivityResult(ScanContract())
    { result: ScanIntentResult ->
//        _binding!!.editBarCode.setText("")
        if (result.contents == null) {
            val originalIntent = result.originalIntent
            if (originalIntent == null) {
                requireActivity().showToast("Cancelled")
            } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                requireActivity().showToast("Cancelled due to missing camera permission")
            }
        } else {
            Log.d("MainActivity", "Scanned")
            try {
                val b = result.contents.all { char -> char.isDigit() }
                if (b && result.contents.length==16) {
                    _binding!!.editBarCode.setText(result.contents)
                    (requireActivity() as ReportResultFragmentActivity).getSample(result.contents) {
                        soilSample = it
//                         requireActivity().showToast("Record found")
                    }
                }
                else if(result.contents.length!=16) requireActivity().showToast("Please scan 16 digit barcode")
                else requireActivity().showToast("Invalid barcode")
            } catch (e: Exception) {
                Log.e("NumberFormatException", "${e.message}")
                requireActivity().showToast("${e.message}")
            }
        }
    }

    private fun getFinalResult() = inputArray.groupingBy { it }.eachCount().maxBy { it.value }.key

    private fun updateSoilSample(){
        var dataUpdate=false
        var reportList= mutableListOf<TestParametersModel>()
        reportData?.let { data ->
            reportList.addAll(data.testParametersModel)
            dataUpdate=reportList.remove(data.testParametersModel.find { it.testType==selectedValue })
        }
        reportList.add(TestParametersModel(selectedValue,_binding!!.editResult.text.toString()))
        var userData = ReportEntity(
            if(dataUpdate||reportList.size>1) reportData!!.id else 0,
            _binding!!.editTextAgentName.text.toString(),
            _binding!!.editTextMobile.text.toString(),
            _binding!!.editBarCode.text.toString(),
            _binding!!.editTextFarmerName.text.toString(),
            _binding!!.editTextFarmerMobile.text.toString(),
            getDateTime(),
            reportList
        )
        lifecycleScope.launch {
            if(dataUpdate||reportList.size>1) {
//             viewModel.updateTestReport(userData.testParametersModel, userData.barcode)
                viewModel.updateTestReport(userData)
            }
            else {
                viewModel.insert(userData)
            }
            requireActivity().showToast("Record successfully saved")
            requireActivity().finish()
        }


        /* val fragment = requireActivity().supportFragmentManager.findFragmentByTag("reportFragment")
         if (fragment == null) {
             requireActivity().finish()
         } else {
             requireActivity().supportFragmentManager.popBackStack()
         }*/
    }
}