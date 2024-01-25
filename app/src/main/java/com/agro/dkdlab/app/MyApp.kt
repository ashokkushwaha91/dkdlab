package com.agro.dkdlab.app

import android.app.Application
import android.app.PendingIntent
import android.content.*
import android.content.res.Configuration
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.agro.dkdlab.constant.Constants.*
import com.agro.dkdlab.custom.Constants
import com.agro.dkdlab.custom.Util.fromJson
import com.agro.dkdlab.custom.Util.json
import com.agro.dkdlab.custom.Util.showToast
import com.agro.dkdlab.custom.Util.toHexByteArray
import com.agro.dkdlab.locale.LocaleManager
import com.agro.dkdlab.network.model.UserModel
import com.agro.dkdlab.simple_usb_terminal.CustomProber
import com.agro.dkdlab.simple_usb_terminal.SerialListener
import com.agro.dkdlab.simple_usb_terminal.SerialService
import com.agro.dkdlab.simple_usb_terminal.SerialService.Companion.lastCmd
import com.agro.dkdlab.simple_usb_terminal.SerialSocket
import com.agro.dkdlab.ui.view.guest.GuestDashboardActivity
import com.hoho.android.usbserial.driver.SerialTimeoutException
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import dagger.hilt.android.HiltAndroidApp
import java.util.*
import kotlin.math.log


@HiltAndroidApp
class MyApp : Application() , ServiceConnection, SerialListener {
    private lateinit var localStorage: SharedPreferences
    private var deviceId =0
    private  var portNum = 0
    private  var baudRate = 0
    var dataListener: DataListener? = null
    private enum class Connected {
        False, Pending, True
    }

    var warmingListener: WarmingListener? = null
    private var broadcastReceiver: BroadcastReceiver? = null
    private var usbSerialPort: UsbSerialPort? = null
    private var service: SerialService? = null

//    var readCommandDataCB : ((String) -> Unit)? = null
    var readDataCB : ((String) -> Unit)? = null
//    var sensorStatus : ((String) -> Unit)? = null

    private var connected = Connected.False
    private var initialStart = true
    var onConnectionCallback:((String)->Unit)?=null

    private var timer = Timer()
    private var timerTask = object : TimerTask() {
        override fun run() {
            if (isUsbConnected() && lastCmd=="0") {
                Log.e("timer", "$lastCmd")
                send("0")
            }
        }
    }

    private var cTimer : CountDownTimer?=null
    private var total: Long = 40*1000
    var isWarmingTimerFinished = false

