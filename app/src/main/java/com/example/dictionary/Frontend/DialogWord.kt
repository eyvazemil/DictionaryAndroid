package com.example.dictionary.Frontend

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import com.example.dictionary.R

class DialogWord(dialog_title: String, context: Context, word: Pair<String, String>?) {
    val dialog: AlertDialog.Builder = AlertDialog.Builder(context)
    var dialog_input_word: EditText? = null
    var dialog_input_definition: EditText? = null

    init {
        val layout_inflater = LayoutInflater.from(context)
        val layout = layout_inflater.inflate(R.layout.dialog_word, null)

        // attach view to dialog
        dialog.setView(layout)

        // set inputs
        dialog_input_word = layout.findViewById(R.id.dialog_input_word)
        dialog_input_definition = layout.findViewById(R.id.dialog_input_definition)
        if(word != null) {
            dialog_input_word?.hint = word.first
            dialog_input_definition?.hint = word.second
        }

        // add dialog title
        dialog.setTitle(dialog_title)

        // set negative button
        dialog.setNegativeButton("Cancel") { dialogInterface, i ->
            dialogInterface.cancel()
        }
    }

    fun show() {
        dialog.show()
    }
}