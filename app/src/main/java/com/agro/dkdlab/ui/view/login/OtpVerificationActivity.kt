package com.agro.dkdlab.ui.view.login

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.agro.dkdlab.R
import com.agro.dkdlab.app.MyApp
import com.agro.dkdlab.constant.ContextUtils
import com.agro.dkdlab.custom.Util.collapse
import com.agro.dkdlab.custom.Util.expand
import com.agro.dkdlab.custom.Util.getProgress
import com.agro.dkdlab.custom.Util.json
import com.agro.dkdlab.custom.Util.showAlert
import com.agro.dkdlab.custom.Util.showToast
import com.agro.dkdlab.network.config.Status
import com.agro.dkdlab.ui.view.SplashActivity
import com.agro.dkdlab.ui.view.admin.AdminDashboardActivity
import com.agro.dkdlab.ui.view.guest.GuestDashboardActivity
import com.agro.dkdlab.ui.view.guest.SoilSampleActivity
import com.agro.dkdlab.ui.view.offline.DashboardActivity
import com.agro.dkdlab.ui.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_otp_verification.*
import java.util.*


@AndroidEntryPoint
class OtpVerificationActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)


        textMobileNumber.text = "+91-" + intent.getStringExtra("mobile_number").toString()
        addTextWatcher(et1)
        addTextWatcher(et2)
        addTextWatcher(et3)
        addTextWatcher(et4)

        startCountDownTimer()
        hideSoftInputKeyboard()
        layoutVerifyOTP.setOnClickListener {
            var otp=getOTPFromText()
            when{
                otp.isEmpty()-> showToast("Please enter OTP")
                otp.length<4-> showToast("Please enter correct OTP")
                else -> verifyOTP(otp)
            }
        }
        imageViewEditMobile.setOnClickListener {
            finish()
        }
        textResend.setOnClickListener {
            resendOtp()
        }
    }
    private fun addTextWatcher(et: EditText) {
        et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                when (et.id) {
                    R.id.et1 -> if (et.length() == 1) {
                        et2.requestFocus()
                    }
                    R.id.et2 -> if (et.length() == 1) {
                        et3.requestFocus()
                    } else if (et.length() == 0) {
                        et1.requestFocus()
                    }
                    R.id.et3 -> if (et.length() == 1) {
                        et4.requestFocus()
                    } else if (et.length() == 0) {
                        et2.requestFocus()
                    }
                    R.id.et4 -> if (et.length() == 1) {
                        hideSoftInputKeyboard()
                        et.clearFocus()
//                        verifyOTP(getOTPFromText())
                    } else if (et.length() == 0) {
                        et3.requestFocus()
                    }
                }
            }
        })
    }

    private fun startCountDownTimer() {
        resendView.collapse()
        progressBar.visibility = View.VISIBLE
        val timer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val d = millisUntilFinished / 1000
                textTimer.text = "$d"
            }

            override fun onFinish() {
                resendView.expand()
                textTimer.text = "0"
                progressBar.visibility = View.GONE
            }
        }
        timer.start()
    }

    private fun getOTPFromText(): String {
        return et1.text.toString() + et2.text.toString() + et3.text.toString() + et4.text.toString()
    }

    private fun verifyOTP(otp: String) {
        val pd = getProgress()
        val body = HashMap<String, String>()
        body["phoneNumber"] = intent.getStringExtra("mobile_number").toString()
        body["otp"] = otp
        body["deviceId"] = SplashActivity.DEVICEID

        Log.e("otpParams", body.json())
        loginViewModel.verifyDKDOtp(body).observe(this) {

            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        pd.dismiss()
                        if (it.data?.isSuccessful == true  && it.data?.body()?.status == true) {
                            Log.e("verifyOTP", it.data.body()!!.json())
                            MyApp.get().setProfile(it.data.body())
                            when{
                                it.data.body()?.type?.isNotEmpty() == true && it.data.body()?.type=="Offline"->{
                                    startActivity(Intent(this, DashboardActivity::class.java))
                                }
                                it.data.body()?.type?.isNotEmpty() == true && it.data.body()?.type=="dkd5-Admin" || it.data.body()?.type?.lowercase()=="admin"->{
                                    startActivity(Intent(this, AdminDashboardActivity::class.java))
                                }
                                it.data.body()?.type?.isNotEmpty() == true && it.data.body()?.type=="Guest"->{
                                    startActivity(Intent(this, GuestDashboardActivity::class.java))
                                }
                                else ->{
                                    showToast("User role not found")
                                }/*startActivity(Intent(this, GuestDashboardActivity::class.java))*/
                            }
                            finishAffinity()
                        } else {
                            showToast(it.data?.body()?.message.toString())
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
                    else->{}
                }
            }
        }
    }

    private fun resendOtp() {
        loginViewModel?.let {
            val pd = getProgress()
            loginViewModel!!.sendOtp(intent.getStringExtra("mobile_number").toString(), "English").observe(this) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            pd.dismiss()
                            if (it.data?.isSuccessful == true) {
                                startCountDownTimer()
                            }else showToast(it.data?.message().toString())
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
                        else->{}
                    }
                }
            }

        }
    }

    fun hideSoftInputKeyboard() {
        val inputManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}