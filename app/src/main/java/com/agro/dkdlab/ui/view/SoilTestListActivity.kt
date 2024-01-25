package com.agro.dkdlab.ui.view

import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agro.dkdlab.R
import com.agro.dkdlab.app.MyApp
import com.agro.dkdlab.databinding.ActivitySoilTestBinding
import com.agro.dkdlab.simple_usb_terminal.CustomProber
import com.agro.dkdlab.ui.viewmodel.ReportViewModel
import com.agro.dkdlab.ui.adapter.ReportAdapter
import com.agro.dkdlab.ui.dialog.ConfirmAlertDialog
import com.agro.dkdlab.ui.view.login.LoginActivity
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialProber
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_soil_test.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SoilTestListActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySoilTestBinding
    @Inject
    lateinit var reportAdapter: ReportAdapter
    internal class ListItem(var device: UsbDevice, var port: Int, var driver: UsbSerialDriver?)

    private val listItems = ArrayList<ListItem>()
    private var listAdapter: ArrayAdapter<ListItem>? = null
    private var baudRate = 9600
    private var deviceId =0
    private  var portNum = 0
    private val viewModel: ReportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySoilTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.textTitleHome.text="Soil Test Solution"
        getSoilSampleRecord()
        reportAdapter.apply {
            onItemClick { record ->
                startActivity(Intent(this@SoilTestListActivity,ReportDetails::class.java)
                    .putExtra("barcode",record.sampleBarCode))
            }
        }
        binding.textViewLogout.setOnClickListener {
            ConfirmAlertDialog(
                this,getString(R.string.are_you_sure),getString(R.string.do_you_want_to_logout)
            ).apply {
                setDialogDismissListener {
                    MyApp.get().logout()
                    startActivity(Intent(this@SoilTestListActivity, LoginActivity::class.java))
                    finishAffinity()
                }
            }.show()
        }
        binding.recyclerViewUserList.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewUserList.adapter = reportAdapter
//        reportAdapter.setAdapterData()
        binding.editTextSearch.doAfterTextChanged {
            reportAdapter.onFilter(binding.editTextSearch.text.toString())
        }
        binding.createReport.setOnClickListener {
            if(listItems.size>0){
                var item: ListItem = listItems[0]
                //  for pc panel
                if ((item.driver == null) && (listItems.size >= 2)) {
                    item = listItems[1]
                }
                // end for pc panel
                if (item.driver == null) {
                    Toast.makeText(this, "Driver not found", Toast.LENGTH_SHORT).show()
                } else {
                    deviceId=item.device.deviceId
                    portNum=item.port
                    val intents = Intent(this, ReportResultFragmentActivity::class.java)
                    intents.putExtra("device",deviceId )
                    intents.putExtra("port", portNum)
                    intents.putExtra("baud", baudRate)
                    startActivity(intents)
                }
            }else {
                Toast.makeText(this, "Device not connected", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,ReportResultFragmentActivity::class.java))
            }

        }
        binding.recyclerViewUserList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && binding.createReport.visibility == VISIBLE)
                    binding.createReport.visibility = GONE
                else if (dy < 0 && binding.createReport.visibility != VISIBLE && !layoutDevice.isVisible)
                    binding.createReport.visibility = VISIBLE
            }
        })
        binding.btnConnect.setOnClickListener {
            MyApp.get().connect()
        }

        MyApp.get().onConnectionCallback = {
            txtStatus.text = it
            btnConnect.visibility= if (it == "connected") GONE else VISIBLE
            binding.createReport.visibility = VISIBLE
        }
        swipeRefresh.setOnRefreshListener {
            swipeRefresh?.isRefreshing = true
            getSoilSampleRecord()
        }
    }
    private fun getSoilSampleRecord() {
        lifecycleScope.launch {
            viewModel.getSoilSampleRecord().observe(this@SoilTestListActivity) { stocks ->
                swipeRefresh.isRefreshing = false
                Log.e("userDataSize", "${stocks?.size}")
                Log.e("userData", "${stocks}")
                textCount.text="Result: ${stocks.size}"
                when {
                    stocks.isNotEmpty() -> {
                        reportAdapter.setAdapterData(stocks)
                    }
                    else -> {}
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        refresh()
    }
    private fun refresh() {
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
            layoutDevice.visibility=VISIBLE
            text2.visibility=VISIBLE
            var item: ListItem = listItems[0]

            //  for pc panel
            if ((item.driver == null) && (listItems.size >= 2)) {
                item = listItems[1]
            }
            // end for pc panel
            deviceId=item.device.deviceId
            portNum=item.port
            when{
                item.driver == null -> text1.text ="Driver not found"
                item.driver?.ports?.size == 1 -> text1.text ="Device Name: ${item.driver!!.javaClass.simpleName.replace("SerialDriver", "")}"
                else -> text1.text = "Device Name: ${item.driver?.javaClass?.simpleName?.replace("SerialDriver","") + ", Port " + item.port}"
            }
            text2.text = String.format(Locale.US,"Vendor %04X, Product %04X",item.device.vendorId,item.device.productId)
            txtStatus.text = "Inserted"
            MyApp.get().updateDeviceInfo(deviceId,portNum,baudRate)
            if (MyApp.get().isUsbConnected()){
                txtStatus.text = "Connected"
                btnConnect.visibility = GONE
            }
            binding.createReport.isVisible=!btnConnect.isVisible
        }/*else showToast("Device not connected")*/
    }
    var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        val manager: FragmentManager = supportFragmentManager
        if (manager.backStackEntryCount > 0) {
            super.onBackPressed()
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }
            doubleBackToExitPressedOnce = true
            Toast.makeText(this, getString(R.string.to_exit), Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed(
                { doubleBackToExitPressedOnce = false },
                2000
            )
        }
    }
}