package com.example.dictionary.Frontend

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputType
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.dictionary.MainActivity
import com.example.dictionary.Miscelaneous.EnumStatus
import com.example.dictionary.R

@SuppressLint("ViewConstructor")
class ButtonLayoutTitle(context: Context, name: String, scrollable_window: ScrollableWindowInterface,
                            val activity_opener: ActivityOpenerInterface
                        ) : ButtonLayoutInterface(context, name, scrollable_window)
{
    @SuppressLint("ResourceAsColor")
    override fun create() : View {
        // set title button
        val button_title = Button(context)
        if(name == "")
            button_title.text = context.getString(R.string.default_title)
        else
            button_title.text = name
        button_title.setTransformationMethod(null)

        // set title button listener
        button_title.setOnClickListener { ti ->
            button_callback(ti)
        }

        return button_title
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if(item!!.itemId == R.id.open) // open title activity with all words in it
            activity_opener.open_activity(name)
        else if(item!!.itemId == R.id.change) { // create dialog for changing title name
            val input_text = EditText(context)
            input_text.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            input_text.setText(name)

            // create dialog for button editing
            val dialog = DialogAdd("Edit title", context, input_text)
            dialog.dialog.setPositiveButton("Change") { dialogInterface, i ->
                val new_name: String = input_text.text.toString()

                // add new language to the dictionary manager
                val status: EnumStatus = MainActivity.dictionary_manager.change_title_name(name, new_name)
                if (status == EnumStatus.ALREADY_EXISTS) {
                    if(new_name == "")
                        Toast.makeText(context, "Default title already exists", Toast.LENGTH_LONG).show()
                    else
                        Toast.makeText(context, "Title $new_name already exists", Toast.LENGTH_LONG).show()
                } else if(status == EnumStatus.DEFAULT_TITLE_CHANGE)
                    Toast.makeText(context, "Default title may not be changed", Toast.LENGTH_LONG).show()
                else {
                    Toast.makeText(context, "Title $new_name was added successfully", Toast.LENGTH_LONG).show()

                    // update scroll window
                    scrollable_window.fill_scroll_window()
                }
            }

            dialog.show()
        } else { // create dialog for confirmation of removal
            val text_confirmation = TextView(context)
            text_confirmation.text = "\tAre you sure that you want to remove title \"$name\""

            // create dialog for removal confirmation
            val dialog = DialogAdd("Remove title", context, text_confirmation)
            dialog.dialog.setPositiveButton("Remove") { dialogInterface, i ->
                val status: EnumStatus = MainActivity.dictionary_manager.remove_title(name)
                if(status == EnumStatus.DOES_NOT_EXIST)
                    Toast.makeText(context, "Title $name does not exist", Toast.LENGTH_LONG).show()
                else if(status == EnumStatus.DEFAULT_TITLE_REMOVAL)
                    Toast.makeText(context, "Default title may not be removed", Toast.LENGTH_LONG).show()
                else {
                    Toast.makeText(context, "Title $name was removed successfully", Toast.LENGTH_LONG).show()

                    // update scroll window
                    scrollable_window.fill_scroll_window()
                }
            }

            dialog.show()
        }

        return true
    }
}