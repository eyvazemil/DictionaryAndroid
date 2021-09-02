package com.example.dictionary

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.dictionary.Backend.DictionaryManager
import com.example.dictionary.Language
import com.example.dictionary.Miscelaneous.EnumStatus


class MainActivity : AppCompatActivity() {
    var scroll_languages: LinearLayout? = null

    companion object {
        val dir: String = "/data/data/com.example.dictionary/files/Languages/"
        val dictionary_manager: DictionaryManager = DictionaryManager(dir)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize Dictionary Manager object
        dictionary_manager.initialize()

        // get scroll view reference by id
        scroll_languages = findViewById(R.id.languages_layout)

        // get languages list and add them as a button to the layout
        fill_scroll_window()
    }

    override fun onDestroy() {
        super.onDestroy()

        // finish dictionary manager
        dictionary_manager.finish()
    }

    fun button_add_language_on_click(view: View?) {
        // create edit text for dialog
        val input_text: EditText = EditText(this)
        input_text.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME

        // create a dialog
        val dialog = DialogAdd("Add new language", this, input_text)

        dialog.dialog.setPositiveButton("Add") { dialogInterface, i ->
            val new_lang_name: String = input_text.text.toString()

            // add new language to the dictionary manager
            val status: EnumStatus = dictionary_manager.add_language(new_lang_name)
            if (status == EnumStatus.ALREADY_EXISTS)
                Toast.makeText(this, "Language $new_lang_name already exists", Toast.LENGTH_LONG).show()
            else {
                Toast.makeText(this, "Language $new_lang_name was added successfully", Toast.LENGTH_LONG).show()

                // update scroll window
                fill_scroll_window()

                // open language
                open_language(new_lang_name)
            }
        }

        // show dialog
        dialog.show()
    }

    fun button_choose_lang_callback(view: View?) {
        // get button
        val button_clicked: Button = view as Button

        // open chosen language
        open_language(button_clicked.text.toString())
    }

    fun fill_scroll_window() {
        // empty scroll window
        scroll_languages?.removeAllViews()

        // add language button to the scroll window
        dictionary_manager.get_languages().forEach {
            val button_lang: Button = Button(applicationContext)

            //set button text
            button_lang.text = it

            // set language listener
            button_lang.setOnClickListener { ti ->
                button_choose_lang_callback(ti)
            }

            // add button to the scroll window
            scroll_languages?.addView(button_lang)
        }
    }

    fun open_language(lang_name: String) {
        // choose language in backend
        dictionary_manager.choose_language(lang_name)

        // create language activity
        val intent = Intent(this, Language::class.java)
        startActivity(intent)
    }
}