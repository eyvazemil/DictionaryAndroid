package com.example.dictionary

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.view.View
import android.widget.EditText

class DialogAdd(dialog_title: String, context: Context, dialog_view: View) {
    val dialog: AlertDialog.Builder = AlertDialog.Builder(context)

    init {
        // add dialog title
        dialog.setTitle(dialog_title)

        // attach view to dialog
        dialog.setView(dialog_view)

        // set negative button
        dialog.setNegativeButton("Cancel") { dialogInterface, i ->
            dialogInterface.cancel()
        }
    }

    fun show() {
        dialog.show()
    }
}