package com.example.dictionary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.example.dictionary.Frontend.ActivityOpenerInterface
import com.example.dictionary.Frontend.ButtonLayoutTitle
import com.example.dictionary.Frontend.ScrollableWindowInterface
import com.example.dictionary.Miscelaneous.EnumStatus

class TitleActivity : AppCompatActivity(), ScrollableWindowInterface {
    override var scroll_window: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)

        // set scrollable window
        scroll_window = findViewById(R.id.scrollView_titles)

        // get language choose button
        val button_lang: Button = findViewById(R.id.button3)
        button_lang.text = MainActivity.dictionary_manager.m_chosen_language
        button_lang.setOnClickListener {
            // if button is clicked, finish this activity to get back to main activity
            finish()
        }

        // get titles list and add them as a button to the layout
        fill_scroll_window()

        // get button for adding new titles
        val button_add_title: Button = findViewById(R.id.button4)
        button_add_title.setOnClickListener {
            // create edit text for dialog
            val input_text: EditText = EditText(this)
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
                }
            }

            // show dialog
            dialog.show()
        }
    }

    override fun fill_scroll_window() {
        // empty scroll window
        /*scroll_window?.removeAllViews()

        Log.d("Point:","__HERE__")

        // add language button to the scroll window
        MainActivity.dictionary_manager.get_titles().forEach { title_name ->
            // create layout for title
            val layout_title = ButtonLayoutTitle(this, title_name, this)

            // add button to the scroll window
            scroll_window?.addView(layout_title.create())
        }*/
    }
}