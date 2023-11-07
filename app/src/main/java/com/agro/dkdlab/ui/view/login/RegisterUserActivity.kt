package com.agro.dkdlab.ui.view.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.agro.dkdlab.R
import com.agro.dkdlab.custom.Util.getProgress
import com.agro.dkdlab.custom.Util.isValidMobile
import com.agro.dkdlab.custom.Util.json
import com.agro.dkdlab.custom.Util.showAlert
import com.agro.dkdlab.custom.Util.showToast
import com.agro.dkdlab.custom.validateForError
import com.agro.dkdlab.databinding.ActivityRegisterUserBinding
import com.agro.dkdlab.network.config.Status
import com.agro.dkdlab.network.model.Address
import com.agro.dkdlab.network.tool.ErrorUtils
import com.agro.dkdlab.ui.viewmodel.AddressViewModel
import com.agro.dkdlab.ui.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterUserActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    lateinit var binding: ActivityRegisterUserBinding
    private val addressViewModel: AddressViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegisterUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.include.textTitle.text = getString(R.string.add_user)
        binding.inputLayoutUserName.validateForError()
        binding.inputLayoutMobile.validateForError()
        binding.inputLayoutVillage.validateForError()

        binding.buttonRegister.setOnClickListener {
            validateForm()
        }
        binding.include.imageViewBack.setOnClickListener {
            finish()
        }
        getState()
    }
    private fun bindState(state: List<Address>) {
        val adapter = ArrayAdapter(this, R.layout.spinner_source_layout, state.map { it.name })
        adapter.setDropDownViewResource(R.layout.spinner_source_layout)
        binding.spinnerState.adapter = adapter
        binding.spinnerState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.spinnerState.tag=state[position].name
                getDistrict(state[position].state_id.toString())
            }
        }
    }
    private fun bindDistrict(district: List<Address>) {
        val list: List<String> = district.map { it.name }
        val adapter = ArrayAdapter(this, R.layout.spinner_source_layout,list)
        adapter.setDropDownViewResource(R.layout.spinner_source_layout)
        binding.spinnerDistrict.adapter = adapter
        binding.spinnerDistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.spinnerDistrict.tag=district[position].name
                getBlock(district[position].district_id.toString())
            }
        }
    }
    private fun bindBlock(block: List<Address>) {
        val adapter = ArrayAdapter(this, R.layout.spinner_source_layout, block.map { it.name })
        adapter.setDropDownViewResource(R.layout.spinner_source_layout)
        binding.spinnerBlock.adapter = adapter
        binding.spinnerBlock.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.spinnerBlock.tag=block[position].name

            }
        }
    }
    private fun getState() {
        var pd=getProgress()
        addressViewModel.getStates().observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        pd.dismiss()
                        if (it.data?.isSuccessful == true && it.data.body() != null) {
                            Log.e("state:", it.data.body().toString())
                            bindState(it.data.body()!!)
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
    fun getDistrict(stateId:String) {
        var pd=getProgress()
        addressViewModel.getDistricts(stateId).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        pd.dismiss()
                        if (it.data?.isSuccessful == true && it.data.body() != null) {
                            Log.e("district:", it.data.body().toString())
                            bindDistrict(it.data.body()!!)
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
    fun getBlock(districtId:String) {
        var pd=getProgress()
        addressViewModel.getBlock(districtId).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        pd.dismiss()
                        if (it.data?.isSuccessful == true && it.data.body() != null) {
                            Log.e("block:", it.data.body().toString())
                            bindBlock( it.data.body()!!)
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
    private fun validateForm() {
        when{
            binding.editTextUserName.text.toString().isEmpty() ->{
                binding.inputLayoutUserName.requestFocus()
                binding.inputLayoutUserName.error = resources.getString(R.string.name)
            }
            binding.editTextMobile.text.toString().trim().isEmpty() -> {
                binding.inputLayoutMobile.editText?.requestFocus()
                binding.inputLayoutMobile.error = resources.getString(R.string.enter_mobile_number)
            }

            binding.editTextMobile.text.toString().trim().length < 10 -> {
                binding.inputLayoutMobile.editText?.requestFocus()
                binding.inputLayoutMobile.error = getString(R.string.enter_10_digit_mobile_number)
            }

            binding.editTextMobile.text.toString().trim().isNotBlank() && !isValidMobile(binding.editTextMobile.text.toString()) -> {
                binding.inputLayoutMobile.editText?.requestFocus()
                binding.inputLayoutMobile.error = resources.getString(R.string.please_enter_valid_mobile_number)
            }
            binding.editTextRole.text.toString().isEmpty() -> {
                binding.inputLayoutRole.editText?.requestFocus()
                binding.inputLayoutRole.error = resources.getString(R.string.enter_role)
            }
            binding.spinnerState.selectedItem.toString().isEmpty() ->  showToast(getString(R.string.please_select_state))
            binding.spinnerDistrict.selectedItem.toString().isEmpty() ->  showToast(getString(R.string.please_select_district))
            binding.spinnerBlock.selectedItem.toString().isEmpty()->  showToast(getString(R.string.please_select_block))
            binding.editTextVillage.text.toString().isEmpty()-> {
                binding.inputLayoutVillage.requestFocus()
                binding.inputLayoutVillage.error = resources.getString(R.string.enter_village_name)
            }
            else ->
                createUser()
        }

    }
    private fun createUser() {
        val pd = getProgress()
        val guestUserDTO = HashMap<String, String>()
        guestUserDTO["stateName"] = binding.spinnerState.tag.toString()
        guestUserDTO["districtName"] = binding.spinnerDistrict.tag.toString()
        guestUserDTO["blockName"] =binding.spinnerBlock.tag.toString()
        guestUserDTO["villageName"] =binding.editTextVillage.text.toString()

        val body = HashMap<String, Any>()
        body["dkdUserAgentAddressRequestDTO"] = guestUserDTO
        body["name"] = binding.editTextUserName.text.toString()
        body["primaryPhone"] = binding.editTextMobile.text.toString()
        body["type"] =binding.editTextRole.text.toString()/*"Offline"*/
        Log.e("createUserParams", body.json())

        loginViewModel.registerUser(body).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        pd.dismiss()
                        if (it.data?.isSuccessful == true && it.data.body() != null) {
                            Log.d("soilSampleResponse:", it.data.body()!!.toString())
                            if (it.data.body()!!.status) {
                                it.data.body()?.id?.let { it1 -> createUserAddress(it1) }
                            }else showToast(it.data.body()?.message.toString())
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
    private fun createUserAddress(id: String) {
        val pd = getProgress()
        val userAddress = HashMap<String, String>()
        userAddress["userId"] = id
        userAddress["stateName"] = binding.spinnerState.tag.toString()
        userAddress["districtName"] = binding.spinnerDistrict.tag.toString()
        userAddress["blockName"] =binding.spinnerBlock.tag.toString()
        userAddress["villageName"] =binding.editTextVillage.text.toString()
        Log.e("createUserAddress", userAddress.json())

        loginViewModel.registerUserAddress(userAddress).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        pd.dismiss()
                        if (it.data?.isSuccessful == true && it.data.body() != null) {
                            showToast(getString(R.string.user_successfully_register))
                            val intent = Intent()
                            setResult(RESULT_OK, intent)
                            finish()
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
}