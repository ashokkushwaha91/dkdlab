package com.agro.dkdlab.ui.view.guest

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.agro.dkdlab.R
import com.agro.dkdlab.app.DataListener
import com.agro.dkdlab.app.MyApp
import com.agro.dkdlab.custom.Util.fromJson
import com.agro.dkdlab.custom.Util.json
import com.agro.dkdlab.custom.Util.showToast
import com.agro.dkdlab.custom.Util.toExceptNPKValue
import com.agro.dkdlab.custom.Util.toNPKValue
import com.agro.dkdlab.databinding.ActivityGenerateReportBinding
import com.agro.dkdlab.network.model.SoilSampleModel
import com.agro.dkdlab.simple_usb_terminal.SerialService
import com.agro.dkdlab.ui.dialog.SensorAlertDialog
import com.agro.dkdlab.ui.view.login.GuestLoginActivity
import com.agro.dkdlab.ui.view.offline.DashboardActivity
import com.agro.dkdlab.ui.viewmodel.SoilSampleViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class GenerateReportActivity : AppCompatActivity() {
    private lateinit var binding:ActivityGenerateReportBinding
    private  var selectedValue=""
    private var isFinished = false
    private val inputArray = listOf<Int>().toMutableList()
    var consoleTxt = ""
    private val soilSampleViewModel by viewModels<SoilSampleViewModel>()
    var sampleData:SoilSampleModel?=null
    var sensorNotOkDialogOpen=false
    var sensorOkDialogOpen=false
    var sensorNotOkDialog: SensorAlertDialog?=null
    var sensorOkDialog: SensorAlertDialog?=null
    var sensorOk=false
    private var lastRx = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityGenerateReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.textTitle.text="Soil test report"
        selectedValue = intent.getStringExtra("selectedValue").toString()
        sampleData = intent.getStringExtra("sampleData")?.fromJson()
        binding.txtResult.text="$selectedValue Mode"
//        sendQuery()
        MyApp.get().dataListener = object : DataListener{
            override fun onDataRead(data: String) {
                runOnUiThread {
                    processResult(data)
                }
            }

        }
        binding.imageViewBack.setOnClickListener {
            onBackPressed()
        }
       /* binding.btnReset.setOnClickListener {
            binding.btnReset.visibility = INVISIBLE
            binding.progressbar.visibility = VISIBLE
            MyApp.get().disconnect()
            val handler = Handler(mainLooper)
            handler.postDelayed({
                MyApp.get().connect(true)
                handler.postDelayed({
                    isFinished=false
                    sendQuery()
                    binding.btnReset.visibility = VISIBLE
                    binding.progressbar.visibility = GONE
                },500)
            }, 1000)
        }*/
        binding!!.btnSave.setOnClickListener {
            when{
                binding.editResult.text.toString().trim().isEmpty() -> {
                    binding.editResult.requestFocus()
                    binding.editResult.error = resources.getString(R.string.please_wait_for_result)
                    binding.txtError.text = resources.getString(R.string.please_wait_for_result)
                    binding.txtError.visibility=VISIBLE
                }
                consoleTxt == "Processing..."-> {
                    showToast(resources.getString(R.string.please_wait_for_result))
                }
                else->{
                    updateSample()
                }
            }
        }
        binding!!.btnCheck.setOnClickListener {
            when (consoleTxt) {
                "Processing..." -> {
                    showToast(resources.getString(R.string.please_wait_for_result))
                }
                else -> {
                    isFinished=false
                    Handler(Looper.getMainLooper()).postDelayed({
                        if(lastRx == "0") {
                            sendQuery()
                        }else{
                            showToast("Please wait....")
                        }
                    },200)

                }
            }

        }

        MyApp.get().readDataCB = { data ->
            lastRx = data
            when (data) {
                "7" -> {
                    if(!sensorNotOkDialogOpen) {
                        try {
                            sensorNotOkDialog = SensorAlertDialog(this,"Issue detected","Please contact to your nearest patanjali service center.")
                            sensorNotOkDialog?.setCancelable(false)
                            sensorNotOkDialog?.show()
                            sensorNotOkDialogOpen=true
                        }catch (e: Exception){}
                    }
                }
                "0" -> {
                    sensorNotOkDialog?.let {
                        it.dismiss()
                        sensorNotOkDialogOpen=false
                        sensorNotOkDialog=null
                        if(!sensorOkDialogOpen){
                            try {
                                sensorOkDialog= SensorAlertDialog(this,"Issue resolved","Please reconnect the USB cable.")
                                sensorOkDialog?.setCancelable(false)
                                sensorOkDialog?.show()
                                sensorOkDialogOpen=true
                            }catch (e: Exception){}
                        }
                    }
                }
            }
        }
    }


   /* private fun checkSensorStatus(){
        MyApp.get().sensorStatus = {
            when (it) {
                "7" -> {
                    sensorOk=false
                    if(!sensorNotOkDialogOpen) {
                        try {
                            sensorNotOkDialog = SensorAlertDialog(this,"Issue detected","Please contact to your nearest patanjali service center.")
                            sensorNotOkDialog?.setCancelable(false)
                            sensorNotOkDialog?.show()
                            sensorNotOkDialogOpen=true
                        }catch (e: Exception){}
                    }
                }
                "0" -> {
                    sensorOk=true
                    sensorNotOkDialog?.let {
                        it.dismiss()
                        sensorNotOkDialogOpen=false
                        sensorNotOkDialog=null
                        if(!sensorOkDialogOpen){
                            try {
                                sensorOkDialog= SensorAlertDialog(this,"Issue resolved","Please reconnect the USB cable.")
                                sensorOkDialog?.setCancelable(false)
                                sensorOkDialog?.show()
                                sensorOkDialogOpen=true
                            }catch (e: Exception){}
                        }
                    }
                }
            }
        }
    }*/

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
    private fun processResult(it: String){
        try {
            if (it.isNumeric() && !isFinished){
                if(inputArray.size < 15){
//                    _binding!!.editResult.append(it)
                    binding.editResult.setText("Processing...")
                    consoleTxt = "Processing..."
                    binding.myProgressBar.visibility= VISIBLE
                    inputArray.add(it.toInt())
                }else if (inputArray.size > 14){
                    for (i in 0..3){
                        inputArray.removeAt(0)
                    }
                    val result = getFinalResult()
                    Log.e("finalResult", "$result")

                    consoleTxt = result.toString()
                    binding.myProgressBar.visibility= GONE
                    binding.txtError.visibility= GONE
                    when(selectedValue){
                        "Nitrogen","Phosphorus","Potassium","Organic Carbon"->  binding.editResult.setText("${result.toString().toNPKValue()}")
                        "Copper","Manganese","Iron","Zinc","Boron","Sulphur"-> binding.editResult.setText("${result.toString().toExceptNPKValue()}")
                    }

                    MyApp.get().send("0")
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
    private fun getFinalResult() = inputArray.groupingBy { it }.eachCount().maxBy { it.value }.key


    override fun onDestroy() {
        consoleTxt = ""
        /*MyApp.get().send("0")
        MyApp.get().readDataCB = null
        MyApp.get().sensorStatus = null*/
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (consoleTxt == "Processing..."){
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Do you want to terminate the process.")
                .setPositiveButton("Yes",
                    DialogInterface.OnClickListener { dialog, id ->
                        MyApp.get().send("0")
                        super.onBackPressed()
                    })
                .setNegativeButton("No",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
            builder.create()
            builder.show()

        }else super.onBackPressed()
    }
    private fun updateSample() {
        val intent = Intent()
        var result=binding.editResult.text.toString()
        when(selectedValue){
            "Nitrogen"-> sampleData?.nrangName = result
            "Phosphorus"->sampleData?.prangName =result
            "Potassium"-> sampleData?.krangName =result
            "Organic Carbon"-> sampleData?.ocRangName =result
            "Copper"-> sampleData?.copperRangName =result
            "Manganese"->sampleData?.maganeseRangName =result
            "Iron"-> sampleData?.ironRangName=result
            "Zinc"-> sampleData?.zincRangName=result
            "Boron"-> sampleData?.boronRangName=result
            "Sulphur"-> sampleData?.sulphurRangName =result
        }
        intent.putExtra("sampleUpdatedData",sampleData?.json())
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}