    fun startCountDownTimer() {
        isWarmingTimerFinished = false
        if (cTimer == null){
            cTimer = object : CountDownTimer(total, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val d = millisUntilFinished / 1000
                    warmingListener?.onTick(d, false)
                }
                override fun onFinish() {
                    isWarmingTimerFinished = true
                    warmingListener?.onTick(0, true)
                }
            }
            cTimer?.start()
        }

    }

    fun stopWarmingTimer(){
        cTimer?.cancel()
        cTimer = null
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        localStorage = getSharedPreferences(packageName, MODE_PRIVATE)
        LocaleManager.init()

        timer.schedule(timerTask, 0, 5000)

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (Constants.INTENT_ACTION_GRANT_USB == intent.action) {
                    val granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                    connect(granted)
                }
            }
        }

       try {
           if (service != null) service!!.attach(this) else  startService(
               Intent(
                   this,
                   SerialService::class.java
               )
           )

           bindService(
               Intent(this, SerialService::class.java),
               this,
               BIND_AUTO_CREATE
           )
       }catch (e: Exception){}





    }
    override fun onServiceConnected(name: ComponentName?, binder: IBinder) {
//        showToast("onServiceConnected")
        service = (binder as SerialService.SerialBinder).service
        if (service == null) showToast("service is null")
        service?.attach(this)
        if (initialStart) {
            initialStart = false
            connect()
        }
    }

    override fun onTerminate() {
        try {
            unbindService(this)
            if (service != null) service!!.detach()
            timer.cancel()
        } catch (ignored: Exception) {
        }
        super.onTerminate()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        service = null
//        showToast("onServiceDisconnected")
    }

    fun connect() {
        connect(null)
    }
    fun updateDeviceInfo(device_id:Int,port_num:Int,baud_rate:Int) {
        deviceId =device_id
        portNum = port_num
        baudRate = baud_rate
    }

    fun connect(permissionGranted: Boolean? = null) {
//        showToast("connecting...")
        var device: UsbDevice? = null
        val usbManager = getSystemService(USB_SERVICE) as UsbManager
        for (v in usbManager.deviceList.values) if (v.deviceId == deviceId) device = v
        if (device == null) {
            status("connection failed: device not found")
//            Log.e("Log message:","device not found")
            return
        }
        var driver = UsbSerialProber.getDefaultProber().probeDevice(device)
        if (driver == null) {
            driver = CustomProber.customProber.probeDevice(device)
        }
        if (driver == null) {
            status("connection failed: no driver for device")
//            Log.e("Log message:","no driver for device")
            return
        }
        if (driver.ports.size < portNum) {
            status("connection failed: not enough ports at device")
//            Log.e("Log message:","not enough ports at device")
            return
        }
        usbSerialPort = driver.ports[portNum]
        if (permissionGranted == null && !usbManager.hasPermission(driver.device)) {
            val flags =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
            val usbPermissionIntent = PendingIntent.getBroadcast(
                this,
                0,
                Intent(Constants.INTENT_ACTION_GRANT_USB),
                flags
            )
            usbManager.requestPermission(driver.device, usbPermissionIntent)
            return
        }

        val usbConnection = usbManager.openDevice(driver.device)
        if (usbConnection == null) {
            if (!usbManager.hasPermission(driver.device)) status("connection failed: permission denied") else status(
                "connection failed: open failed"
            )
            return
        }
        connected = Connected.Pending
        try {
//            showToast("usbSerialPort initialized")
            usbSerialPort!!.open(usbConnection)
            usbSerialPort!!.setParameters(
                9600,
                UsbSerialPort.DATABITS_8,
                UsbSerialPort.STOPBITS_1,
                UsbSerialPort.PARITY_NONE
            )
            val socket = SerialSocket(this.applicationContext, usbConnection, usbSerialPort!!)
            service?.connect(socket)?:run {
                try {
                    if (service != null) service!!.attach(this) else  startService(
                        Intent(
                            this,
                            SerialService::class.java
                        )
                    )
                    bindService(
                        Intent(this, SerialService::class.java),
                        this,
                        BIND_AUTO_CREATE
                    )
                }catch (e: Exception){}
                service?.connect(socket)
            }
            // usb connect is not asynchronous. connect-success and connect-error are returned immediately from socket.connect
            // for consistency to bluetooth/bluetooth-LE app use same SerialListener and SerialService classes
            onSerialConnect()
        } catch (e: java.lang.Exception) {
            onSerialConnectError(e)
        }
    }

    fun disconnect() {
//        showToast("service disconnect")
        connected = Connected.False
        service?.disconnect()
        usbSerialPort = null
    }

    fun isUsbConnected() = connected == Connected.True

    fun send(str: String) {
        lastCmd = str
        if (connected == Connected.False) {
            Toast.makeText(this, "Device not connected", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            service?.write(str.toHexByteArray())
        } catch (e: SerialTimeoutException) {
            status("write timeout: " + e.message)
        } catch (e: java.lang.Exception) {
            onSerialIoError(e)
        }
    }

    private fun receive(datas: ArrayDeque<ByteArray>?) {
//        showToast("readDataCB")
        datas?.forEach {
             Log.e("readDataCB", String(it))
            /*if(UpdateSoilSampleActivity.currentCommand=="0"*//*&& String(it)=="7"*//*){
                sensorStatus?.invoke(String(it))
            }
            else if(UpdateSoilSampleActivity.currentCommand==String(it)){
                readCommandDataCB?.invoke(String(it))
            }
            else*/
             readDataCB?.invoke(String(it))
            dataListener?.onDataRead(String(it))
         }
    }

    fun status(str: String) {
//        showToast("Service: $str")
        Log.e("Log message status:","$str")
        onConnectionCallback?.invoke(str)
    }

    override fun onSerialConnect() {
        status("connected")
        connected = Connected.True
        send("0")
    }

    override fun onSerialConnectError(e: java.lang.Exception) {
        status("connection failed: " + e.message)
//        Log.e("Log message:","connection failed")
        disconnect()
    }

    override fun onSerialRead(data: ByteArray) {
        Log.e("onSerialRead", "${String(data)}")
        try {
            val datas = ArrayDeque<ByteArray>()
            datas.add(data)
            receive(datas)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onSerialRead(datas: ArrayDeque<ByteArray>) {
        Log.e("onSerialRead", "datas")
        receive(datas)
    }

    override fun onSerialIoError(e: java.lang.Exception) {
        status("connection lost: " + e.message)
//        Log.e("Log message:","connection lost")
        disconnect()
    }

    fun setLogin() {
        putBoolean("login", true)
    }

    fun getLogin(): Boolean = getBoolean("login")
     fun logout(){
         localStorage.edit().clear().apply()
     }

    fun setProfile(profile: UserModel?) {
        profile?.let { putString(PROFILE, it.json()) }
    }

    fun getProfile(): UserModel?{
        return if(getString(PROFILE)=="")
            null
        else
            getString(PROFILE)?.fromJson()!!
    }
    fun getRole(): String{ // get role from profile data
        var type = ""
        val s = getString(PROFILE)
        if (s.isNotEmpty() && s.fromJson<UserModel>().status && !s.fromJson<UserModel>().type.isNullOrEmpty()){
            type = s.fromJson<UserModel>().type.toString()
        }
        return type
    }

    fun setLanguage(language: String) {
        putString(LANGUAGE, language)
    }

    fun getLanguage() = if (getString(LANGUAGE).isEmpty()) "Hindi" else getString(LANGUAGE)

    fun setLocale(locale: String) {
        putString(LOCALE, locale)
    }

    fun getLocale() = if (getString(LOCALE).isEmpty()) "hi" else getString(LOCALE)
    fun setCropData(village: String) {
        putString(CROPDATA, village)
    }
    fun getCropData()  =  getString(CROPDATA)



    private fun getString(key: String) = localStorage.getString(key, "").toString()
    private fun putString(key: String, value: String) {
        localStorage.edit().putString(key, value).apply()
    }
    private fun getBoolean(key: String) = localStorage.getBoolean(key, false)
    private fun putBoolean(key: String, value: Boolean) {
        localStorage.edit().putBoolean(key, value).apply()
    }
    companion object {
        private lateinit var app: MyApp
        fun get(): MyApp = app
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleManager.get().updateResources(this, "hi")
    }

    fun checkNetworkSpeed(){
        // Connectivity Manager
        val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // Network Capabilities of Active Network
        val nc = cm.getNetworkCapabilities(cm.activeNetwork)
        // DownSpeed in MBPS
        nc?.let {
            val downSpeed = (it.linkDownstreamBandwidthKbps)/1000
            // UpSpeed  in MBPS
            val upSpeed = (it.linkUpstreamBandwidthKbps)/1000
            // Toast to Display DownSpeed and UpSpeed
            showToast("Up Speed: $upSpeed Mbps \nDown Speed: $downSpeed Mbps")
        }

    }
}