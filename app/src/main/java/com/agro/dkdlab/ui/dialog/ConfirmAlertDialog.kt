package com.agro.dkdlab.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.agro.dkdlab.R
import kotlinx.android.synthetic.main.dialog_confirmation_layout.*

class ConfirmAlertDialog(val context: Activity, val title: String, val message: String) : Dialog(context) {
    private var onActionYes: (() -> Unit)? = null

    fun setDialogDismissListener(listener: (() -> Unit)) {
        onActionYes = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_confirmation_layout)
        textTitle.text = title
        messageTxt.text = message
        btnNo.setOnClickListener {
            dismiss()
        }

        btnYes.setOnClickListener {
            dismiss()
            onActionYes?.invoke()
        }
    }
}