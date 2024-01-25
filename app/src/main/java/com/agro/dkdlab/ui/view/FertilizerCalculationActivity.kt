package com.agro.dkdlab.ui.view

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.agro.dkdlab.R
import com.agro.dkdlab.app.MyApp
import com.agro.dkdlab.constant.ContextUtils
import com.agro.dkdlab.constant.getFarmingTypeNew
import com.agro.dkdlab.custom.Util.findColor
import com.agro.dkdlab.custom.Util.fromJson
import com.agro.dkdlab.custom.Util.fromListJson
import com.agro.dkdlab.custom.Util.getProgress
import com.agro.dkdlab.custom.Util.inflate
import com.agro.dkdlab.custom.Util.json
import com.agro.dkdlab.custom.Util.showAlert
import com.agro.dkdlab.network.config.Status
import com.agro.dkdlab.network.model.FertCalcManualModelNew
import com.agro.dkdlab.network.model.FertCalcModel
import com.agro.dkdlab.network.model.FertCropModel
import com.agro.dkdlab.network.model.SoilSampleModel
import com.agro.dkdlab.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_fertilizer_calculation.*
import kotlinx.android.synthetic.main.layout_fert_row_item.view.*
import java.util.*

@AndroidEntryPoint
class FertilizerCalculationActivity : AppCompatActivity() {
    //    private var selectedFarmIndex = 0
    private var farmingType = ""
    private var farmingTypeIndex = ""
    private var farmingMethod = ""

    //    private var farm: FarmDetailsModel? = null
    private var fertCalcModel: FertCalcModel? = null
    lateinit var fertCalcManualModel: FertCalcManualModelNew
    var api_call: Boolean = false
    lateinit var cropModel: List<FertCropModel>

    private var originalValueFym: Double = 8.0
    private var originalValuePsb: Double = 500.0
    private var originalValueKmb: Double = 500.0
    private var originalValueRockPhosphate: Double = 100.0
    private val viewModel by viewModels<MainViewModel>()
    lateinit var soilSampleResult: SoilSampleModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fertilizer_calculation)
        soilSampleResult = intent.getStringExtra("sampleData")!!.fromJson()
        textTitleFer.text = getString(R.string.fert_dose)
        cropModel = MyApp.get().getCropData().fromListJson()
        /*if(selectedFarm==null) { //   for demo
            var farmsDataList= MyApp.get().getDemoFarm()
            farmsDataList?.let {
                selectedFarm = it[0]
                farms.addAll(it)
            }
        }
        farm = selectedFarm*/
//        Log.e( "farmingType:","${farm!!.farmingType}")
        farmingTypeIndex = /*farm!!.farmingType?:*/ "0"
        farmingTypeIndex = if (farmingTypeIndex.isNullOrEmpty()) "0" else farmingTypeIndex
        farmingType = getFarmingTypeNew(/*farm!!.farmingType*/"MIX25")


