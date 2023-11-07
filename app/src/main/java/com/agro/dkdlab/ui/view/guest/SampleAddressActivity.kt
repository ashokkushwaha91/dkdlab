package com.agro.dkdlab.ui.view.guest

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.agro.dkdlab.R
import com.agro.dkdlab.custom.Util.getProgress
import com.agro.dkdlab.custom.Util.showAlert
import com.agro.dkdlab.custom.Util.showToast
import com.agro.dkdlab.custom.validateForError
import com.agro.dkdlab.databinding.ActivitySampleAddressBinding
import com.agro.dkdlab.network.config.Status
import com.agro.dkdlab.network.model.Address
import com.agro.dkdlab.network.tool.ErrorUtils
import com.agro.dkdlab.ui.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.appbarlayout.*

@AndroidEntryPoint
class SampleAddressActivity : AppCompatActivity() {
    lateinit var binding:ActivitySampleAddressBinding
    private val addressViewModel:AddressViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySampleAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        textTitle.text = "Sample Location"
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

       /* bindDistrict()
        bindBlock()*/

        imageViewBack.setOnClickListener {
            finish()
        }

        binding.inputLayoutVillage.validateForError()
        binding.inputLayoutPicCode.validateForError()

        binding.btnCollectSample.setOnClickListener {
//            Log.e("onCreate: ",binding.spinnerState.tag.toString() )
            validateForm()
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
        when {
            binding.spinnerState.selectedItem.toString().isEmpty() ->  showToast(getString(R.string.please_select_state))
            binding.spinnerDistrict.selectedItem.toString().isEmpty() ->  showToast(getString(R.string.please_select_district))
            binding.spinnerBlock.selectedItem.toString().isEmpty()->  showToast(getString(R.string.please_select_block))
            binding.inputLayoutVillage.editText?.text.toString().trim().isEmpty() -> {
                binding.inputLayoutVillage.editText?.requestFocus()
                binding.inputLayoutVillage.error = resources.getString(R.string.enter_village_name)
            }
            binding.inputLayoutPicCode.editText?.text.toString().trim().isEmpty() -> {
                binding.inputLayoutPicCode.editText?.requestFocus()
                binding.inputLayoutPicCode.error = resources.getString(R.string.enter_pincode)
            }
            binding.inputLayoutPicCode.editText?.text.toString().trim().length < 6 -> {
                binding.inputLayoutPicCode.editText?.requestFocus()
                binding.inputLayoutPicCode.error = getString(R.string.enter_correct_pincode)
            }

            else -> {
                val intent = Intent()
                intent.putExtra("state",binding.spinnerState.tag.toString())
                intent.putExtra("district",binding.spinnerDistrict.tag.toString())
                intent.putExtra("block",binding.spinnerBlock.tag.toString())
                intent.putExtra("village",binding.inputLayoutVillage.editText?.text.toString().trim())
                intent.putExtra("pincode",binding.inputLayoutPicCode.editText?.text.toString().trim())
                setResult(RESULT_OK, intent)
                finish()
            }
        }

    }
}