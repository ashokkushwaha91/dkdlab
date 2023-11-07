package com.agro.dkdlab.ui.view.admin

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.agro.dkdlab.R
import com.agro.dkdlab.app.MyApp
import com.agro.dkdlab.constant.ContextUtils
import com.agro.dkdlab.custom.Util.getProgress
import com.agro.dkdlab.custom.Util.showAlert
import com.agro.dkdlab.databinding.ActivityAdminDashboardBinding
import com.agro.dkdlab.network.config.Status
import com.agro.dkdlab.network.model.Address
import com.agro.dkdlab.network.tool.ErrorUtils
import com.agro.dkdlab.ui.view.admin.fragment.DashboardFragment
import com.agro.dkdlab.ui.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class AdminDashboardActivity : AppCompatActivity() {
 lateinit var binding:ActivityAdminDashboardBinding
 private val addressViewModel: AddressViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getState()
        val navController = findNavController(R.id.fragment)
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun getState() {
        addressViewModel.getStates().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data?.isSuccessful == true && it.data.body() != null) {
                        Log.e("state:", it.data.body().toString())
                        stateList=it.data.body()!!.toMutableList()
                        /*if(it.data.body()!!.isNotEmpty())
                         getDistrict(it.data.body()!![0].state_id.toString())*/
                    } else {
                        val apiError = ErrorUtils.parseError(it.data!!)
                        showAlert(
                            getString(R.string.error),
                            if (it.data.code() == 500) getString(R.string.internal_server_error) else apiError.message
                        )
                    }
                }
                Status.ERROR -> {
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

    fun getDistrict(stateId:String) {
        addressViewModel.getDistricts(stateId).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data?.isSuccessful == true && it.data.body() != null) {
                        Log.e("district:", it.data.body().toString())
                        districtList=it.data.body()!!.toMutableList()
                        /*if(it.data.body()!!.isNotEmpty())
                            getBlock(it.data.body()!![0].district_id.toString())*/
                    } else {
                        val apiError = ErrorUtils.parseError(it.data!!)
                        showAlert(
                            getString(R.string.error),
                            if (it.data.code() == 500) getString(R.string.internal_server_error) else apiError.message
                        )
                    }
                }
                Status.ERROR -> {
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
    fun getBlock(districtId:String) {
        addressViewModel.getBlock(districtId).observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data?.isSuccessful == true && it.data.body() != null) {
                        Log.e("block:", it.data.body().toString())
                        blockList=it.data.body()!!.toMutableList()
                    } else {
                        val apiError = ErrorUtils.parseError(it.data!!)
                        showAlert(
                            getString(R.string.error),
                            if (it.data.code() == 500) getString(R.string.internal_server_error) else apiError.message
                        )
                    }
                }
                Status.ERROR -> {
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
    companion object{
        var stateList= mutableListOf<Address>()
        var districtList= mutableListOf<Address>()
        var blockList= mutableListOf<Address>()
    }
    override fun attachBaseContext(newBase: Context?) {
        val locale = Locale(MyApp.get().getLocale())
        val localeUpdatedContext: ContextWrapper = ContextUtils.updateLocale(newBase!!, locale)
        super.attachBaseContext(localeUpdatedContext)
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