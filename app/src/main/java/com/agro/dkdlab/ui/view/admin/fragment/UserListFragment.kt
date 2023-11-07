package com.agro.dkdlab.ui.view.admin.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.agro.dkdlab.R
import com.agro.dkdlab.custom.Util.capitalizeWords
import com.agro.dkdlab.custom.Util.showAlert
import com.agro.dkdlab.custom.Util.showToast
import com.agro.dkdlab.custom.clearSearch
import com.agro.dkdlab.custom.searchValue
import com.agro.dkdlab.databinding.UsersFragmentBinding
import com.agro.dkdlab.network.config.Status
import com.agro.dkdlab.network.model.UserListModel
import com.agro.dkdlab.network.tool.ErrorUtils
import com.agro.dkdlab.ui.adapter.UserAdapter
import com.agro.dkdlab.ui.view.admin.AdminDashboardActivity
import com.agro.dkdlab.ui.view.admin.AdminDashboardActivity.Companion.blockList
import com.agro.dkdlab.ui.view.admin.AdminDashboardActivity.Companion.districtList
import com.agro.dkdlab.ui.view.admin.AdminDashboardActivity.Companion.stateList
import com.agro.dkdlab.ui.view.login.RegisterUserActivity
import com.agro.dkdlab.ui.viewmodel.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserListFragment : Fragment() {
    private var userAdapter=UserAdapter()
    private val adminViewModel: AdminViewModel by viewModels()
    var stateName:String=""
    var districtName:String=""
    var blockName:String=""
    lateinit var binding:UsersFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= UsersFragmentBinding.inflate(layoutInflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewUserList.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewUserList.adapter = userAdapter
        getUserList()
        userAdapter.apply {
            onVisibilityCheck {
                if (isVisible){
                    binding.emptyUserList.text="No result found"
                    binding.emptyUserList.visibility=if(userAdapter.itemCount==0)View.VISIBLE else GONE
                    binding.recyclerViewUserList.visibility=if(userAdapter.itemCount==0)View.GONE else VISIBLE
                }
            }
        }
        binding.txtAddNewUser.setOnClickListener {
            addUserLauncher.launch(Intent(requireContext(), RegisterUserActivity::class.java))
        }

        binding.editTextSearch.searchValue {
            if(binding.editTextSearch.isFocused)
              userAdapter.filterData(it)
        }
        binding.editTextSearch.clearSearch()

        binding.swipeRefresh.setOnRefreshListener {
            binding.editTextSearch.setText("")
            getUserList()
        }

        binding.txtState.setOnClickListener {
            /*bindStateMenu(it)*/
            requireContext().showToast("Development mode")
        }
        binding.txtDistrict.setOnClickListener {
           /* if(stateName.isNotEmpty())
                bindDistrictMenu(it)
            else
                requireContext().showToast("Please select state first")*/
            requireContext().showToast("Development mode")
        }
        binding.txtBlock.setOnClickListener {
            /*if(districtName.isNotEmpty())
                bindBlockMenu(it)
            else
                requireContext().showToast("Please select district first")*/
            requireContext().showToast("Development mode")
        }

    }
    private fun bindStateMenu(view:View){
        val popup = PopupMenu(requireContext(), view)
        stateList.forEach { it.name.toSortedSet() } // sorting list
        stateList?.forEach {
            popup.menu.add(it.name.capitalizeWords())
        }
        popup.setOnMenuItemClickListener { item ->
            binding.editTextSearch.setText("")
            binding.txtState.text = item.title.toString()
            var state= stateList.find { it.name==item.title.toString() }
            stateName=state?.name.toString()
            districtName=""
            binding.txtDistrict.text = "Select District"
            blockName=""
            binding.txtBlock.text="Select Block"
            (requireActivity() as AdminDashboardActivity).getDistrict(state?.state_id!!)
            getUserList()
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
            binding.editTextSearch.setText("")
            binding.txtDistrict.text = item.title.toString()
            var district= districtList.find { it.name==item.title.toString() }
            districtName=district?.name.toString()
            blockName=""
            binding.txtBlock.text="Select Block"
            (requireActivity() as AdminDashboardActivity).getBlock(district?.district_id!!)
            getUserList()
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
            binding.editTextSearch.setText("")
            binding.txtBlock.text = item.title.toString()
            blockName= blockList.find { it.name==item.title.toString() }?.name.toString()
            getUserList()
            true
        }
        popup.show()
    }

     private fun getUserList() {
         binding.swipeRefresh?.isRefreshing = true
         binding.editTextSearch.setText("")
        adminViewModel.getUsers().observe(requireActivity()) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        binding.swipeRefresh.isRefreshing = false
                        if (it.data?.isSuccessful == true) {
                            it.data.body()?.let { userList->
                                var userFilteredList=userList.filter { user->user.userType=="Guest" }
                                setListData(userFilteredList)
                               }
                        } else {
                            val apiError = ErrorUtils.parseError(it.data!!)
                            requireActivity().showAlert(getString(R.string.error),apiError.message)
                        }
                    }
                    Status.ERROR -> {
                        binding.swipeRefresh.isRefreshing = false
                        if (it.message == "internet") {
                            requireActivity().showAlert(getString(R.string.network_error), getString(R.string.no_internet))
                        } else {
                            requireActivity().showAlert(getString(R.string.error),it?.message.toString())
                        }
                    }
                    else->{}
                }
            }
        }
     }

    private fun setListData(list: List<UserListModel>) {
        binding.textVerifyUser.text = "Verified User: ${list.filter { it.otpVerified }.size}"
        binding.textUnVerifyUser.text = "Unverified users: ${list.filter { !it.otpVerified }.size}"
        binding.emptyUserList.text ="It seems to you haven't created user yet, Please click + icon for adding new user"
        binding.emptyUserList.visibility =if (list.isEmpty()) VISIBLE else GONE
        binding.recyclerViewUserList.visibility =if (list.isEmpty()) GONE else VISIBLE

        val sortedList = list.sortedWith(compareBy { it.userName })
        userAdapter.setAdapterData(sortedList)
    }
    // Receiver
    private val addUserLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode== Activity.RESULT_OK)
              getUserList()
        }
}