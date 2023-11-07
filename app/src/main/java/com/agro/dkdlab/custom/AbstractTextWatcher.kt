package com.agro.dkdlab.custom

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import com.agro.dkdlab.R
import com.agro.dkdlab.custom.Util.findColor
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.validateForError(): EditText? {
    editText?.searchValue {
        this@validateForError.error = null
    }
    this.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
        this.setFocus(hasFocus)
    }
    return this.editText
}

fun TextInputLayout.setFocus(hasFocus: Boolean) {
    when{
        hasFocus ->{
            boxStrokeColor = context.findColor(R.color.color_focus_underline)
            defaultHintTextColor = ColorStateList.valueOf(
                context.findColor(R.color.text_lebel_focus)
            )
        }
        editText?.text.toString().trim().isEmpty() ->{
            boxStrokeColor = context.findColor(R.color.color_unfill_underline)
            defaultHintTextColor = ColorStateList.valueOf(
                context.findColor(R.color.text_lebel_unfill)
            )
        }
        editText?.text.toString().trim().isNotEmpty() -> {
            boxStrokeColor = context.findColor(R.color.color_fill_underline)
            defaultHintTextColor = ColorStateList.valueOf(
                context.findColor(R.color.text_lebel_fill)
            )
        }
    }
}

fun  EditText.searchValue(onTextChanged: ((String) -> Unit) ): EditText? {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }
        override fun afterTextChanged(s: Editable) {
        }
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            onTextChanged.invoke(s.toString())
        }
    })
    return this
}
@SuppressLint("ClickableViewAccessibility")
fun EditText.clearSearch() {
    setOnTouchListener(View.OnTouchListener { _, event ->
        val drwRight = 2
        if (event.action == MotionEvent.ACTION_UP) {
            if (event.rawX >= right - compoundDrawables[drwRight].bounds.width()) {
                setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_search,
                    0
                )
                setText("")
                return@OnTouchListener true
            }
        }
        false
    })
}

