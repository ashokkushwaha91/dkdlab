package com.agro.dkdlab.ui.view

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.agro.dkdlab.R
import com.agro.dkdlab.app.MyApp
import com.agro.dkdlab.constant.ContextUtils
import com.agro.dkdlab.custom.Util.fromListJson
import com.agro.dkdlab.custom.Util.hideKeyboard
import com.agro.dkdlab.custom.Util.json
import com.agro.dkdlab.custom.clearSearch
import com.agro.dkdlab.custom.searchValue
import com.agro.dkdlab.network.model.FertCropModel
import com.agro.dkdlab.ui.adapter.CropListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_crop_list.*
import kotlinx.android.synthetic.main.appbarlayout.*
import java.util.*

@AndroidEntryPoint
class CropListActivity : AppCompatActivity() {
    private lateinit var cropAdapter: CropListAdapter
    var cropModel: List<FertCropModel>?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_list)

        textTitle.text = getString(R.string.crop_list)
        hideKeyboard()
        cropAdapter = CropListAdapter().apply {
            setItemClick {
                val intent = Intent()
                intent.putExtra("cropData", it.json())
                setResult(RESULT_OK, intent)
                finish()
            }
            onVisibilityCheck {
                emptyCropList.visibility=if(cropAdapter.itemCount==0)View.VISIBLE else View.GONE
                recyclerViewCrop.visibility=if(cropAdapter.itemCount==0)View.GONE else View.VISIBLE
            }
        }

        recyclerViewCrop.layoutManager = LinearLayoutManager(this)
        recyclerViewCrop.adapter = cropAdapter

        editTextSearch.searchValue {
            cropAdapter.filter.filter(it)
        }

        editTextSearch.clearSearch()

        getCropData()

    }

    private fun getCropData() {
        cropModel = intent.getStringExtra("cropList")!!.fromListJson()
        cropModel?.let {cropList->
            var sortedCropList= cropList.sortedBy { it.cropName }
            cropAdapter.setVillageData(sortedCropList)
        }
    }
    fun back(view:View)
    {
        finish()
    }
    override fun attachBaseContext(newBase: Context?) {
        val locale = Locale(MyApp.get().getLocale())
        val localeUpdatedContext: ContextWrapper = ContextUtils.updateLocale(newBase!!, locale)
        super.attachBaseContext(localeUpdatedContext)
    }
}