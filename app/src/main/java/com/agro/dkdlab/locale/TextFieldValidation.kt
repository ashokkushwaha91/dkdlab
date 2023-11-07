package com.agro.dkdlab.locale

import android.text.Editable
import android.text.TextWatcher
import android.view.View

class TextFieldValidation(private val view: View) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

}