package com.agro.dkdlab.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.agro.dkdlab.R
import com.agro.dkdlab.custom.Util.findColor
import kotlinx.android.synthetic.main.sensor_alert_layout.*

class SensorAlertDialog(val context: Activity, val title: String, val message: String?) : Dialog(context) {
    private var onDialogDismiss: (() -> Unit)? = null
    fun setDialogDismissListener(listener: (() -> Unit)){
        onDialogDismiss = listener
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.sensor_alert_layout)
        txtTitle.text=title
        txtMessage.text=message
        if(title=="Issue detected"){
            txtTitle.setTextColor(ColorStateList.valueOf(context.findColor(R.color.red)))
        }else{
            txtTitle.setTextColor(ColorStateList.valueOf(context.findColor(R.color.black)))
        }
    }
}