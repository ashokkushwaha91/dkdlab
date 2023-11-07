package com.agro.dkdlab.ui.view.guest

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.agro.dkdlab.R
import com.agro.dkdlab.app.MyApp
import com.agro.dkdlab.custom.Util.capitalizeWords
import com.agro.dkdlab.custom.Util.getProgress
import com.agro.dkdlab.custom.Util.hideKeyboard
import com.agro.dkdlab.custom.Util.showAlert
import com.agro.dkdlab.network.config.Status
import com.agro.dkdlab.network.model.SoilSampleModel
import com.agro.dkdlab.network.tool.ErrorUtils
import com.agro.dkdlab.ui.adapter.ViewPagerAdapter
import com.agro.dkdlab.ui.dialog.ConfirmAlertDialog
import com.agro.dkdlab.ui.view.guest.fragment.ReportFragment
import com.agro.dkdlab.ui.view.guest.fragment.SampleFragment
import com.agro.dkdlab.ui.view.login.GuestLoginActivity
import com.agro.dkdlab.ui.view.login.RegisterUserActivity
import com.agro.dkdlab.ui.viewmodel.SoilSampleViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_guest_dashboard.*
import kotlinx.android.synthetic.main.appbarlayout.*

@AndroidEntryPoint
class GuestDashboardActivity : AppCompatActivity() {
    private var userType:String?=null
    private var primaryPhone:String?=null
    private val soilSampleViewModel: SoilSampleViewModel by viewModels()
    var sampleDataList:List<SoilSampleModel>? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest_dashboard)

        viewPagerPosition = 0
        hideKeyboard()

        var userData = MyApp.get().getProfile()
        userData?.let {
            userType=it.type
            primaryPhone=it.primaryPhone
            textTitle.text=it.name.capitalizeWords()
        }
        soilSampleByPrimaryPhone()

        textViewProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        if(userType=="Admin") txtAddUser.visibility=VISIBLE
        txtAddUser.setOnClickListener {
            startActivity(Intent(this, RegisterUserActivity::class.java))
        }
        textViewLogout.setOnClickListener {
            ConfirmAlertDialog(this,
                getString(R.string.logout_title),
                getString(R.string.logout_message)
            ).apply {
                setDialogDismissListener {
                    MyApp.get().logout()
                    startActivity(Intent(this@GuestDashboardActivity, GuestLoginActivity::class.java))
                    finishAffinity()
                }
            }.show()
        }
    }

     fun soilSampleByPrimaryPhone() {
         var pd=getProgress()
         soilSampleViewModel.soilSampleByPrimaryPhone(primaryPhone.toString()).observe(this) {
                 it?.let { resource ->
                     when (resource.status) {
                         Status.SUCCESS -> {
                             pd.dismiss()
                             if (it.data?.isSuccessful == true && it.data.body() != null) {
                                 Log.e("soilSampleListRes:", it.data.body().toString())
                                 sampleDataList=it.data.body()?.reversed()
                                 sampleDataList?.let { it1 -> setPager(it1) }
                             } else {
                                 val apiError = ErrorUtils.parseError(it.data!!)
                                 showAlert(
                                     getString(R.string.error),
                                     if (it.data.code() == 500) getString(R.string.internal_server_error) else apiError.message
                                 )
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
                                 showAlert(getString(R.string.error), it.message.toString())
                             }
                         }
                         else->{}
                     }
                 }
             }
    }
    private fun setPager(list: List<SoilSampleModel>) {
        val l1 = list.filter { it.nrangName==null ||it.nrangName=="" || it.prangName==null || it.prangName=="" || it.krangName==null || it.krangName=="" || it.ocRangName==null|| it.ocRangName==""  }
        val l2 = list.filter { it.nrangName!=null && it.nrangName!="" && it.prangName!=null && it.prangName!="" && it.krangName!=null && it.krangName!="" && it.ocRangName!=null && it.ocRangName!="" }
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(SampleFragment(l1,userType.toString()), getString(R.string.collected_sample))
        adapter.addFragment(ReportFragment(l2,userType.toString()), getString(R.string.test_completed))
        viewPager.adapter = adapter
        viewPager.setCurrentItem(viewPagerPosition,true)
        tabs.setupWithViewPager(viewPager)
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                viewPagerPosition =position
            }

            override fun onPageSelected(position: Int) {
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
       /* if (resultCode == RESULT_OK) {
            soilSampleByPrimaryPhone()
        }*/
//        if(resultCode == RESULT_OK){
//            sampleDataList?.apply {
//                var updatedData=data?.getStringExtra("sampleData")?.fromJson<SoilSampleModel>()
//                val pos = indexOf(this.find { it.id == updatedData?.id })
//                showToast(updatedData?.nvalue.toString())
//                sampleDataList?.toMutableList()?.add(pos, updatedData!!)
//                showToast(sampleDataList!![pos]?.nvalue.toString())
//                sampleDataList?.let { it1 -> setPager(it1) }
//            }
//        }
    }
    companion object {
        var viewPagerPosition=0
    }
    var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }
            doubleBackToExitPressedOnce = true
            Toast.makeText(this, getString(R.string.to_exit), Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun onResume() {
        super.onResume()
        soilSampleByPrimaryPhone()
    }
}