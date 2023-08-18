package com.example.dormitoryapp.utils

import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import com.example.dormitoryapp.R

class GenericKeyEvent(private val currentView: EditText, private val previousView: EditText?) :
    View.OnKeyListener {
    override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.etCode1 && currentView.text.isEmpty()) {
            previousView!!.text = null
            previousView.requestFocus()
            previousView.setBackgroundResource(R.drawable.default_editor_empty)
            return true
        }
        return false
    }
}