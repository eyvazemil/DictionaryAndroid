package com.example.dictionary.Frontend

import android.os.Build
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.dictionary.R

@RequiresApi(Build.VERSION_CODES.M)
abstract class ActivityInterface : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var button_add: Button
    lateinit var scroll_view: LinearLayout

    companion object {
        private val TAG = "ActivityInterface"
        val background: Int = R.drawable.gradient_button_scroll
    }

    abstract val search_dialog_title: String

    abstract fun button_add_callback(view: View?)
    abstract fun button_search_callback_helper(search_text: String): Int

    fun create(activity_title: String) {
        // create all required views
        create_toolbar(activity_title)
        create_scroll_view()
        create_button_add()
    }

    private fun create_toolbar(activity_title: String) {
        // get a toolbar
        toolbar = findViewById(R.id.toolbar)

        // set toolbar text
        val text_activity_title: TextView = findViewById(R.id.text_activity_title)
        text_activity_title.text = activity_title

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

    fun scroll_view_add_button(button: View, existing_layout_params: LinearLayout.LayoutParams? = null, flag_background: Boolean = false) {
        val layout_params = existing_layout_params ?: LinearLayout.LayoutParams(
                                                        LinearLayout.LayoutParams. MATCH_PARENT,
                                                        LinearLayout.LayoutParams. WRAP_CONTENT
                                                    )
        layout_params.setMargins(5, 0, 5, 15)

        // add layout parameters to the button
        button.layoutParams = layout_params

        // set button background
        if(flag_background)
            button.setBackgroundResource(background)

        // add button to the linear layout
        scroll_view.addView(button)
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

            Log.d(TAG, "Searched item position: $item_position")

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