//        mixed25(txt_organic_farming25) // for default open MIX25 always


        /*if (farms != null && farms.isNotEmpty()) {
            var i = 1
            val list = farms.map { "${i++}" }
            selectedFarmIndex = farms.indexOf(selectedFarm)
            bindFarmName(list)
        }*/
        getFertCal()
        textViewSeedDose.setOnClickListener {
            startActivity(Intent(this, SeedRecommendationActivity::class.java)/*.putExtra("farmDetails", selectedFarm?.json())*/)
        }

        txt_organic_farming25.setOnClickListener {
            txt_organic_farming25.selectFarmingType()
        }

        txt_organic_farming50.setOnClickListener {
            txt_organic_farming50.selectFarmingType()
        }
        txt_organic_farming75.setOnClickListener {
            txt_organic_farming75.selectFarmingType()
        }
        txt_inorganic_farming.setOnClickListener {
            txt_inorganic_farming.selectFarmingType()
        }
        txt_organic_farming.setOnClickListener {
            txt_organic_farming.selectFarmingType()
        }
        imgBack.setOnClickListener {
            finish()
        }
    }

    private fun clearView() {
        linearLayout1.removeAllViews()
    }

    private fun includeFertilizerView(fertCalcModel: FertCalcModel, position: Int) {
        linearLayout2.visibility = View.GONE  //always gone becouse new fert showing
        linearLayout1.visibility =
            if (farmingTypeIndex == "4") View.GONE else View.VISIBLE  // 4 means Organic

        layoutOrganicFert.visibility = if (farmingTypeIndex == "3") View.GONE else View.VISIBLE

        linearLayoutNote1.visibility =
            if (farmingTypeIndex != "3") View.VISIBLE else View.GONE // !3 means organic and mixed
        linearLayoutNote2.visibility =
            if (farmingTypeIndex != "4") View.VISIBLE else View.GONE // !4 means Inorganic and mixed
        linearLayoutNote3.visibility =
            if (farmingTypeIndex != "4") View.VISIBLE else View.GONE // !4 means Inorganic and mixed

        textLevelNote1.text = if (farmingTypeIndex == "4") "Note. " else "Note 1. "
        textLevelNote2.text = if (farmingTypeIndex == "3") "Note 1. " else "Note 2. "
        textLevelNote3.text = if (farmingTypeIndex == "3") "Note 2. " else "Note 3. "

        layout2_title.visibility = View.GONE  //always gone becouse new fert showing
//   for DAP/MOP/UREA  for first cardview
        when (position) {
            0 -> fertCalcManualModel = fertCalcModel!!.MIX25
            1 -> fertCalcManualModel = fertCalcModel!!.MIX50
            2 -> fertCalcManualModel = fertCalcModel!!.MIX75
            3 -> fertCalcManualModel = fertCalcModel!!.DAP_MOP_Urea
            4 -> fertCalcManualModel = fertCalcModel!!.ORGANIC
        }
//   end for DAP/MOP/UREA  for first cardview
        txt_formValue.text = " ${fertCalcManualModel.farmSize}"
        textViewSentenceOne.text = fertCalcManualModel.organicSentenceOne
        textViewSentenceTwo.text = fertCalcManualModel.organicSentenceTwo
        textViewSentenceThree.text = fertCalcManualModel.organicSentenceThree
        /* textViewSentenceFour.text = fertCalcManualModel.organicSentenceFour
         textViewSentenceFive.text = fertCalcManualModel.organicSentenceFive*/

        textViewFooter1.text = fertCalcManualModel.footerSentenceOne
        textViewFooter2.text = fertCalcManualModel.footerSentenceTwo
        textViewFooter3.text = fertCalcManualModel.footerSentenceThree

        val cardView1 = linearLayout1.inflate(R.layout.layout_fert_row_item)
        cardView1.txtTitle.text = fertCalcModel.DAP_MOP_Urea.title
        cardView1.txtTitle1.text = resources.getString(R.string.dap)
        cardView1.txtTitle2.text = resources.getString(R.string.mop)
        cardView1.txtTitle3.text = resources.getString(R.string.urea)
        cardView1.txt_item1_value.text =
            "${fertCalcManualModel.dap} ${resources.getString(R.string.kg_acre)}"
        cardView1.txt_item2_value.text =
            "${fertCalcManualModel.mop} ${resources.getString(R.string.kg_acre)}"
        cardView1.txt_item3_value.text =
            "${fertCalcManualModel.urea} ${resources.getString(R.string.kg_acre)}"
        cardView1.constraintLayoutClick.setOnClickListener {
            //fertCalDetails()
            startActivity(Intent(this@FertilizerCalculationActivity, FertilizerCalDetailsNewActivity::class.java)
                .putExtra("dapValue", fertCalcManualModel.dap.toString())
                .putExtra("mopValue", fertCalcManualModel.mop.toString())
                .putExtra("ureaValue", fertCalcManualModel.urea.toString())
                .putExtra("level1", resources.getString(R.string.dap))
                .putExtra("level2", resources.getString(R.string.mop))
                .putExtra("level3", resources.getString(R.string.urea))
            )
        }
        linearLayout1.addView(cardView1)

        val cardView2 = linearLayout1.inflate(R.layout.layout_fert_row_item)
        cardView2.txtTitle.text = fertCalcModel.MOP_SSP_Urea.title
        cardView2.txtTitle1.text = resources.getString(R.string.mop)
        cardView2.txtTitle2.text = resources.getString(R.string.ssp)
        cardView2.txtTitle3.text = resources.getString(R.string.urea)
        cardView2.txt_item1_value.text =
            "${fertCalcManualModel.mop} ${resources.getString(R.string.kg_acre)}"
        cardView2.txt_item2_value.text =
            "${fertCalcModel.MOP_SSP_Urea.ssp} ${resources.getString(R.string.kg_acre)}"
        cardView2.txt_item3_value.text =
            "${fertCalcManualModel.urea} ${resources.getString(R.string.kg_acre)}"
        cardView2.constraintLayoutClick.setOnClickListener {
            // fertCalDetails()
            startActivity(Intent(this@FertilizerCalculationActivity, FertilizerCalDetailsNewActivity::class.java)
                .putExtra("dapValue", fertCalcManualModel.mop.toString())
                .putExtra("mopValue", fertCalcModel.MOP_SSP_Urea.ssp.toString())
                .putExtra("ureaValue", fertCalcManualModel.urea.toString())
                .putExtra("level1", resources.getString(R.string.mop))
                .putExtra("level2", resources.getString(R.string.ssp))
                .putExtra("level3", resources.getString(R.string.urea))
            )
        }

        linearLayout1.addView(cardView2)
        val cardView3 = linearLayout1.inflate(R.layout.layout_fert_row_item)
        cardView3.txtTitle.text = fertCalcModel._102626_DAP_Urea.title
        cardView3.txtTitle1.text = resources.getString(R.string.mixFert102626)
        cardView3.txtTitle2.text = resources.getString(R.string.dap)
        cardView3.txtTitle3.text = resources.getString(R.string.urea)
        cardView3.txt_item1_value.text =
            "${fertCalcModel._102626_DAP_Urea.mop} ${resources.getString(R.string.kg_acre)}"
        cardView3.txt_item2_value.text =
            "${fertCalcManualModel.dap} ${resources.getString(R.string.kg_acre)}"
        cardView3.txt_item3_value.text =
            "${fertCalcManualModel.urea} ${resources.getString(R.string.kg_acre)}"
        //cardView3.img_next.callOnClick()
        cardView3.constraintLayoutClick.callOnClick()
        cardView3.constraintLayoutClick.setOnClickListener {
            //fertCalDetails()
            startActivity(
                Intent(this@FertilizerCalculationActivity, FertilizerCalDetailsNewActivity::class.java)
                .putExtra("dapValue", fertCalcModel._102626_DAP_Urea.mop.toString())
                .putExtra("mopValue", fertCalcManualModel.dap.toString())
                .putExtra("ureaValue", fertCalcManualModel.urea.toString())
                .putExtra("level1", resources.getString(R.string.mixFert102626))
                .putExtra("level2", resources.getString(R.string.dap))
                .putExtra("level3", resources.getString(R.string.urea))
            )
        }
        linearLayout1.addView(cardView3)
        val cardView4 = linearLayout1.inflate(R.layout.layout_fert_row_item)
        cardView4.txtTitle.text = fertCalcModel._123216_DAP_Urea.title
        cardView4.txtTitle1.text = resources.getString(R.string.mixFert123216)
        cardView4.txtTitle2.text = resources.getString(R.string.dap)
        cardView4.txtTitle3.text = resources.getString(R.string.urea)
        cardView4.txt_item1_value.text =
            "${fertCalcModel._123216_DAP_Urea.mop} ${resources.getString(R.string.kg_acre)}"
        cardView4.txt_item2_value.text =
            "${fertCalcManualModel.dap} ${resources.getString(R.string.kg_acre)}"
        cardView4.txt_item3_value.text =
            "${fertCalcManualModel.urea} ${resources.getString(R.string.kg_acre)}"
        cardView4.constraintLayoutClick.setOnClickListener {
            //fertCalDetails()
            startActivity(Intent(this@FertilizerCalculationActivity, FertilizerCalDetailsNewActivity::class.java)
                .putExtra("dapValue", fertCalcModel._123216_DAP_Urea.mop.toString())
                .putExtra("mopValue", fertCalcManualModel.dap.toString())
                .putExtra("ureaValue", fertCalcManualModel.urea.toString())
                .putExtra("level1", resources.getString(R.string.mixFert123216))
                .putExtra("level2", resources.getString(R.string.dap))
                .putExtra("level3", resources.getString(R.string.urea))
            )
        }
        linearLayout1.addView(cardView4)

        Log.e("selectedFarming:", farmingType)
        var fym = 0.0
        var psb = 0.0
        var kmb = 0.0
        var rockPhosphate = 0.0
        when (farmingType) {
            "ORGANIC" -> {
                fym = (originalValueFym * fertCalcManualModel.farmSize!!)
                psb = (originalValuePsb * fertCalcManualModel.farmSize!!)
                kmb = (originalValueKmb * fertCalcManualModel.farmSize!!)
                rockPhosphate = (originalValueRockPhosphate * fertCalcManualModel.farmSize!!)
            }

            "MIX25" -> {
                fym =
                    (originalValueFym * fertCalcManualModel.farmSize!!) * (75.0 / 100)  //  25 inorganic and 75 organic
                psb = (originalValuePsb * fertCalcManualModel.farmSize!!) * (75.0 / 100)
                kmb = (originalValueKmb * fertCalcManualModel.farmSize!!) * (75.0 / 100)
                rockPhosphate =
                    (originalValueRockPhosphate * fertCalcManualModel.farmSize!!) * (75.0 / 100)
            }

            "MIX50" -> {
                fym = (originalValueFym * fertCalcManualModel.farmSize!!) * (50.0 / 100)
                psb = (originalValuePsb * fertCalcManualModel.farmSize!!) * (50.0 / 100)
                kmb = (originalValueKmb * fertCalcManualModel.farmSize!!) * (50.0 / 100)
                rockPhosphate =
                    (originalValueRockPhosphate * fertCalcManualModel.farmSize!!) * (50.0 / 100)
            }

            "MIX75" -> {
                fym = (originalValueFym * fertCalcManualModel.farmSize!!) * (25.0 / 100)
                psb = (originalValuePsb * fertCalcManualModel.farmSize!!) * (25.0 / 100)
                kmb = (originalValueKmb * fertCalcManualModel.farmSize!!) * (25.0 / 100)
                rockPhosphate =
                    (originalValueRockPhosphate * fertCalcManualModel.farmSize!!) * (25.0 / 100)
            }

            "INORGANIC" -> {
                fym = 0.0
                psb = 0.0
                kmb = 0.0
                rockPhosphate = 0.0
            }

        }
        try {
            txtFym.text = "${" % .2f".format(fym)} ${resources.getString(R.string.ton)}"
            txtPsb.text = "${" % .2f".format(psb)} ${resources.getString(R.string.ml)}"
            txtKmb.text = "${" % .2f".format(kmb)} ${resources.getString(R.string.ml)}"
            var unit = if (rockPhosphate > 100) "${resources.getString(R.string.quintal)}" else "${
                resources.getString(R.string.kg_acre)
            }"
            if (rockPhosphate > 100) rockPhosphate /= 100
            txtRockPhosphate.text = "${" % .2f".format(rockPhosphate)} $unit"

        } catch (e: Exception) {
        }


    }

    private fun getFertCal() {
        var cropModelTemp = cropModel.find { it.cropName == textCropNameFertilizer.text.toString() }
        var cropId: String = ""
        if (cropModel.isNotEmpty()) {
            cropId = if (cropModelTemp == null) {
                textCropNameFertilizer.text =
                    cropModel.find { it.id == "61763eedda31e632d60b832a" }?.cropName // by default wheat
                "61763eedda31e632d60b832a"
            } else
                cropModel.find { it.cropName == textCropNameFertilizer.text.toString() }!!.id
        }

        val pd = getProgress()
        viewModel.storeFertelizerCalcCombination(
            userId = MyApp.get().getProfile()!!.id,
            cropId = cropId,
            farmSize = soilSampleResult?.farmSize.toString(),
            nRangName = soilSampleResult?.nrangName.toString(),
            pRangName = soilSampleResult?.prangName.toString(),
            kRangName = soilSampleResult?.krangName.toString(),
            ocRangName = soilSampleResult?.ocRangName.toString(),
            phRangName = soilSampleResult?.phRangName.toString()
        ).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        pd.dismiss()
                        if (it.data != null) {
                            try {
                                fertCalcModel = it.data
                                api_call = false
                                Log.e("fertCal", it.data!!.json())
                                emptyMessageView1.visibility = View.GONE
                                clearView()
                                if (fertCalcModel!!.MIX25 != null)
                                    includeFertilizerView(fertCalcModel!!, farmingTypeIndex.toInt())
                            } catch (e: java.lang.Exception) {
                                linearLayout1.visibility = View.GONE
                                linearLayout2.visibility = View.GONE
                                layout1_title.visibility = View.GONE
                                layout2_title.visibility = View.GONE
                                emptyMessageView1.visibility = View.VISIBLE
                            }
                        } else {
                            linearLayout1.visibility = View.GONE
                            linearLayout2.visibility = View.GONE
                            layout1_title.visibility = View.GONE
                            layout2_title.visibility = View.GONE
                            emptyMessageView1.visibility = View.VISIBLE
                        }
                    }

                    Status.ERROR -> {
                        pd.dismiss()
                        linearLayout1.visibility = View.GONE
                        linearLayout2.visibility = View.GONE
                        layout1_title.visibility = View.GONE
                        layout2_title.visibility = View.GONE
                        emptyMessageView1.visibility = View.VISIBLE
                        if (it.message == "internet") {
                            showAlert(
                                getString(R.string.network_error),
                                getString(R.string.no_internet)
                            )
                        } else {
                            showAlert(getString(R.string.error), it.message.toString())
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun changeCropFertilizer(view: View) {
        val menu = PopupMenu(this, view)
        menu.setOnMenuItemClickListener {
            txt_organic_farming25.selectFarmingType()
            updateUI(it.title.toString())
            return@setOnMenuItemClickListener true
        }

        cropModel.forEach {
            menu.menu.add(it.cropName)
        }
        menu.show()
    }

    private fun updateUI(cropName: String) {
        textCropNameFertilizer.text = cropName
        getFertCal()
    }

    fun fertCalDetails() {
        //startActivity(Intent(this@FertilizerCalculationActivity, FertilizerCalDetailsActivity::class.java))
        //startActivity(Intent(this@FertilizerCalculationActivity, FertilizerCalDetailsNewActivity::class.java))
    }

    fun back(view: View) {
        finish()
    }

    /*
        private fun bindFarmName(list: List<String>) {
            val adapter = ArrayAdapter(this, R.layout.spinner_source_layout, list)
            adapter.setDropDownViewResource(R.layout.spinner_source_layout)
            spinnerFarmName.adapter = adapter
            spinnerFarmName.setSelection(selectedFarmIndex)
            spinnerFarmName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    farm = farms[position]
                    if (farm != null) {
                        txt_organic_farming25.selectFarmingType()
                        textCropNameFertilizer.text = if(!farm!!.currentFarmCrop.equals("Other crop",ignoreCase = true))"${farm!!.currentFarmCrop}" else resources.getString(R.string.other_crop)
                        getFertCal()
                    }
                }

            }
        }
    */


    override fun attachBaseContext(newBase: Context?) {
        val locale = Locale(MyApp.get().getLocale())
        val localeUpdatedContext: ContextWrapper = ContextUtils.updateLocale(newBase!!, locale)
        super.attachBaseContext(localeUpdatedContext)
    }

    private fun View.selectFarmingType() {
        changeFarmingType(this.tag.toString().toInt())
        this.changeBG()
    }

    private fun View.changeBG() {
        layoutFarmingTypeView.children.forEach {
            (it as TextView).setBackgroundResource(R.drawable.btn_bg_unselected)
            it.setTextColor(findColor(R.color.black))
        }

        (this as TextView).setBackgroundResource(R.drawable.btn_bg_selected)
        this.setTextColor(ContextCompat.getColor(this.context, R.color.white))

    }

    private fun changeFarmingType(position: Int) {
        scrollViewVert.postDelayed({ scrollViewVert.fullScroll(ScrollView.FOCUS_UP) }, 300)
        farmingTypeIndex = "$position"
        farmingType = getFarmingTypeNew("$position")
        clearView()
        if (fertCalcModel != null) {
            if (fertCalcModel!!.MIX25 != null)
                includeFertilizerView(fertCalcModel!!, position)
            else layout1_title.visibility = View.GONE
        }
    }
}