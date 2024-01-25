package com.agro.dkdlab.ui.view.guest.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.agro.dkdlab.R
import com.agro.dkdlab.app.AppPermission.askLocationWithCameraPermission
import com.agro.dkdlab.app.AppPermission.enableLocationSettings
import com.agro.dkdlab.app.AppPermission.hasLocationPermission
import com.agro.dkdlab.app.AppPermission.isLocationEnabled
import com.agro.dkdlab.app.MyApp
import com.agro.dkdlab.custom.Util.json
import com.agro.dkdlab.custom.Util.showToast
import com.agro.dkdlab.custom.clearSearch
import com.agro.dkdlab.custom.searchValue
import com.agro.dkdlab.network.model.SoilSampleModel
import com.agro.dkdlab.network.model.UserModel
import com.agro.dkdlab.ui.adapter.SampleListAdapter
import com.agro.dkdlab.ui.view.guest.GuestDashboardActivity
import com.agro.dkdlab.ui.view.guest.GuestDashboardActivity.Companion.viewPagerPosition
import com.agro.dkdlab.ui.view.guest.SoilSampleActivity
import com.agro.dkdlab.ui.view.guest.UpdateSoilSampleActivity
import kotlinx.android.synthetic.main.fragment_completed.*
import kotlinx.android.synthetic.main.fragment_new_sample.*

class SampleFragment(val list: List<SoilSampleModel>, val userType: String) : Fragment() {
    private val adapter = SampleListAdapter(list, userType)
    var profile: UserModel?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_new_sample, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         profile= MyApp.get().getProfile()
         layoutNewSample.setOnClickListener {
            when{
                !requireActivity().isLocationEnabled() -> {
                    requireActivity().enableLocationSettings(432)
                }
                requireActivity().hasLocationPermission()-> {
                    createUpdateSampleLauncher.launch(Intent(activity, SoilSampleActivity::class.java))
                }
                else-> {
                    requireActivity().askLocationWithCameraPermission(200)
                }
            }

             //  bpos
//             createUpdateSampleLauncher.launch(Intent(activity, SoilSampleActivity::class.java))
         }
        checkVisibility()

        adapter.apply {
            onItemClick {
                createUpdateSampleLauncher.launch(Intent(requireActivity(), UpdateSoilSampleActivity::class.java)
                    .putExtra("sampleData",  it.json()))
            }
            onVisibilityCheck {
                emptyMessageViewReport.text="No result found"
                emptyMessageViewReport.visibility=if(adapter.itemCount==0)View.VISIBLE else View.GONE
                recyclerViewReport.visibility=if(adapter.itemCount==0)View.GONE else View.VISIBLE
            }
        }
        editSearchSample.searchValue {
            adapter.filter(it)
        }
        editSearchSample.clearSearch()

        recyclerViewSample.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewSample.adapter = adapter
        swipeRefreshSample.setOnRefreshListener {
            swipeRefreshSample.isRefreshing = false
            when(activity){
                is GuestDashboardActivity -> {
                    viewPagerPosition=0
                    (activity as GuestDashboardActivity?)?.soilSampleByPrimaryPhone()
                }
            }
        }
    }
    private fun checkVisibility(){
        var message = when(profile?.type){
            "Guest"-> "You haven't started collecting a soil sample yet"
            "Admin"-> {
                when (userType) {
                    "Guest" -> "User hasn't started soil sampling yet"
                    else -> "Record not found"
                }
            }
            else-> "Record not found"
        }
        emptyMessageViewSample.text=message
        when{
            list.isEmpty()->{
                editSearchSample.visibility= GONE
                recyclerViewSample.visibility= GONE
                emptyMessageViewSample.visibility= VISIBLE
            }
            else->{
                editSearchSample.visibility= VISIBLE
                recyclerViewSample.visibility= VISIBLE
                emptyMessageViewSample.visibility= GONE
            }
        }
    }
    /*// Receiver
    private val updateSampleLauncher =registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK && MyApp.get().getProfile()?.type=="Guest") {
            (activity as GuestDashboardActivity?)?.soilSampleByPrimaryPhone()
        }
    }*/
    private val createUpdateSampleLauncher =registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            (activity as GuestDashboardActivity?)?.soilSampleByPrimaryPhone()
        }
    }
}
