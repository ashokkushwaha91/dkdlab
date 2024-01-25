package com.agro.dkdlab.ui.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.agro.dkdlab.R
import kotlinx.android.synthetic.main.activity_fertilizer_cal_details_new.*
import kotlinx.android.synthetic.main.appbarlayout.*

class FertilizerCalDetailsNewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fertilizer_cal_details_new)

        textTitle.text = "Fertilizer Calculation Details"
        imageViewBack.setOnClickListener {
            finish()
        }
        val dapValue = intent.getStringExtra("dapValue").toString()
        val mopValue = intent.getStringExtra("mopValue").toString()
        val ureaValue = intent.getStringExtra("ureaValue").toString()

        txt_item1_value.text = "$dapValue KG"
        txt_item2_value.text = "$mopValue KG"
        txt_item3_value.text = "${String.format("%.2f", ureaValue.toDouble()/2 )} KG"

        txt_item1.text = intent.getStringExtra("level1").toString()
        txt_item2.text = intent.getStringExtra("level2").toString()
        txt_item3.text = intent.getStringExtra("level3").toString()


        txt_item11.text = intent.getStringExtra("level1").toString()
        txt_item22.text = intent.getStringExtra("level2").toString()
        txt_item33.text = intent.getStringExtra("level3").toString()

        //txt_item1_value1.text = "-"
       // txt_item2_value1.text = "-"
        txt_item3_value1.text = "${String.format("%.2f", ureaValue.toDouble()/2 )} KG"

    }
}