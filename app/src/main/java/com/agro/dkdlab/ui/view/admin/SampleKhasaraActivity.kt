package com.agro.dkdlab.ui.view.admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.agro.dkdlab.R
import com.agro.dkdlab.custom.Util.capitalizeWords
import com.agro.dkdlab.custom.Util.json
import com.agro.dkdlab.custom.Util.showAlert
import com.agro.dkdlab.custom.clearSearch
import com.agro.dkdlab.custom.searchValue
import com.agro.dkdlab.databinding.ActivitySampleKhasaraBinding
import com.agro.dkdlab.network.config.Status
import com.agro.dkdlab.network.model.SampleList
import com.agro.dkdlab.network.tool.ErrorUtils
import com.agro.dkdlab.ui.adapter.KhasaraAdapter
import com.agro.dkdlab.ui.view.guest.ReportDetailsActivity
import com.agro.dkdlab.ui.viewmodel.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.appbarlayout.*

@AndroidEntryPoint
class SampleKhasaraActivity : AppCompatActivity() {
    private val adapter = KhasaraAdapter()
    private var listCompletedSample= listOf<SampleList>()
    private var villageName:String=""
    lateinit var binding:ActivitySampleKhasaraBinding
    private val adminViewModel: AdminViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySampleKhasaraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        villageName = intent.getStringExtra("villageName").toString()
        textTitle.text = villageName.capitalizeWords()
        val layoutManager = GridLayoutManager(this, 3)
        binding.recyclerViewKhasaraList.layoutManager = layoutManager
        binding.recyclerViewKhasaraList.adapter = adapter


        binding.swipeRefreshKh.setOnRefreshListener {
            soilSampleKhasraStatusVillageWise()
        }
        soilSampleKhasraStatusVillageWise()

        imageViewBack.setOnClickListener {
            finish()
        }

        binding.editTextSearchKhasra.clearSearch()
        binding.editTextSearchKhasra.searchValue {
            adapter.filterList(it)
        }
        adapter.apply {
            onVisibilityCheck {
                binding.emptyKhasraList.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
                binding.recyclerViewKhasaraList.visibility =
                    if (adapter.itemCount == 0) View.GONE else View.VISIBLE
            }
            onItemClick {
                startActivity(Intent(this@SampleKhasaraActivity, ReportDetailsActivity::class.java)
                .putExtra("sampleData",  it.json()))
            }
        }

    }

    private fun soilSampleKhasraStatusVillageWise() {
        binding.swipeRefreshKh.isRefreshing = true
        adminViewModel.soilSampleByVillageName(villageName).observe(this) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            binding.swipeRefreshKh.isRefreshing = false
                            if (it.data?.isSuccessful == true) {
                                it.data.body()?.let { data->
                                    setListData(data)
                                }
                            } else {
                                val apiError = ErrorUtils.parseError(it.data!!)
                                showAlert(getString(R.string.error),
                                    if (it.data?.code() == 500) getString(R.string.internal_server_error) else apiError.message
                                )
                            }
                        }
                        Status.ERROR -> {
                            binding.swipeRefreshKh.isRefreshing = false
                            if (it.message == "internet") {
                                showAlert(
                                    getString(R.string.network_error),
                                    getString(R.string.no_internet)
                                )
                            } else {
                                showAlert(getString(R.string.error), it?.message.toString())
                            }
                        }
                        else->{}
                    }
                }

            }
    }
    private fun setListData(list: List<SampleList>) {
         /*listCompletedSample=list.filter { it.nrangName.isNotEmpty() }
        var listInCompletedSample=list.filter { it.nrangName.isEmpty() }
        var listOther=list.filter { it.status=="" }
        var soilSampleList= mutableListOf<SampleList>()
        var finalList= mutableListOf<SampleList>()
        finalList.addAll(listCompletedSample)
        finalList.addAll(listInCompletedSample)
        finalList.addAll(listOther)
        //  for all selection
        soilSampleList.addAll(listCompletedSample)
        soilSampleList.addAll(listInCompletedSample)*/
        adapter.setList(list)
    }
}