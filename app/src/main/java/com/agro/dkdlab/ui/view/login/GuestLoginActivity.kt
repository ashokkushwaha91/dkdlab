package com.agro.dkdlab.ui.view.login

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.agro.dkdlab.R
import com.agro.dkdlab.app.AppPermission.askLocationWithCameraPermission
import com.agro.dkdlab.app.AppPermission.enableLocationSettings
import com.agro.dkdlab.app.AppPermission.hasLocationPermission
import com.agro.dkdlab.app.AppPermission.isLocationEnabled
import com.agro.dkdlab.app.MyApp
import com.agro.dkdlab.constant.ContextUtils
import com.agro.dkdlab.custom.Util.getProgress
import com.agro.dkdlab.custom.Util.hideKeyboard
import com.agro.dkdlab.custom.Util.showAlert
import com.agro.dkdlab.custom.Util.showToast
import com.agro.dkdlab.custom.validateForError
import com.agro.dkdlab.network.config.Status
import com.agro.dkdlab.ui.view.SplashActivity
import com.agro.dkdlab.ui.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_guest_login.*
import java.util.*

@AndroidEntryPoint
class GuestLoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest_login)
        hideKeyboard()
        MyApp.get().setLanguage("English")
        MyApp.get().setLocale("en")
        val temperatureMinStyledText =
            "<font color='${resources.getColor(R.color.textColor)}'><small>${
                resources.getString(R.string.login_text_message1)
            }</small> <b>${resources.getString(R.string.login_text_message2)}</b><br><small>${
                resources.getString(R.string.login_text_message3)
            }</small>"
        textView.setText(
            Html.fromHtml(temperatureMinStyledText),
            TextView.BufferType.SPANNABLE
        )

        layoutSendOTP.setOnClickListener {
            validateForm()
        }
        inputLayoutMobile.validateForError()
        SplashActivity.DEVICEID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        when{
            !isLocationEnabled() -> enableLocationSettings(432)
            hasLocationPermission()-> {
            }
            else-> askLocationWithCameraPermission(200)
        }
    }
    private fun validateForm() {
        when{
            inputLayoutMobile.editText?.text.toString().trim().length < 10 -> {
                inputLayoutMobile.editText?.requestFocus()
                inputLayoutMobile.error = resources.getString(R.string.enter_valid_number)
            }
            else -> login()
        }
    }

    private fun login() {
            var pd = getProgress()
            loginViewModel.sendOtp(editMobile.text.toString(), "English").observe(this) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            pd.dismiss()
                            if (it.data?.isSuccessful == true) {
                                if (it.data.body()?.otp == "")
                                    showToast(it.data.body()?.message.toString())
                                else
                                    startActivity(Intent(applicationContext,
                                        OtpVerificationActivity::class.java).putExtra("mobile_number", editMobile.text.toString().trim()))
                            } else showToast(it.data?.message().toString())
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