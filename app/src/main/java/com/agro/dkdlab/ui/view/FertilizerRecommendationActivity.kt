package com.agro.dkdlab.ui.view

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.agro.dkdlab.R
import com.agro.dkdlab.custom.Util.capitalizeWords
import com.agro.dkdlab.custom.Util.fromJson
import com.agro.dkdlab.databinding.ActivityFertilizerRecommendationBinding
import com.agro.dkdlab.network.model.FarmingType
import com.agro.dkdlab.network.model.FertCalcManualModel
import com.agro.dkdlab.network.model.MixCropValue
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class FertilizerRecommendationActivity : AppCompatActivity() {
    var hashMap = TreeMap<String, String>()
    private var recommendation: FertCalcManualModel?=null
    private var farmSize:String=""
    private var recommendationValue: MixCropValue?=null
    lateinit var binding:ActivityFertilizerRecommendationBinding
    val getFarmingTypes = listOf(
        "Organic Farming",
        "Mixed 25% Organic",
        "Mixed 50% Organic",
        "Mixed 75% Organic",
        "Inorganic Farming"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityFertilizerRecommendationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.include.textTitle.text="Fertilizer Recommendation"
        binding.include.imageViewBack.setOnClickListener {
            finish()
        }

        recommendation = intent.getStringExtra("recommendation")!!.fromJson()
        farmSize = intent.getStringExtra("farmSize").toString()

        hashMap["Wheat"] = "5fd43e351290536115cf15b1"
        hashMap["Sugarcane"] = "5fd43e3c1290536115cf1602"
        hashMap["Mustard"] = "60ddb28bc856a7405cb5901d"
        hashMap["Potato"] = "5fd43e371290536115cf15c6"
        hashMap["Rice"] = "60d5d39fce3a1526d4d1ae57"
        hashMap["Gram"] = "62b40ffc26173b9d1d0d7fed"
        hashMap["Cotton"] = ""
        hashMap["Cotton Hybrid"] = ""

        bindFarmingType()
        binding.textViewChangeFilter.setOnClickListener {
            bindMenu(it)
        }
    }
    private fun bindFarmingType() {
        val adapter = ArrayAdapter(this, R.layout.spinner_source_layout, getFarmingTypes)
        adapter.setDropDownViewResource(R.layout.spinner_source_layout)
        binding.spinnerFarmingType.adapter = adapter
//        spinnerFarmingType.tag = getFarmingTypes()[4]  // by default Inorganic
        binding.spinnerFarmingType.setSelection(0)
        binding.spinnerFarmingType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long,
            ) {
                binding.spinnerFarmingType.tag = getFarmingTypes[position]
                when(getFarmingTypes[position]){
                    "Organic Farming"-> recommendationValue=recommendation?.organic?.getCrop()
                    "Mixed 25% Organic"-> recommendationValue=recommendation?.mix25?.getCrop()
                    "Mixed 50% Organic"-> recommendationValue=recommendation?.mix50?.getCrop()
                    "Mixed 75% Organic"-> recommendationValue=recommendation?.mix75?.getCrop()
                    "Inorganic Farming"-> recommendationValue=recommendation?.inOrganic?.getCrop()
                }
                setFertCalData(recommendationValue)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

    }
private fun FarmingType.getCrop(): MixCropValue? {
    var aa:MixCropValue?=null
    when(binding.textViewFilter.text.toString()) {
        "Wheat"-> aa=this.Wheat
        "Sugarcane"-> aa=this.Sugarcane
        "Mustard"-> aa=this.Mustard
        "Potato"-> aa=this.Potato
        "Rice"-> aa=this.Paddy
        "Gram"-> aa=this.Gram
        "Cotton"-> aa=this.Gram
        "Cotton Hybrid"-> aa=this.Gram
    }
    return aa
}
    private fun bindMenu(view: View) {
        val popup = PopupMenu(this, binding.linearLayout4, Gravity.RIGHT)
        hashMap.forEach { it.key.toSortedSet() }
        hashMap?.forEach {
            popup.menu.add(it.key.capitalizeWords())
        }
        popup.setOnMenuItemClickListener { item ->
            binding.textViewFilter.text = item.title.toString()
            binding.textViewFilter.tag = item.title.toString()
            when(binding.spinnerFarmingType.tag){
                "Organic Farming"-> recommendationValue=recommendation?.organic?.getCrop()
                "Mixed 25% Organic"-> recommendationValue=recommendation?.mix25?.getCrop()
                "Mixed 50% Organic"-> recommendationValue=recommendation?.mix50?.getCrop()
                "Mixed 75% Organic"-> recommendationValue=recommendation?.mix75?.getCrop()
                "Inorganic Farming"-> recommendationValue=recommendation?.inOrganic?.getCrop()
            }
            setFertCalData(recommendationValue)
            true
        }
        popup.show()
    }

    private fun setFertCalData(fertCalcManualModel: MixCropValue?) {
        binding.linearLayout1.visibility = if (binding.spinnerFarmingType.selectedItemPosition == 0) View.GONE else View.VISIBLE
        binding.linearLayout2.visibility = if (binding.spinnerFarmingType.selectedItemPosition == 0) View.VISIBLE else View.GONE
        binding.layoutOrganicFert.visibility = if (binding.spinnerFarmingType.selectedItemPosition == 4) View.GONE else View.VISIBLE

        binding.txtUrea.text = fertCalcManualModel?.urea.toString()
        binding.txtDap.text = fertCalcManualModel?.dap.toString()
        binding.txtMOP.text = fertCalcManualModel?.mop.toString()
        binding.txtSsp.text = fertCalcManualModel?.ssp.toString()
        binding.txtLandAreaAcre.text = "Farm size: $farmSize Acre"
        var rockPhosphate=fertCalcManualModel?.rp
        try{
            binding.txtFym.text = "%.2f".format(fertCalcManualModel?.fym)
            binding.txtPsb.text = "%.2f".format(fertCalcManualModel?.psb)
            binding.txtKmb.text = "%.2f".format(fertCalcManualModel?.kmb)
            /*if (rockPhosphate != null) {
                binding.txtRockPhosphateLabel.text=if(rockPhosphate>100) "Quintal" else "KG"
                if(rockPhosphate>100) rockPhosphate /= 100
            }*/ //commented by bhanu ji

            binding.txtRockPhosphate.text = "%.2f".format(rockPhosphate)

        }catch (e:Exception){}


        fertCalcManualModel?.sentences?.let {
            binding.textViewSentenceOne.text = it[0]
            binding.textViewSentenceTwo.text = it[1]
            binding.textViewSentenceThree.text = it[2]
            binding.layoutSentenceThree.visibility=if(it[2].isNullOrEmpty()) GONE else VISIBLE
        }
    }
}