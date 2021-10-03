package com.example.dictionary

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.example.dictionary.Frontend.*
import com.example.dictionary.Miscelaneous.EnumStatus

@RequiresApi(Build.VERSION_CODES.M)
class TitleActivity : ActivityInterface(), ButtonToolbarBack, ScrollableWindowInterface {
    override val search_dialog_title: String = "word"

    companion object {
        private val TAG = "TitleActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get generic views from parent abstract class
        create(
            if(MainActivity.dictionary_manager.m_chosen_title!! == "") "<None>"
            else MainActivity.dictionary_manager.m_chosen_title!!
        )

        // get toolbar back button
        create_button_toolbar_back(this)

        // get titles list and add them as a button to the layout
        fill_scroll_window()
    }

    override fun fill_scroll_window() {
        // empty scroll window
        scroll_view.removeAllViews()

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
            param_button_num.setMargins(0, 0, 5, 0)

            // create text view for word number
            val button_word_num = Button(this)
            button_word_num.text = "${word_num}."
            button_word_num.layoutParams = param_button_num

            // create layout for title
            val layout_word = ButtonLayoutWord(this, word, this).create()
            layout_word.layoutParams = param_button_word

            // set background colours for buttons
            button_word_num.setBackgroundResource(background)
            layout_word.setBackgroundResource(background)

            // add word number and word to the layout
            layout.addView(button_word_num)
            layout.addView(layout_word)

            // add button to the scroll window
            scroll_view_add_button(layout)

            word_num++
        }
    }

    override fun button_add_callback(view: View?) {
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
                Toast.makeText(this, "Word $new_word was added successfully", Toast.LENGTH_LONG).show()

                // update scroll window
                fill_scroll_window()
            }
        }

        // show dialog
        dialog.show()
    }

    override fun button_search_callback_helper(search_text: String): Int {
        var word_pos = -1
        val words: List<Pair<String, String>> = MainActivity.dictionary_manager.get_words()

        for(i in (0..words.size)) {
            if(words[i].first == search_text) {
                word_pos = i
                break
            }
        }

        return word_pos
    }

    override fun button_toolbar_back_callback(view: View?) {
        this.finish()
    }
}