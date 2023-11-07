package com.agro.dkdlab.ui.view.admin.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.agro.dkdlab.R
import com.agro.dkdlab.custom.Util.capitalizeWords
import com.agro.dkdlab.custom.Util.json
import com.agro.dkdlab.custom.Util.showAlert
import com.agro.dkdlab.custom.Util.showToast
import com.agro.dkdlab.custom.clearSearch
import com.agro.dkdlab.custom.searchValue
import com.agro.dkdlab.databinding.DashboardFragmentBinding
import com.agro.dkdlab.network.config.Status
import com.agro.dkdlab.network.model.PhRangName
import com.agro.dkdlab.network.model.RangName
import com.agro.dkdlab.network.model.Village
import com.agro.dkdlab.network.tool.ErrorUtils
import com.agro.dkdlab.ui.adapter.DashboardAdapters
import com.agro.dkdlab.ui.view.admin.AdminDashboardActivity
import com.agro.dkdlab.ui.view.admin.AdminDashboardActivity.Companion.blockList
import com.agro.dkdlab.ui.view.admin.AdminDashboardActivity.Companion.districtList
import com.agro.dkdlab.ui.view.admin.AdminDashboardActivity.Companion.stateList
import com.agro.dkdlab.ui.view.admin.SampleKhasaraActivity
import com.agro.dkdlab.ui.viewmodel.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private var dashboardAdapter = DashboardAdapters()
    private lateinit var layoutManager: LinearLayoutManager
    private val adminViewModel: AdminViewModel by viewModels()
    lateinit var binding:DashboardFragmentBinding
    var stateName:String=""
    var districtName:String=""
    var blockName:String=""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DashboardFragmentBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutManager = GridLayoutManager(requireContext(), 1)
        binding.recyclerViewDashboard.layoutManager = layoutManager
        binding.recyclerViewDashboard.adapter = dashboardAdapter
        callApiMethod()
        dashboardAdapter.apply {
            setItemClick { item,action->
                when(action) {
                    "view"->{
                        startActivity(Intent(requireContext(), SampleKhasaraActivity::class.java).putExtra("villageName",item.value1))
                    }
                }
            }
            onVisibilityCheck {
               if (isVisible){
                   binding.emptyVillageList.text="No result found"
                   binding.emptyVillageList.visibility=if(dashboardAdapter.itemCount==0)View.VISIBLE else View.GONE
                   binding.recyclerViewDashboard.visibility=if(dashboardAdapter.itemCount==0)View.GONE else View.VISIBLE
               }
            }
        }

        binding.txtState.setOnClickListener {
            bindStateMenu(it)
        }
        binding.txtDistrict.setOnClickListener {
            if(stateName.isNotEmpty())
              bindDistrictMenu(it)
            else
              requireContext().showToast("Please select state first")
        }
        binding.txtBlock.setOnClickListener {
            if(districtName.isNotEmpty())
                bindBlockMenu(it)
            else
                requireContext().showToast("Please select district first")
        }

        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = true
            binding.editTextSearchVillage.setText("")
            callApiMethod()
        }

        binding.editTextSearchVillage.searchValue {
            if(binding.editTextSearchVillage.isFocused)
                dashboardAdapter.filterData(it)
        }
        binding.editTextSearchVillage.clearSearch()
    }

    private fun bindStateMenu(view:View){
        val popup = PopupMenu(requireContext(), view)
        stateList.forEach { it.name.toSortedSet() } // sorting list
        stateList?.forEach {
            popup.menu.add(it.name.capitalizeWords())
        }
        popup.setOnMenuItemClickListener { item ->
            binding.editTextSearchVillage.setText("")
            binding.txtState.text = item.title.toString()
            var state= stateList.find { it.name==item.title.toString() }
            stateName=state?.name.toString()
            districtName=""
            binding.txtDistrict.text = "Select District"
            blockName=""
            binding.txtBlock.text="Select Block"
            (requireActivity() as AdminDashboardActivity).getDistrict(state?.state_id!!)
            callApiMethod()
            true
        }
        popup.show()
    }
    private fun bindDistrictMenu(view:View){
        val popup = PopupMenu(requireContext(), view)
        districtList.forEach { it.name.toSortedSet() } // sorting list
        districtList?.forEach {
            popup.menu.add(it.name.capitalizeWords())
        }
        popup.setOnMenuItemClickListener { item ->
            binding.editTextSearchVillage.setText("")
            binding.txtDistrict.text = item.title.toString()
            var district= districtList.find { it.name==item.title.toString() }
            districtName=district?.name.toString()
            blockName=""
            binding.txtBlock.text="Select Block"
            (requireActivity() as AdminDashboardActivity).getBlock(district?.district_id!!)
            callApiMethod()
            true
        }
        popup.show()
    }
    private fun bindBlockMenu(view:View){
        val popup = PopupMenu(requireContext(), view)
        blockList.forEach { it.name.toSortedSet() } // sorting list
        blockList?.forEach {
            popup.menu.add(it.name.capitalizeWords())
        }
        popup.setOnMenuItemClickListener { item ->
            binding.editTextSearchVillage.setText("")
            binding.txtBlock.text = item.title.toString()
            blockName= blockList.find { it.name==item.title.toString() }?.name.toString()
            callApiMethod()
            true
        }
        popup.show()
    }

    private fun callApiMethod(){
        getSampleCount()
        getSoilSampleAverage()
        getVillage()
    }
     private fun getSampleCount() {
        var body=HashMap<String,String>()
        body["stateName"] = stateName
        body["districtName"] = districtName
        body["blockName"] = blockName
        Log.e( "sampleCountParam: ",body.json())
        binding.swipeRefresh.isRefreshing = true
        adminViewModel.getSampleCount(body).observe(requireActivity()) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        binding.swipeRefresh?.isRefreshing = false
                        if (it.data?.isSuccessful == true) {
                            it.data.body()?.let { data->
                                if(data.statusCode==200){
                                    if(data.dataList.isNotEmpty())
                                        binding.txtSampleCount.text = "${data.dataList[0]}"
                                }
                                else if(data.statusCode==404){
                                    binding.txtSampleCount.text = "0"
                                }
                            }
                        } else {
                            val apiError = ErrorUtils.parseError(it.data!!)
                            requireActivity().showAlert(getString(R.string.error),
                                if (it.data?.code() == 500) getString(R.string.internal_server_error) else apiError.message
                            )
                        }
                    }
                    Status.ERROR -> {
                        binding.swipeRefresh?.isRefreshing = false
                        if (it.message == "internet") {
                            requireActivity().showAlert(getString(R.string.network_error),getString(R.string.no_internet))
                        } else {
                            requireActivity().showAlert(getString(R.string.error),it?.message.toString())
                        }
                    }
                    else->{}
                }
            }

        }
    }
    private fun getSoilSampleAverage() {
        var body=HashMap<String,String>()
        body["stateName"] = stateName
        body["districtName"] = districtName
        body["blockName"] = blockName
        Log.e( "sampleAverageParam: ",body.json())
        binding.swipeRefresh.isRefreshing = true
        adminViewModel.getSoilSampleAverage(body).observe(requireActivity()) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        binding.swipeRefresh?.isRefreshing = false
                        if (it.data?.isSuccessful == true) {
                            it.data.body()?.let { data->
                                if(data.statusCode==200){
                                    if(data.dataList.isNotEmpty()) {
                                        binding.txtNValue.text = "${findMaxNPKValue(data.dataList[0].nrangName)}"
                                        binding.txtPValue.text ="${findMaxNPKValue(data.dataList[0].prangName)}"
                                        binding.txtKValue.text = "${findMaxNPKValue(data.dataList[0].krangName)}"
                                        binding.txtOCValue.text = "${findMaxNPKValue(data.dataList[0].ocRangName)}"
                                        binding.txtPHValue.text = "${findMaxPhValue(data.dataList[0].phRangName)}"
                                    }
                                }
                                else if(data.statusCode==404){
                                    binding.txtNValue.text = "--"
                                    binding.txtPValue.text ="--"
                                    binding.txtKValue.text = "--"
                                    binding.txtOCValue.text = "--"
                                    binding.txtPHValue.text = "--"
                                }
                            }
                        } else {
                            val apiError = ErrorUtils.parseError(it.data!!)
                            requireActivity().showAlert(getString(R.string.error),
                                if (it.data?.code() == 500) getString(R.string.internal_server_error) else apiError.message
                            )
                        }
                    }
                    Status.ERROR -> {
                        binding.swipeRefresh?.isRefreshing = false
                        if (it.message == "internet") {
                            requireActivity().showAlert(getString(R.string.network_error),getString(R.string.no_internet))
                        } else {
                            requireActivity().showAlert(getString(R.string.error),it?.message.toString())
                        }
                    }
                    else->{}
                }
            }

        }
    }
    private fun findMaxNPKValue(rangeObject: RangName): String {
        var listValue= mutableListOf<Int>()
        var listName= mutableListOf<String>()
        listValue.add(rangeObject.Low)
        listValue.add(rangeObject.veryLow)
        listValue.add(rangeObject.Medium)
        listValue.add(rangeObject.High)
        listValue.add(rangeObject.veryHigh)

        listName.add("Low")
        listName.add("Very Low")
        listName.add("Medium")
        listName.add("High")
        listName.add("Very High")
        var maxValue=listValue.sortedWith(compareBy { it }).last()
        var pos=listValue.indexOf(maxValue)
        return listName[pos]

    }

    private fun findMaxPhValue(rangeObject: PhRangName): String {
        var listValue= mutableListOf<Int>()
        var listName= mutableListOf<String>()
        listValue.add(rangeObject.Neutral)
        listValue.add(rangeObject.stronglyAcidic)
        listValue.add(rangeObject.moderatelyAcidic)
        listValue.add(rangeObject.acidSulphate)
        listValue.add(rangeObject.slightlyAcidic)
        listValue.add(rangeObject.highlyAcidic)
        listValue.add(rangeObject.moderatelyAlkaline)
        listValue.add(rangeObject.stronglyAlkaline)

        listName.add("Neutral")
        listName.add("Strongly Acidic")
        listName.add("Moderately Acidic")
        listName.add("Acid Sulphate")
        listName.add("Slightly Acidic")
        listName.add("Highly Acidic")
        listName.add("Moderately Alkaline")
        listName.add("Strongly Alkaline")
        var maxValue=listValue.sortedWith(compareBy { it }).last()
        var pos=listValue.indexOf(maxValue)
        return listName[pos]

    }
    private fun getVillage() {
        var body=HashMap<String,String>()
        body["stateName"] = stateName
        body["districtName"] = districtName
        body["blockName"] = blockName
        Log.e( "villageParam: ",body.json())
        binding.swipeRefresh.isRefreshing = true
        adminViewModel.getVillageList(body).observe(requireActivity()) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        binding.swipeRefresh?.isRefreshing = false
                        if (it.data?.isSuccessful == true) {
                            it.data.body()?.let { data->
                                if(data.statusCode==200){
                                    Log.e("villageData", data.json())
                                    binding.txtVillageCount.text = "${data.dataList.size}"
                                    setData(data.dataList)
                                }
                                else if(data.statusCode==404){
                                    binding.txtVillageCount.text = "0"
                                    setData(emptyList())
                                }
                            }
                        } else {
                            val apiError = ErrorUtils.parseError(it.data!!)
                            requireActivity().showAlert(getString(R.string.error),
                                if (it.data?.code() == 500) getString(R.string.internal_server_error) else apiError.message
                            )
                        }
                    }
                    Status.ERROR -> {
                        binding.swipeRefresh?.isRefreshing = false
                        if (it.message == "internet") {
                            requireActivity().showAlert(getString(R.string.network_error),getString(R.string.no_internet))
                        } else {
                            requireActivity().showAlert(getString(R.string.error),it?.message.toString())
                        }
                    }
                    else->{}
                }
            }

        }
    }
    private fun setData(list: List<Village>) {
        val sortedList = list.sortedWith(compareBy { it.value1 })
        dashboardAdapter.setAdapterData(sortedList)
        binding.emptyVillageList.text="Data not found"
        binding.emptyVillageList.visibility=if(dashboardAdapter.itemCount==0)View.VISIBLE else View.GONE
        binding.recyclerViewDashboard.visibility=if(dashboardAdapter.itemCount==0)View.GONE else View.VISIBLE

    }
}