package com.agro.dkdlab.ui.view.guest

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.agro.dkdlab.R
import com.agro.dkdlab.app.DataListener
import com.agro.dkdlab.app.MyApp
import com.agro.dkdlab.app.WarmingListener
import com.agro.dkdlab.constant.*
import com.agro.dkdlab.custom.Util.capitalizeWords
import com.agro.dkdlab.custom.Util.fromJson
import com.agro.dkdlab.custom.Util.getProgress
import com.agro.dkdlab.custom.Util.json
import com.agro.dkdlab.custom.Util.listNPK
import com.agro.dkdlab.custom.Util.listPh
import com.agro.dkdlab.custom.Util.showAlert
import com.agro.dkdlab.custom.Util.showToast
import com.agro.dkdlab.databinding.ActivityUpdateSoilSampleBinding
import com.agro.dkdlab.network.config.Status
import com.agro.dkdlab.network.model.SoilSampleModel
import com.agro.dkdlab.network.tool.ErrorUtils
import com.agro.dkdlab.simple_usb_terminal.CustomProber
import com.agro.dkdlab.simple_usb_terminal.SerialService
import com.agro.dkdlab.simple_usb_terminal.SerialService.Companion.lastCmd
import com.agro.dkdlab.ui.adapter.SpinnerAdapter
import com.agro.dkdlab.ui.dialog.ConfirmAlertDialog
import com.agro.dkdlab.ui.dialog.SensorAlertDialog
import com.agro.dkdlab.ui.viewmodel.ReportViewModel
import com.agro.dkdlab.ui.viewmodel.SoilSampleViewModel
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialProber
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class UpdateSoilSampleActivity : AppCompatActivity(), DataListener, WarmingListener {
    private val soilSampleViewModel by viewModels<SoilSampleViewModel>()
    private val viewModel: ReportViewModel by viewModels()

   var sampleData:SoilSampleModel?=null
    lateinit var binding:ActivityUpdateSoilSampleBinding
    internal class ListItem(var device: UsbDevice, var port: Int, var driver: UsbSerialDriver?)
    private val listItems = ArrayList<ListItem>()
    private var listAdapter: ArrayAdapter<ListItem>? = null
    private var baudRate = 9600
    private var deviceId =0
    private  var portNum = 0
    var sensorNotOkDialogOpen=false
    var sensorOkDialogOpen=false
    var currentCommandRead=false
    var sensorNotOkDialog: SensorAlertDialog?=null
    var sensorOkDialog: SensorAlertDialog?=null
    var pd: Dialog?=null
    var cmd = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUpdateSoilSampleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.include.textTitle.text="Sample Details"
        callUsbDeviceStatus()
        deviceConnectionState()
        val filter = IntentFilter()
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        registerReceiver(usbReceiver, filter)

        binding.buttonUpdateSample.setOnClickListener {
            validateForm()
        }
        sampleData=intent.getStringExtra("sampleData")?.fromJson()
        bindNValue()
        bindPValue()
        bindKValue()
        bindOCValue()
        bindPhValue()
        setData()
        dataBind()
        registerClicks()
        MyApp.get().onConnectionCallback = {
            Log.e("Log message:","Call back")
            if(!it.contains("connection lost")){
                binding.txtStatus.text = it
                if (it == "connected"){
                    binding.layoutConnection.visibility=  GONE
                    binding.textTimer.visibility= GONE
                    binding.btnConnect.visibility = GONE
                }
            }else{
                binding.layoutMachineReport.visibility= GONE
                binding.layoutNpk.visibility=VISIBLE
                binding.txtDeviceName.text ="Dkd Device"
                binding.txtDeviceDetails.text ="Not Attached"
                binding.layoutStatus.visibility= GONE
            }
        }
        binding.btnConnect.setOnClickListener {
           MyApp.get().connect()
        }

        MyApp.get().warmingListener = this
    }




    private fun deviceConnectionState(){
        /*if (MyApp.get().isWarmingTimerFinished){
            binding.btnConnect.visibility = VISIBLE
            binding.layoutConnection.visibility=  GONE
            binding.textTimer.visibility= GONE
        }else{
            binding.layoutConnection.visibility= VISIBLE
            binding.textTimer.visibility= VISIBLE
            binding.btnConnect.visibility=GONE
        }*/
        when{

            MyApp.get().isWarmingTimerFinished && SerialService.connected -> {
                binding.txtStatus.text = "Connected"
                binding.layoutConnection.visibility = GONE
                binding.textTimer.visibility = GONE
                binding.btnConnect.visibility = GONE
            }

            MyApp.get().isWarmingTimerFinished && !SerialService.connected->{
                binding.txtStatus.text = "Ready to Connect"
                binding.layoutConnection.visibility = GONE
                binding.textTimer.visibility = GONE
                binding.btnConnect.visibility = VISIBLE
            }

            listItems.size > 0 && !MyApp.get().isWarmingTimerFinished ->{
                binding.txtStatus.text = "Attached"
                binding.layoutConnection.visibility = VISIBLE
                binding.textTimer.visibility = VISIBLE
                binding.btnConnect.visibility = GONE
                MyApp.get().startCountDownTimer()
            }
        }
    }
    private fun registerClicks(){
        binding.btnNitrogen.setOnClickListener {
            nextPage("Nitrogen", binding.nValue.text.toString())
        }
        binding.btnPhosphorus.setOnClickListener {
            nextPage("Phosphorus",binding.pValue.text.toString())
        }
        binding.btnPotassium.setOnClickListener {
            nextPage("Potassium",binding.kValue.text.toString())
        }
        binding.btnOrganicCarbon.setOnClickListener {
            nextPage("Organic Carbon",binding.ocValue.text.toString())
        }
        binding.btnCopper.setOnClickListener {
            nextPage("Copper",binding.copperValue.text.toString())
        }
        binding.btnManganese.setOnClickListener {
            nextPage("Manganese",binding.manganeseValue.text.toString())
        }
        binding.btnIron.setOnClickListener {
            nextPage("Iron",binding.ironValue.text.toString())
        }
        binding.btnZinc.setOnClickListener {
            nextPage("Zinc",binding.zincValue.text.toString())
        }

        binding.btnBoron.setOnClickListener {
            nextPage("Boron",binding.boronValue.text.toString())
        }
        binding.btnSulphur.setOnClickListener {
            nextPage("Sulphur",binding.sulphurValue.text.toString())
        }
        binding.include.imageViewBack.setOnClickListener {
            onBackPressed()
        }
    }



    private val usbReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action){
                null -> {
                    showToast("USB Null")
                }
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    MyApp.get().startCountDownTimer()
                    showToast("USB ATTACHED")
                    binding.layoutMachineReport.visibility= VISIBLE
                    binding.layoutNpk.visibility=GONE
                    binding.layoutConnection.visibility= VISIBLE
                    callUsbDeviceStatus()
                }
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    showToast("USB DETACHED")
                    MyApp.get().stopWarmingTimer()
                    /*val am = context!!.getSystemService(ACTIVITY_SERVICE) as ActivityManager
                    val cn = am.getRunningTasks(1)[0].topActivity?.className
                    if(cn=="com.agro.dkdlab.ui.view.guest.GenerateReportActivity"){
                        finishActivity(200)
                    }*/
                    finishActivity(200)
                    binding.layoutMachineReport.visibility= GONE
                    binding.layoutNpk.visibility=VISIBLE
                    binding.txtDeviceName.text ="Dkd Device"
                    binding.txtDeviceDetails.text ="Not Attached"
                    binding.layoutStatus.visibility= GONE
                    binding.layoutConnection.visibility= GONE
                    binding.textTimer.visibility= GONE
                    if(sensorNotOkDialogOpen){
                        sensorNotOkDialog?.let {
                            it.dismiss()
                            sensorNotOkDialogOpen=false
                            sensorNotOkDialog=null
                        }
                    }
                    if(sensorOkDialogOpen){
                        sensorOkDialog?.let {
                            it.dismiss()
                            sensorOkDialogOpen=false
                            sensorOkDialog=null
                        }
                    }
                }
                else -> {
                    showToast("USB else")
                }
            }
        }

    }

    private fun nextPage(str: String, testValue: String) {
//        val isConnected = MyApp.get().isUsbConnected()
        val isConnected = SerialService.connected
        when{
            !isConnected->{
                showToast("Device not connected")
            }
            testValue == "---" || testValue == "" ->{
                sendQuery(str)
            }
            else ->{
                try {
                    val dialog = ConfirmAlertDialog(this, "Alert!", "Do you want to update current value?")
                    dialog.setDialogDismissListener {
                        sendQuery(str)
                    }
                    dialog.show()
                }catch (e: Exception){}
            }
        }
    }
    /*private fun gotoNextPage(str: String){
        sendQuery(str)
        MyApp.get().readCommandDataCB = {
            currentCommandRead=true
            startActivityForResult(
                Intent(this, GenerateReportActivity::class.java)
                    .putExtra("selectedValue", str)
                    .putExtra("sampleData", sampleData?.json()),200
            )
        }
    }*/
    private fun sendQuery(str: String) {
        cmd = str
        when(str){
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

        pd = getProgress().apply {
            setCancelable(true)

        }
    }
    private fun findSelectionPosition(str: String):Int{
        return when(str){
            "Nitrogen"-> listNPK.indexOf(sampleData?.nrangName)
            "Phosphorus"-> listNPK.indexOf(sampleData?.prangName)
            "Potassium"-> listNPK.indexOf(sampleData?.krangName)
            "Organic Carbon"-> listNPK.indexOf(sampleData?.ocRangName)
            "pH"-> listPh.indexOf(sampleData?.phRangName)
            /*"Copper"-> return listNPK.indexOf(sampleData?.copperRangName)
                "Manganese"-> return listNPK.indexOf(sampleData?.maganeseRangName)
                "Iron"-> return listNPK.indexOf(sampleData?.ironRangName)
                "Zinc"-> return listNPK.indexOf(sampleData?.zincRangName)
                "Boron"-> return listNPK.indexOf(sampleData?.boronRangName)
                "Sulphur"-> return listNPK.indexOf(sampleData?.sulphurRangName)*/
            else-> 0
        }
    }

    private fun bindNValue() {
        val adapter = SpinnerAdapter(this,getNList)
//        adapter.setDropDownViewResource(R.layout.spinner_source_layout)
        binding.spinnerNValue.adapter = adapter
        binding.spinnerNValue.setSelection(findSelectionPosition("Nitrogen"))
        binding.spinnerNValue.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                binding.spinnerNValue.tag = getNList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun bindPValue() {
        val adapter = SpinnerAdapter(this,getPList)
//        val adapter = ArrayAdapter(this, R.layout.spinner_source_layout, listNPK)
//        adapter.setDropDownViewResource(R.layout.spinner_source_layout)
        binding.spinnerPValue.adapter = adapter
        binding.spinnerPValue.setSelection(findSelectionPosition("Phosphorus"))
        binding.spinnerPValue.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                binding.spinnerPValue.tag = getPList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun bindKValue() {
//        val adapter = ArrayAdapter(this, R.layout.spinner_source_layout, listNPK)
//        adapter.setDropDownViewResource(R.layout.spinner_source_layout)
        val adapter = SpinnerAdapter(this,getKList)
        binding.spinnerKValue.adapter = adapter
        binding.spinnerKValue.setSelection(findSelectionPosition("Potassium"))
        binding.spinnerKValue.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                binding.spinnerKValue.tag = getKList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun bindOCValue() {
//        val adapter = ArrayAdapter(this, R.layout.spinner_source_layout, listNPK)
//        adapter.setDropDownViewResource(R.layout.spinner_source_layout)
        val adapter = SpinnerAdapter(this,getOCList)
        binding.spinnerOCValue.adapter = adapter
        binding.spinnerOCValue.setSelection(findSelectionPosition("Organic Carbon"))
        binding.spinnerOCValue.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                binding.spinnerOCValue.tag = getOCList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
    private fun bindPhValue() {
//        val adapter = ArrayAdapter(this, R.layout.spinner_source_layout, listPh)
//        adapter.setDropDownViewResource(R.layout.spinner_source_layout)
        val adapter = SpinnerAdapter(this,getPhList)
        binding.spinnerPhValue.adapter = adapter
        binding.spinnerPhValue.setSelection(findSelectionPosition("pH"))
        binding.spinnerPhValue.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                binding.spinnerPhValue.tag = getPhList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setData(){
        sampleData?.let {
            binding.textFarmerName.text=it.farmerName?.capitalizeWords()
            binding.textFathername.text=it.fatherName?.capitalizeWords()
            binding.textMobileNumber.text=it.farmerMobileNumber
            it.villageName?.let{ location->
                binding.textLocation.text="${it.villageName}, ${it.blockName}, ${it.districtName}, ${it.stateName}, ${it.pincode}"
                binding.layoutLocation.visibility=VISIBLE
            }
            binding.textKhasra.text=it.khasraNumber
            binding.textLandArea.text="${it.farmSize} Acre"
            binding.textSampleCode.text=it.sampleBarCode
            binding.textGender.text=it.gender
            binding.textCategory.text=it.category
            binding.textCropName.text=it.cropName
            binding.textIrrigationType.text=it.irrigationSource
            binding.textSampleDate.text="${it.creationDate} ${it.creationTime}"
        }
    }
    private fun dataBind(){
        sampleData?.let {
            binding.nValue.text= it.nrangName?:"---"
            binding.pValue.text= it.prangName?:"---"
            binding.kValue.text= it.krangName?:"---"
            binding.ocValue.text= it.ocRangName?:"---"
            binding.copperValue.text= it.copperRangName?:"---"
            binding.manganeseValue.text= it.maganeseRangName?:"---"
            binding.ironValue.text= it.ironRangName?:"---"
            binding.zincValue.text= it.zincRangName?:"---"
            binding.boronValue.text=it.boronRangName?:"---"
            binding.sulphurValue.text= it.sulphurRangName?:"---"
        }
    }

    private fun validateForm() {
        if(binding.layoutMachineReport.visibility== VISIBLE)
        {
            when {
//                binding.nValue.text.toString().isEmpty() || binding.nValue.text.toString() == "---" -> showToast(getString(R.string.select_n_value))
//                binding.pValue.text.toString().isEmpty() || binding.pValue.text.toString() == "---" -> showToast(getString(R.string.select_p_value))
//                binding.kValue.text.toString().isEmpty() || binding.kValue.text.toString() == "---"  -> showToast(getString(R.string.select_k_value))
//                binding.ocValue.text.toString().isEmpty() || binding.ocValue.text.toString() == "---" -> showToast(getString(R.string.select_oc_value))
                binding.spinnerPhValue.selectedItem.toString().isEmpty() || binding.spinnerPhValue.selectedItemPosition == 0 -> showToast(getString(R.string.select_ph_value))
                /*else -> if(MyApp.get().getProfile()?.type=="Guest") updateSampleOnline("Button")
                         else updateSampleOffline()*/
                else-> updateSampleOnline("Button")
            }
        }else{
            when {
                binding.spinnerNValue.selectedItem.toString().isEmpty() || binding.spinnerNValue.selectedItemPosition == 0 -> showToast(getString(R.string.select_n_value))
                binding.spinnerPValue.selectedItem.toString().isEmpty() || binding.spinnerPValue.selectedItemPosition == 0 -> showToast(getString(R.string.select_p_value))
                binding.spinnerKValue.selectedItem.toString().isEmpty() || binding.spinnerKValue.selectedItemPosition == 0 -> showToast(getString(R.string.select_k_value))
                binding.spinnerOCValue.selectedItem.toString().isEmpty() || binding.spinnerOCValue.selectedItemPosition == 0 -> showToast(getString(R.string.select_oc_value))
                binding.spinnerPhValue.selectedItem.toString().isEmpty() || binding.spinnerPhValue.selectedItemPosition == 0 -> showToast(getString(R.string.select_ph_value))
               /* else -> if(MyApp.get().getProfile()?.type!="Offline") updateSampleOnline("Button")
                            else updateSampleOffline()*/
                else->updateSampleOnline("Button")
            }
        }
    }
    private fun updateSampleOnline(action:String) {
        val pd = getProgress()
        val body = HashMap<String, Any>()
        body["id"] =sampleData?.id.toString()
        if(binding.layoutMachineReport.visibility== VISIBLE)
        {
            body["nrangName"] = sampleData?.nrangName?:""
            body["prangName"] = sampleData?.prangName?:""
            body["krangName"] = sampleData?.krangName?:""
            body["ocRangName"] = sampleData?.ocRangName?:""
            body["phRangName"] = (binding.spinnerPhValue.selectedItem as NPKMOdel).name
//            body["copperRangName"] = sampleData?.copperRangName?:""
//            body["maganeseRangName"] = sampleData?.maganeseRangName?:""
//            body["ironRangName"] = sampleData?.ironRangName?:""
//            body["zincRangName"] = sampleData?.zincRangName?:""
//            body["boronRangName"] = sampleData?.boronRangName?:""
//            body["sulphurRangName"] = sampleData?.sulphurRangName?:""
        }else{
            body["nrangName"] = (binding.spinnerNValue.selectedItem as NPKMOdel).name
            body["prangName"] = (binding.spinnerPValue.selectedItem as NPKMOdel).name
            body["krangName"] = (binding.spinnerKValue.selectedItem as NPKMOdel).name
            body["ocRangName"] = (binding.spinnerOCValue.selectedItem as NPKMOdel).name
            body["phRangName"] = (binding.spinnerPhValue.selectedItem as NPKMOdel).name
        }
        Log.e("soilSampleParams", body.json())


        soilSampleViewModel.updateSampleById(body).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        pd.dismiss()
                        if (it.data?.isSuccessful == true && it.data.body() != null) {
                            Log.d("soilUpdateResponse:", it.data.body()!!.toString())
                            if (it.data.body()?.status == true) {
                                showToast("Report successfully saved")
                                if(action=="Button") {
                                    val intent = Intent()
                                    setResult(RESULT_OK, intent)
                                    finish()
                                }
                            }else showToast(it.data.body()?.message.toString())
                        } else {
                            val apiError = ErrorUtils.parseError(it.data!!)
                            showAlert(getString(R.string.error),if (it.data?.code() == 500) getString(R.string.internal_server_error) else apiError.message)
                        }
                    }
                    Status.ERROR -> {
                        pd.dismiss()
                        if (it.message == "internet") {
                            showAlert(getString(R.string.network_error),getString(R.string.no_internet))
                        } else {
                            showAlert(getString(R.string.error), it?.message.toString())
                        }
                    }
                    else->{}
                }
            }
        }
    }

    private fun updateSampleOffline(){
        if(binding.layoutMachineReport.visibility== VISIBLE)
        {
            sampleData?.let {
                it.ids = sampleData?.ids?:0
                it.nrangName =sampleData?.nrangName?:""
                it.prangName = sampleData?.prangName?:""
                it.krangName = sampleData?.krangName?:""
                it.ocRangName = sampleData?.ocRangName?:""
                it.phRangName = binding.spinnerPhValue.selectedItem.toString()
            }
        }else{
            sampleData?.let {
                it.ids = sampleData?.ids?:0
                it.nrangName = binding.spinnerNValue.selectedItem.toString()
                it.prangName = binding.spinnerPValue.selectedItem.toString()
                it.krangName = binding.spinnerKValue.selectedItem.toString()
                it.ocRangName = binding.spinnerOCValue.selectedItem.toString()
                it.phRangName = binding.spinnerPhValue.selectedItem.toString()
            }
        }

        lifecycleScope.launch {
            sampleData?.let {
                viewModel.updateTestReportNew(it)
                showToast("Report successfully generated")
                finish()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode==200) {
            sampleData=data?.getStringExtra("sampleUpdatedData")?.fromJson()
            dataBind()
            updateSampleOnline("Direct")
        }
    }

    private fun callUsbDeviceStatus() {
        val usbManager = getSystemService(USB_SERVICE) as UsbManager
        val usbDefaultProber = UsbSerialProber.getDefaultProber()
        val usbCustomProber: UsbSerialProber = CustomProber.customProber
        listItems.clear()
        for (device in usbManager.deviceList.values) {
            var driver = usbDefaultProber.probeDevice(device)
            if (driver == null) {
                driver = usbCustomProber.probeDevice(device)
            }
            if (driver != null) {
                for (port in driver.ports.indices) listItems.add(
                    ListItem(
                        device,
                        port,
                        driver
                    )
                )
            } else {
                listItems.add(
                    ListItem(
                        device,
                        0,
                        null
                    )
                )
            }
        }
        checkDeviceConnection()
        listAdapter?.notifyDataSetChanged()

    }

    private fun checkDeviceConnection(){
        Log.e( "onOptionsItemSelected: ", "${listItems.size}")
        if(listItems.size>0) {
            binding.layoutStatus.visibility=VISIBLE
            var item: ListItem = listItems[0]
            //  for pc panel
            if ((item.driver == null) && (listItems.size >= 2)) {
                item = listItems[1]
            }
            // end for pc panel
            deviceId=item.device.deviceId
            portNum=item.port
            when{
                item.driver == null -> binding.txtDeviceName.text ="Driver not found"
                item.driver?.ports?.size == 1 -> binding.txtDeviceName.text ="Dkd Device: ${item.driver!!.javaClass.simpleName.replace("SerialDriver", "")}"
                else -> binding.txtDeviceName.text = "Dkd Device: ${item.driver?.javaClass?.simpleName?.replace("SerialDriver","") + ", Port " + item.port}"
            }
            binding.txtDeviceDetails.text = String.format(Locale.US,"Vendor %04X, Product %04X",item.device.vendorId,item.device.productId)
            binding.txtStatus.text = "Attached"
            MyApp.get().updateDeviceInfo(deviceId,portNum,baudRate)

           /* when{

                MyApp.get().isWarmingTimerFinished && SerialService.connected -> {
                    binding.txtStatus.text = "Connected"
                    binding.layoutConnection.visibility = GONE
                    binding.textTimer.visibility = GONE
                    binding.btnConnect.visibility = GONE
                }

                MyApp.get().isWarmingTimerFinished && !SerialService.connected->{
                    binding.txtStatus.text = "Ready to Connect"
                    binding.layoutConnection.visibility = GONE
                    binding.textTimer.visibility = GONE
                    binding.btnConnect.visibility = VISIBLE
                }

                !MyApp.get().isWarmingTimerFinished ->{
                    binding.txtStatus.text = "Attached"
                    binding.layoutConnection.visibility = VISIBLE
                    binding.textTimer.visibility = VISIBLE
                    binding.btnConnect.visibility = GONE
                    MyApp.get().startCountDownTimer()
                }
            }*/
            deviceConnectionState()
        }
    }

    override fun onPause() {
        super.onPause()
        MyApp.get().dataListener = null
    }

    override fun onResume() {
        super.onResume()
        binding.layoutMachineReport.visibility=if(SerialService.connected || listItems.size>0) VISIBLE else GONE
        binding.layoutNpk.visibility=if(SerialService.connected || listItems.size>0) GONE else VISIBLE

        MyApp.get().dataListener = this

       /* if(MyApp.get().isUsbConnected()) {
            checkSensorStatus()
            Log.e("Timer started", "Started service")
        }*/
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(usbReceiver)
        MyApp.get().warmingListener = null
    }

    override fun onDataRead(data: String) {
        Log.e("cmd", "lastCmd=$lastCmd, data=$data")
        when{
            lastCmd != "0" && data == lastCmd ->{
                pd?.dismiss()
                currentCommandRead=true
                runOnUiThread {
                    startActivityForResult(
                        Intent(this@UpdateSoilSampleActivity, GenerateReportActivity::class.java)
                            .putExtra("selectedValue", cmd)
                            .putExtra("sampleData", sampleData?.json()),200
                    )
                }
            }
            data == "7" ->{
                if(!sensorNotOkDialogOpen) {
                    try {
                        sensorNotOkDialog = SensorAlertDialog(this@UpdateSoilSampleActivity,"Issue detected","Please contact to your nearest patanjali service center.")
                        sensorNotOkDialog?.setCancelable(false)
                        sensorNotOkDialog?.show()
                        sensorNotOkDialogOpen=true
                    }catch (e: Exception){}
                }
            }
            data == "0" ->{
                sensorNotOkDialog?.let {
                    it.dismiss()
                    sensorNotOkDialogOpen=false
                    sensorNotOkDialog=null
                    if(!sensorOkDialogOpen){
                        try {
                            sensorOkDialog= SensorAlertDialog(this@UpdateSoilSampleActivity,"Issue resolved","Please reconnect the USB cable.")
                            sensorOkDialog?.setCancelable(false)
                            sensorOkDialog?.show()
                            sensorOkDialogOpen=true
                        }catch (e: Exception){}
                    }
                }
            }
        }
    }

    override fun onTick(d: Long, isFinished: Boolean) {
        binding.textTimer.text = "$d"
        if (isFinished){
            binding.textTimer.visibility= GONE
            MyApp.get().connect()
            if(SerialService.connected){
                binding.btnConnect.visibility= GONE
            }else{
                binding.btnConnect.visibility= VISIBLE
            }
            binding.layoutConnection.visibility= GONE

        }
    }
}