package com.agro.dkdlab.ui.view.guest

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.agro.dkdlab.R
import com.agro.dkdlab.app.MyApp
import com.agro.dkdlab.custom.Util.getProgress
import com.agro.dkdlab.custom.Util.hideKeyboard
import com.agro.dkdlab.custom.Util.showAlert
import com.agro.dkdlab.network.config.Status
import com.agro.dkdlab.network.tool.ErrorUtils
import com.agro.dkdlab.ui.dialog.ConfirmAlertDialog
import com.agro.dkdlab.ui.view.login.GuestLoginActivity
import com.agro.dkdlab.ui.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        hideKeyboard()
        textTitle.text="Profile"
        setProfileData()
        textViewLogout.setOnClickListener {
            ConfirmAlertDialog(this,
                getString(R.string.logout_title),
                getString(R.string.logout_message)
            ).apply {
                setDialogDismissListener {
                    MyApp.get().logout()
                    startActivity(Intent(this@ProfileActivity, GuestLoginActivity::class.java))
                    finishAffinity()
                }
            }.show()
        }

        imageViewBack.setOnClickListener {
            finish()
        }
        txtName.setOnClickListener {
//            backupDatabase(this)
        }
    }
    private fun getAddress(id: String) {
        val pd = getProgress()
        loginViewModel.findUserAddressByUserId(id).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        pd.dismiss()
                        if (it.data?.isSuccessful == true && it.data.body() != null) {
                             if(it.data.body()!!.isNotEmpty()){
                                 var address=it.data.body()!![0]
                                 txtAddress.text="${address.villageName}, ${address.blockName}, ${address.districtName}, ${address.stateName}"
                             }
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

    private fun setProfileData(){

//        var profile=Accounts.get(MyApp.get().getProfile()?.primaryPhone.toString())
        var profile=MyApp.get().getProfile()
        profile?.let {
//            txtName.text= profile["name"]
//            txtMobile.text=profile["mobileNumber"]
//            txtAddress.text="${profile["village"]}, ${ profile["block"] }, ${profile["district"]}, Punjab \n${profile["pinCode"] }"
            txtName.text= it.name
            txtMobile.text=it.primaryPhone
            getAddress(it.id)
        }
    }
}