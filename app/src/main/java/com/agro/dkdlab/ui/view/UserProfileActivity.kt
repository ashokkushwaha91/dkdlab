package com.agro.dkdlab.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.agro.dkdlab.R
import com.agro.dkdlab.app.MyApp
import com.agro.dkdlab.custom.Util.fromJson
import com.agro.dkdlab.custom.Util.hideKeyboard
import com.agro.dkdlab.network.model.UserModel
import com.agro.dkdlab.ui.dialog.ConfirmAlertDialog
import com.agro.dkdlab.ui.view.login.GuestLoginActivity
import com.agro.dkdlab.ui.view.login.LoginActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_user_profile.*

@AndroidEntryPoint
class UserProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        hideKeyboard()
        setProfileData()
        textViewLogout.setOnClickListener {
            ConfirmAlertDialog(this,
                getString(R.string.logout_title),
                getString(R.string.logout_message)
            ).apply {
                setDialogDismissListener {
                    MyApp.get().logout()
                    startActivity(Intent(this@UserProfileActivity, GuestLoginActivity::class.java))
                    finishAffinity()
                }
            }.show()
        }

        imageViewBack.setOnClickListener {
            finish()
        }
    }

    private fun setProfileData(){

        var profile: UserModel?=null
        if(intent.hasExtra("userProfileData")){
            profile=intent.getStringExtra("userProfileData")?.fromJson()
        }else profile=MyApp.get().getProfile()

        profile?.let {
            textTitle.text="Profile ( ${it.type} )"
            txtName.text=it.name
            txtDOB.text=it.dob
            txtMobile.text=it.primaryPhone
            txtAadhaarNumber.text=it.aadhaarNumber
            it.userPhoto?.let {
                Glide.with(this).load(it).transform(CircleCrop()).into(profile_image)
            }
            it.aadhaarPhoto?.let {
                Glide.with(this).load(it).into(imageViewAadhaarFront)
            }
            it.aadhaarAddress?.let {
                Glide.with(this).load(it).into(imageViewAadhaarBack)
            }
            profile_image.setOnClickListener { view->
//                imageZoom(it.userPhoto,view,it.name?:it.userName.toString())
            }
            imageViewAadhaarFront.setOnClickListener { view->
//                imageZoom(it.aadhaarPhoto,view,"Aadhaar photo")
            }
            imageViewAadhaarBack.setOnClickListener { view->
//                imageZoom(it.aadhaarAddress,view,"Aadhaar photo")
            }

        }
    }
//    private fun imageZoom(photoUrl: String?, view: View, name: String) {
//        var fileType=name
//        if(!fileType.contains("Aadhaar")) fileType="User photo"
//        if(!photoUrl.isNullOrEmpty()){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                val intent = Intent(this, ImageZoomActivity::class.java).putExtra("url",photoUrl)
//                    .putExtra("name",name)
//                val options = ActivityOptions
//                    .makeSceneTransitionAnimation(this, view, "robot")
//                startActivity(intent, options.toBundle())
//            } else {
//                startActivity(Intent(this, ImageZoomActivity::class.java).putExtra("url",photoUrl)
//                    .putExtra("name",name))
//            }
//        }
//        else showToast("$fileType not found")
//    }

}