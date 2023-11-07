package com.agro.dkdlab.ui.view

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.agro.dkdlab.BuildConfig
import com.agro.dkdlab.app.MyApp
import com.agro.dkdlab.custom.Util.showToast
import com.agro.dkdlab.databinding.ActivitySplashBinding
import com.agro.dkdlab.network.config.UrlConfig
import com.agro.dkdlab.network.config.UrlConfig.BASE_URL
import com.agro.dkdlab.ui.view.admin.AdminDashboardActivity
import com.agro.dkdlab.ui.view.guest.GuestDashboardActivity
import com.agro.dkdlab.ui.view.guest.SampleAddressActivity
import com.agro.dkdlab.ui.view.login.GuestLoginActivity
import com.agro.dkdlab.ui.view.offline.DashboardActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        var version= if(BASE_URL.contains("182.18.155.172")) "Production version" else "Testing version"
        var version= "Production version"
        binding.txtVersion.text ="$version: ${BuildConfig.VERSION_NAME}"
        nextScreen()
    }
    private fun nextScreen()
    {
        Handler(Looper.getMainLooper()).postDelayed({
            var userRole=MyApp.get().getRole()
            when{
                userRole.isNullOrEmpty()->{
                    startActivity(Intent(this, GuestLoginActivity::class.java))
                }
                userRole.isNotEmpty() && userRole=="Offline"->{
                    startActivity(Intent(this, DashboardActivity::class.java))
                }
                userRole.isNotEmpty() && userRole=="dkd5-Admin" || userRole.lowercase()=="admin"->{
                    startActivity(Intent(this, AdminDashboardActivity::class.java))
                }
                 userRole.isNotEmpty() && userRole=="Guest"->{
                    startActivity(Intent(this, GuestDashboardActivity::class.java))
                }
                else ->{
                    showToast("User role not found")
                }/*startActivity(Intent(this, GuestDashboardActivity::class.java))*/
            }
            this.finish()
        },3000)
    }
    companion object {
        var DEVICEID = ""
    }
}