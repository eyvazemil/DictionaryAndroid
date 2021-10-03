package com.example.dictionary.Frontend

import android.app.Activity
import android.view.View
import android.widget.Button
import com.example.dictionary.R

interface ButtonToolbarBack {
    fun button_toolbar_back_callback(view: View?)

    fun create_button_toolbar_back(context: Activity) {
        val button_back: Button = context.findViewById(R.id.button_hamburger_back)
        button_back.setBackgroundResource(R.drawable.ic_back)
        button_back.setOnClickListener { button_toolbar_back_callback(button_back) }
    }
}