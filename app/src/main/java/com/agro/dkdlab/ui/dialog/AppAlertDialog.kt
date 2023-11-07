package com.agro.dkdlab.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.agro.dkdlab.R
import kotlinx.android.synthetic.main.app_alert_layout.*

class AppAlertDialog(val context: Activity, val title: String, val message: String?) : Dialog(context) {
    private var onDialogDismiss: (() -> Unit)? = null
    fun setDialogDismissListener(listener: (() -> Unit)){
        onDialogDismiss = listener
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.app_alert_layout)
        textView8.text = title
        messageAlert.text = "$message"

        imgClose.setOnClickListener {
            dismiss()
            onDialogDismiss?.invoke()
        }

        btnClose.setOnClickListener {
            dismiss()
            onDialogDismiss?.invoke()
        }
    }
}