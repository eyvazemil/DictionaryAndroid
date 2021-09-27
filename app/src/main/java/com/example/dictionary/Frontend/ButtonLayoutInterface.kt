package com.example.dictionary.Frontend

import android.content.Context
import android.text.InputType
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import com.example.dictionary.MainActivity
import com.example.dictionary.Miscelaneous.EnumStatus
import com.example.dictionary.R

abstract class ButtonLayoutInterface(context: Context, val name: String,
                                        val scrollable_window: ScrollableWindowInterface
                                     ) : View(context), PopupMenu.OnMenuItemClickListener
{
    abstract fun create() : View

    fun button_callback(view: View?) {
        val pop_up = PopupMenu(context, view)
        pop_up.setOnMenuItemClickListener { item ->
            onMenuItemClick(item)
        }
        pop_up.inflate(R.menu.menu_edit)
        pop_up.show()
    }
}