package com.agro.dkdlab.ui.view.offline

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.agro.dkdlab.R
import com.agro.dkdlab.app.MyApp
import com.agro.dkdlab.custom.Util.capitalizeWords
import com.agro.dkdlab.custom.Util.hideKeyboard
import com.agro.dkdlab.network.model.SoilSampleModel
import com.agro.dkdlab.ui.adapter.ViewPagerAdapter
import com.agro.dkdlab.ui.dialog.ConfirmAlertDialog
import com.agro.dkdlab.ui.view.guest.ProfileActivity
import com.agro.dkdlab.ui.view.guest.fragment.ReportFragment
import com.agro.dkdlab.ui.view.guest.fragment.SampleFragment
import com.agro.dkdlab.ui.view.login.GuestLoginActivity
import com.agro.dkdlab.ui.viewmodel.ReportViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_guest_dashboard.*
import kotlinx.android.synthetic.main.appbarlayout.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {
    private var userType:String?=null
    private var primaryPhone:String?=null
    var sampleDataList:List<SoilSampleModel>? =null
    private val viewModel: ReportViewModel by viewModels()
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

        getSoilSampleRecord()

        textViewProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        textViewLogout.setOnClickListener {
            ConfirmAlertDialog(this,
                getString(R.string.logout_title),
                getString(R.string.logout_message)
            ).apply {
                setDialogDismissListener {
                    MyApp.get().logout()
                    startActivity(Intent(this@DashboardActivity, GuestLoginActivity::class.java))
                    finishAffinity()
                }
            }.show()
        }
    }
    private fun getSoilSampleRecord() {
        lifecycleScope.launch {
            viewModel.getSoilSampleRecordNew().observe(this@DashboardActivity) { sampleData ->
                Log.e("sampleData", "$sampleData")
                setPager(sampleData)
            }
        }
    }
    private fun setPager(list: List<SoilSampleModel>) {
        val l1 = list.filter { it.nrangName==null || it.prangName==null || it.krangName==null || it.ocRangName==null  }
        val l2 = list.filter { it.nrangName!=null && it.prangName!=null && it.krangName!=null && it.ocRangName!=null }
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
}