package com.example.dictionary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.*
import com.example.dictionary.Frontend.ActivityOpenerInterface
import com.example.dictionary.Frontend.ButtonLayoutTitle
import com.example.dictionary.Frontend.ButtonLayoutWord
import com.example.dictionary.Frontend.ScrollableWindowInterface
import com.example.dictionary.Miscelaneous.EnumStatus

class TitleActivity : AppCompatActivity(), ScrollableWindowInterface {
    override var scroll_window: LinearLayout? = null
    var button_title: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)

        // set scrollable window
        scroll_window = findViewById(R.id.scrollView_words)

        // get language choose button
        button_title = findViewById(R.id.button5)
        button_title?.setOnClickListener {
            // if button is clicked, finish this activity to get back to main activity
            finish()
        }

        // get titles list and add them as a button to the layout
        fill_scroll_window()

        // get button for adding new titles
        val button_add_word: Button = findViewById(R.id.button6)
        button_add_word.setOnClickListener {
            // create a dialog
            val dialog = DialogWord("Add new word", this, null)

            dialog.dialog.setPositiveButton("Add") { dialogInterface, i ->
                val new_word: String = dialog.dialog_input_word?.text.toString()
                val new_definition: String = dialog.dialog_input_definition?.text.toString()

                // add new language to the dictionary manager
                val status: EnumStatus = MainActivity.dictionary_manager.add_word(new_word, new_definition)
                if (status == EnumStatus.ALREADY_EXISTS)
                    Toast.makeText(this, "Word $new_word already exists", Toast.LENGTH_LONG).show()
                else {
                    Toast.makeText(this, "Word $new_definition was added successfully", Toast.LENGTH_LONG).show()

                    // update scroll window
                    fill_scroll_window()
                }
            }

            // show dialog
            dialog.show()
        }
    }

    fun set_button_title_text() {
        // words count in this title
        val words_count = MainActivity.dictionary_manager.get_words_count()

        if(MainActivity.dictionary_manager.m_chosen_title == "")
            button_title?.text = "<None> ($words_count)"
        else
            button_title?.text = "${MainActivity.dictionary_manager.m_chosen_title} ($words_count)"
    }

    override fun fill_scroll_window() {
        // empty scroll window
        scroll_window?.removeAllViews()

        var word_num = 1

        // add language button to the scroll window
        MainActivity.dictionary_manager.get_words().forEach { word ->
            val layout = LinearLayout(this, null, LinearLayout.HORIZONTAL)

            // create layout parameters for a title button
            val param_button_word = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            param_button_word.width = LinearLayout.LayoutParams.MATCH_PARENT

            // create layout parameters for an edit button
            val param_button_num = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            param_button_num.width = LinearLayout.LayoutParams.WRAP_CONTENT

            // create text view for word number
            val button_word_num = Button(this)
            button_word_num.text = "${word_num}."
            button_word_num.layoutParams = param_button_num

            // create layout for title
            val layout_word = ButtonLayoutWord(this, word, this).create()
            layout_word.layoutParams = param_button_word

            // add word number and word to the layout
            layout.addView(button_word_num)
            layout.addView(layout_word)

            // add button to the scroll window
            scroll_window?.addView(layout)

            word_num++
        }

        // set button text
        set_button_title_text()
    }
}