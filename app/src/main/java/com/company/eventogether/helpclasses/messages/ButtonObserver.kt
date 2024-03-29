package com.company.eventogether.helpclasses.messages

import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import com.company.eventogether.R

class ButtonObserver(private val button: ImageView) : TextWatcher {

    init {
        button.isEnabled = false
    }

    override fun onTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {
        if (charSequence.toString().trim().isNotEmpty()) {
            button.isEnabled = true
            button.setImageResource(R.drawable.outline_send_24)
        } else {
            button.isEnabled = false
//            button.setImageResource(R.drawable.outline_send_faded_24)
        }
    }

    override fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {}
    override fun afterTextChanged(editable: Editable) {}
}
