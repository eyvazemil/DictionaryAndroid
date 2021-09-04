package com.example.dictionary.Frontend

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.dictionary.DialogAdd
import com.example.dictionary.DialogWord
import com.example.dictionary.MainActivity
import com.example.dictionary.Miscelaneous.EnumStatus
import com.example.dictionary.R

@SuppressLint("ViewConstructor")
class ButtonLayoutWord(context: Context, val word: Pair<String, String>,
                       val scrollable_window: ScrollableWindowInterface) : View(context), PopupMenu.OnMenuItemClickListener
{
    @SuppressLint("ResourceAsColor")
    fun create() : View {
        // set word button
        val button_word = Button(context)
        button_word.text = word.first
        button_word.setTransformationMethod(null)

        // set word button listener
        button_word.setOnClickListener { ti ->
            button_word_callback(ti)
        }

        return button_word
    }

    fun button_word_callback(view: View?) {
        val pop_up = PopupMenu(context, view)
        pop_up.setOnMenuItemClickListener { item ->
            onMenuItemClick(item)
        }
        pop_up.inflate(R.menu.menu_edit)
        pop_up.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if(item!!.itemId == R.id.open) { // open word with definition
            // create layout for word and definition inputs
            val text_definition = TextView(context)
            text_definition.textSize = 18F
            text_definition.text = "\t${word.second}"

            // create dialog for button editing
            val dialog = DialogAdd(word.first, context, text_definition)

            dialog.show()
        } else if(item!!.itemId == R.id.change) { // create dialog for changing word
            // create dialog
            val dialog = create_dialog_word()
            dialog.dialog.setPositiveButton("Change") { dialogInterface, i ->
                val new_word: String = dialog.dialog_input_word?.text.toString()
                val new_definition: String = dialog.dialog_input_definition?.text.toString()

                // add new word to the dictionary manager
                val status: EnumStatus = MainActivity.dictionary_manager.change_word(word.first, new_word, new_definition)
                if(status == EnumStatus.ALREADY_EXISTS)
                    Toast.makeText(context, "Word \"$new_word\" already exists", Toast.LENGTH_LONG).show()
                else {
                    Toast.makeText(context, "Word \"$new_word\" was added successfully", Toast.LENGTH_LONG).show()

                    // update scroll window
                    scrollable_window.fill_scroll_window()
                }
            }

            dialog.show()
        } else { // create dialog for confirmation of word removal
            val text_confirmation = TextView(context)
            text_confirmation.text = "Are you sure that you want to remove word \"${word.first}\""

            // create dialog for removal confirmation
            val dialog = DialogAdd("Remove word", context, text_confirmation)
            dialog.dialog.setPositiveButton("Remove") { dialogInterface, i ->
                val status: EnumStatus = MainActivity.dictionary_manager.remove_word(word.first)
                if(status == EnumStatus.DOES_NOT_EXIST)
                    Toast.makeText(context, "Word \"${word.first}\" does not exist", Toast.LENGTH_LONG).show()
                else {
                    Toast.makeText(context, "Word \"${word.first}\" was removed successfully", Toast.LENGTH_LONG).show()

                    // update scroll window
                    scrollable_window.fill_scroll_window()
                }
            }

            dialog.show()
        }

        return true
    }

    fun create_dialog_word(): DialogWord {
        return DialogWord("Edit word", context, word)
    }
}