package com.agro.dkdlab.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.agro.dkdlab.R
import com.agro.dkdlab.constant.listIrrigation
import com.agro.dkdlab.custom.Util.showToast
import com.agro.dkdlab.custom.validateForError
import com.agro.dkdlab.ui.adapter.IrrigationAdapter
import kotlinx.android.synthetic.main.irrigation_dialog_layout.*

class IrrigationDialog(val context: Activity, val list: List<String>) : Dialog(context) {
    private var onDialogDismiss: (() -> Unit)? = null
    private var itemClick: ((List<String>) -> Unit)? = null

    fun setDialogDismissListener(listener: (() -> Unit)){
        onDialogDismiss = listener
    }
    fun setItemClick(action: (List<String>) -> Unit) {
        this.itemClick = action
    }

    private val adapter = IrrigationAdapter(list.toMutableList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.irrigation_dialog_layout)
        inputLayoutOtherIrr.validateForError()
        val other = list.filterNot { listIrrigation["English"]!!.contains(it) }
        Log.e("otherList", other.toString())
        if (other.isNotEmpty()){
            inputLayoutOtherIrr.visibility = View.VISIBLE
            inputLayoutOtherIrr.editText?.setText(other.first())
            checkBoxOther.isChecked = true
        }

        txtOther.setOnClickListener {
            checkBoxOther.isChecked = !checkBoxOther.isChecked
        }

        checkBoxOther.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                inputLayoutOtherIrr.visibility = View.VISIBLE
            }else{
                inputLayoutOtherIrr.visibility = View.GONE

                val otherVal =  inputLayoutOtherIrr.editText?.text.toString()
                val l = adapter.getSelectedSources()
                if (l.contains(otherVal)){
                    l.remove(otherVal)
                }
                itemClick?.invoke(l)
                inputLayoutOtherIrr.editText?.setText("")
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        imgClose.setOnClickListener {
            onDialogDismiss?.invoke()
            dismiss()
        }

        btnClose.setOnClickListener {
            if (inputLayoutOtherIrr.visibility == View.VISIBLE && inputLayoutOtherIrr.editText?.text.toString().trim().isEmpty()){
                inputLayoutOtherIrr.error = "Please enter other irrigation source value"
                context.showToast("Please enter other irrigation source value")
            }
            else if (inputLayoutOtherIrr.visibility == View.VISIBLE){
                val otherVal =  inputLayoutOtherIrr.editText?.text.toString()
                val l = adapter.getSelectedSources()
                if (!l.contains(otherVal)){
                    l.add(otherVal)
                }
                itemClick?.invoke(l)
                dismiss()
            }
            else{
                itemClick?.invoke(adapter.getSelectedSources())
                dismiss()
            }

        }

    }
}