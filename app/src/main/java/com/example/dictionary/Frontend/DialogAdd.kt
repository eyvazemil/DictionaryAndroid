package com.example.dictionary.Frontend

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.view.marginTop

class DialogAdd(dialog_title: String, context: Context, dialog_view: View) {
    val dialog: AlertDialog.Builder = AlertDialog.Builder(context)

    init {
        // create linear layout for dialog view
        val layout = LinearLayout(context, null, LinearLayout.VERTICAL)

        // layout parameters for dialog view
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        params.topMargin = 30
        params.bottomMargin = 10
        params.leftMargin = 20
        params.rightMargin = 20

        // set margins for dialog view
        dialog_view.layoutParams = params

        // put dialog view in layout
        layout.addView(dialog_view)

        // attach view to dialog
        dialog.setView(layout)

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