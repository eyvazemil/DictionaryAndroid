package com.example.dictionary

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.example.dictionary.Frontend.*
import com.example.dictionary.Miscelaneous.EnumStatus

@RequiresApi(Build.VERSION_CODES.M)
class LanguageActivity : ActivityInterface(), ButtonToolbarBack, ScrollableWindowInterface, ActivityOpenerInterface {
    override val search_dialog_title: String = "title"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get generic views from parent abstract class
        create(MainActivity.dictionary_manager.m_chosen_language!!)

        // get toolbar back button
        create_button_toolbar_back(this)

        // get titles list and add them as a button to the layout
        fill_scroll_window()
    }

    override fun fill_scroll_window() {
        // empty scroll window
        scroll_view.removeAllViews()

        // add language button to the scroll window
        MainActivity.dictionary_manager.get_titles().forEach { title_name ->
            // create layout for title
            val layout_title = ButtonLayoutTitle(this, title_name, this, this)

            // add button to the scroll window
            scroll_view_add_button(layout_title.create(), flag_background = true)
        }
    }

    override fun open_activity(name: String) {
        // choose language in backend
        MainActivity.dictionary_manager.choose_title(name)

        // create language activity
        val intent = Intent(this, TitleActivity::class.java)
        startActivity(intent)
    }

    override fun button_add_callback(view: View?) {
        // create edit text for dialog
        val input_text = EditText(this)
        input_text.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME

        // create a dialog
        val dialog = DialogAdd("Add new title", this, input_text)

        dialog.dialog.setPositiveButton("Add") { dialogInterface, i ->
            val new_title_name: String = input_text.text.toString()

            // add new language to the dictionary manager
            val status: EnumStatus = MainActivity.dictionary_manager.add_title(new_title_name)
            if (status == EnumStatus.ALREADY_EXISTS)
                Toast.makeText(this, "Title $new_title_name already exists", Toast.LENGTH_LONG).show()
            else {
                Toast.makeText(this, "Title $new_title_name was added successfully", Toast.LENGTH_LONG).show()

                // update scroll window
                fill_scroll_window()

                // open language
                open_activity(new_title_name)
            }
        }

        // show dialog
        dialog.show()
    }

    override fun button_search_callback_helper(search_text: String): Int {
        return MainActivity.dictionary_manager.get_titles().indexOf(search_text)
    }

    override fun button_toolbar_back_callback(view: View?) {
        this.finish()
    }
}