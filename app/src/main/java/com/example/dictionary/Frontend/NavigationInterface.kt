package com.example.dictionary.Frontend

import android.app.Activity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.dictionary.Backend.CloudFirestore
import com.example.dictionary.R
import com.google.android.material.navigation.NavigationView

interface NavigationInterface {
    val menu_layout_id: Int
    val menu_items_values: Map<Int, String>

    fun menu_item_callback(menu_item: MenuItem): Boolean

    fun create_navigation(context: Activity) {
        val navigation: NavigationView = context.findViewById(R.id.navigation)

        // set header view for navigation drawer
        val view_header: View = navigation.inflateHeaderView(R.layout.navigation_user)

        // set user image
        val view_user_img: ImageView = view_header.findViewById(R.id.img_user)
        Glide.with(context).load(CloudFirestore.get_user_img_url()).centerCrop().circleCrop().into(view_user_img)

        // set user name
        val text_view_user_name: TextView = view_header.findViewById(R.id.textView_user_name)
        text_view_user_name.text = CloudFirestore.get_user_name()

        // set user email
        val text_view_user_email: TextView = view_header.findViewById(R.id.textView_user_email)
        text_view_user_email.text = CloudFirestore.get_user_email()

        // set navigation drawer menu
        navigation.inflateMenu(menu_layout_id)

        update_navigation_menu(context)

        navigation.setNavigationItemSelectedListener { menu_item ->
            menu_item_callback(menu_item)
        }
    }

    fun update_navigation_menu(context: Activity) {
        val navigation: NavigationView = context.findViewById(R.id.navigation)

        menu_items_values.forEach {
            navigation.menu.findItem(it.key).title = it.value
        }
    }
}