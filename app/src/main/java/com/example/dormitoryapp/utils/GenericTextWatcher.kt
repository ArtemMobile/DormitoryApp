package com.example.dormitoryapp.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.example.dormitoryapp.R

class GenericTextWatcher(
    private val currentView: View,
    private val nextView: View?,
    private val onLastEditTextFilled: () -> Unit = {}
) :
    TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(editable: Editable) { // TODO Auto-generated method stub
        val text = editable.toString()
        currentView.setBackgroundResource(R.drawable.default_editor_filled)
        when (currentView.id) {
            R.id.etCode1 -> if (text.length == 1) nextView!!.requestFocus()
            R.id.etCode2 -> if (text.length == 1) nextView!!.requestFocus()
            R.id.etCode3 -> if (text.length == 1) nextView!!.requestFocus()
            R.id.etCode4 -> if (text.length == 1) onLastEditTextFilled()
        }
    }
}