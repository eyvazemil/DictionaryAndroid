package com.example.dictionary

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.text.InputType
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.Gravity.START
import android.view.View.inflate
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.GravityCompat
import androidx.core.view.children
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.dictionary.Backend.CloudFirestore
import com.example.dictionary.Frontend.DialogAdd
import com.google.android.material.navigation.NavigationView

@RequiresApi(Build.VERSION_CODES.M)
abstract class ActivityInterface : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var button_add: Button
    lateinit var scroll_view: LinearLayout
    lateinit var navigation: NavigationView
    private lateinit var drawer_layout: DrawerLayout

    private companion object {
        val TAG = "ActivityInterface"
    }

    abstract val menu_layout_id: Int
    abstract val search_dialog_title: String
    abstract val menu_items_values: Map<Int, String>

    abstract fun button_add_callback(view: View?)
    abstract fun button_search_callback_helper(search_text: String): Int
    abstract fun menu_item_callback(menu_item: MenuItem): Boolean

    fun create() {
        // create all required views
        create_drawer_layout()
        create_toolbar()
        create_scroll_view()
        create_button_add()
        create_navigation()
    }

    fun create_drawer_layout() {
        drawer_layout = findViewById(R.id.drawer_layout)
    }

    private fun create_toolbar() {
        // get a toolbar
        toolbar = findViewById(R.id.toolbar)

        // get a hamburger button
        val button_hamburger: Button = findViewById(R.id.button_hamburger)
        button_hamburger.setOnClickListener { button_hamburger_callback(button_hamburger) }

        // get a search button
        val button_search: Button = findViewById(R.id.button_search)
        button_search.setOnClickListener { button_search_callback(button_search) }
    }

    private fun create_button_add() {
        button_add = findViewById(R.id.button_add)
        button_add.setOnClickListener { button_add_callback(button_add) }
    }

    private fun create_scroll_view() {
        scroll_view = findViewById(R.id.scroll_linear)
    }

    private fun create_navigation() {
        navigation = findViewById(R.id.navigation)

        // set header view for navigation drawer
        val view_header: View = navigation.inflateHeaderView(R.layout.navigation_user)

        // set user image
        val view_user_img: ImageView = view_header.findViewById(R.id.img_user)
        Glide.with(this).load(CloudFirestore.get_user_img_url()).centerCrop().circleCrop().into(view_user_img)

        // set user name
        val text_view_user_name: TextView = view_header.findViewById(R.id.textView_user_name)
        text_view_user_name.text = CloudFirestore.get_user_name()

        // set user email
        val text_view_user_email: TextView = view_header.findViewById(R.id.textView_user_email)
        text_view_user_email.text = CloudFirestore.get_user_email()

        // set navigation drawer menu
        navigation.inflateMenu(menu_layout_id)

        menu_items_values.forEach {
            navigation.menu.findItem(it.key).title = it.value
        }

        navigation.setNavigationItemSelectedListener { menu_item ->
            menu_item_callback(menu_item)
        }
    }

    fun scroll_view_add_button(button: View, existing_layout_params: LinearLayout.LayoutParams? = null, background: Int? = null) {
        val layout_params = existing_layout_params ?: LinearLayout.LayoutParams(
                                                        LinearLayout.LayoutParams. MATCH_PARENT,
                                                        LinearLayout.LayoutParams. WRAP_CONTENT
                                                    )
        layout_params.setMargins(5, 0, 5, 15)

        // add layout parameters to the button
        button.layoutParams = layout_params

        // set button background
        if(background != null)
            button.setBackgroundResource(background)

        // add button to the linear layout
        scroll_view.addView(button)
    }

    private fun button_hamburger_callback(view: View?) {
        drawer_layout.openDrawer(GravityCompat.START)
    }

    private fun button_search_callback(view: View?) {
        // create edit text for dialog
        val input_text = EditText(this)
        input_text.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME

        // create a dialog
        val dialog = DialogAdd("Search $search_dialog_title", this, input_text)

        dialog.dialog.setPositiveButton("Search") { dialogInterface, i ->
            val search_text: String = input_text.text.toString()

            // position of the searched item in the scroll window
            val item_position: Int = button_search_callback_helper(search_text)

            // check if searched item exists
            if(item_position == -1) {
                val text_view = TextView(this)
                text_view.text = "${search_dialog_title.capitalize()} $search_text does not exist"

                val dialog_error = DialogAdd("Search failed", this, text_view)
                dialog_error.show()
            } else { // search item exists
                val scroll_view_parent: ScrollView = findViewById(R.id.scroll_view)
                scroll_view_parent.smoothScrollTo(0, item_position)
            }
        }

        dialog.show()
    }
}