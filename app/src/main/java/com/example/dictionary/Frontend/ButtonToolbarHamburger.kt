package com.example.dictionary.Frontend

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.widget.Button
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.dictionary.R

interface ButtonToolbarHamburger {
    fun create_button_toolbar_hamburger(context: Activity) {
        val button_hamburger: Button = context.findViewById(R.id.button_hamburger_back)
        button_hamburger.setBackgroundResource(R.drawable.ic_hamburger)
        button_hamburger.setOnClickListener { button_toolbar_hamburger_callback(context) }
    }

    fun button_toolbar_hamburger_callback(context: Activity) {
        val drawer_layout: DrawerLayout = context.findViewById(R.id.drawer_layout)
        drawer_layout.openDrawer(GravityCompat.START)
    }
}