package com.example.dictionary

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.dictionary.Backend.CloudFirestore
import com.example.dictionary.Backend.DictionaryManager
import com.example.dictionary.Backend.FileReadWrite
import com.example.dictionary.Frontend.DialogAdd
import com.example.dictionary.Miscelaneous.EnumStatus
import com.firebase.ui.auth.AuthUI
import java.io.File
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.dictionary.Frontend.ActivityOpenerInterface
import com.example.dictionary.Frontend.ButtonLayoutLanguage
import com.example.dictionary.Frontend.ScrollableWindowInterface
import kotlinx.coroutines.*
import java.util.*


@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : ActivityInterface(), ScrollableWindowInterface, ActivityOpenerInterface {
    private lateinit var dir: String
    private lateinit var cloud_firestore: CloudFirestore
    private lateinit var file_read_write: FileReadWrite

    override val menu_layout_id: Int = R.menu.menu_nav_language
    override val search_dialog_title: String = "language"
    override val menu_items_values: Map<Int, String> = mapOf()

    companion object {
        private const val TAG = "MainActivity"
        lateinit var dictionary_manager: DictionaryManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // create the directory with languages if it doesn't exist
        dir = "${filesDir.path}/Languages/${CloudFirestore.get_user_email()}/"
        if(!File(dir).exists())
            File(dir).mkdirs()

        // initialize file reader and writer to get the language files' last modification time stamps
        file_read_write = FileReadWrite(dir)

        Log.d(TAG, "Progress dialog started")

        Log.d(TAG, "Files' modification time stamps: ${file_read_write.get_modification_dates(setOf(DictionaryManager.m_file_extension))}")

        // sync with firebase
        cloud_firestore = CloudFirestore(dir)
        cloud_firestore.pull(file_read_write.get_modification_dates(setOf(DictionaryManager.m_file_extension)))

        // initialize Dictionary Manager object
        dictionary_manager = DictionaryManager(dir)
        dictionary_manager.initialize()

        Log.d(TAG, "Dir: $dir")

        // get generic views from parent abstract class
        create()

        // get languages list and add them as a button to the layout
        fill_scroll_window()
    }

    fun button_choose_lang_callback(view: View?) {
        // get button
        val button_clicked: Button = view as Button

        // open chosen language
        open_activity(button_clicked.text.toString())
    }

    override fun fill_scroll_window() {
        // empty scroll window
        scroll_view.removeAllViews()

        // add language button to the scroll window
        dictionary_manager.get_languages().forEach {
            val layout_lang = ButtonLayoutLanguage(this, it, this, this)

            // set language listener
            layout_lang.setOnClickListener { ti ->
                button_choose_lang_callback(ti)
            }

            // add button to the scroll window
            scroll_view_add_button(layout_lang.create(), background = R.drawable.gradient_button_scroll)
        }
    }

    override fun open_activity(name: String) {
        // choose language in backend
        dictionary_manager.choose_language(name)

        // create language activity
        val intent = Intent(this, LanguageActivity::class.java)
        startActivity(intent)
    }

    override fun button_add_callback(view: View?) {
        // create edit text for dialog
        val input_text = EditText(this)
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
                open_activity(new_lang_name)
            }
        }

        // show dialog
        dialog.show()
    }

    override fun button_search_callback_helper(search_text: String): Int {
        return dictionary_manager.get_languages().indexOf(search_text)
    }

    override fun menu_item_callback(menu_item: MenuItem): Boolean {
        if(menu_item.itemId == R.id.log_out) {
            Log.i(TAG, "Sign out")

            AuthUI.getInstance().signOut(this).addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    Log.i(TAG, "Signed out successfully")

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else
                    Log.w(TAG, "Signing out failed")
            }
        } else if(menu_item.itemId == R.id.close) {
            // finish dictionary manager
            dictionary_manager.finish()

            // timestamps for all language files on the device
            val map_timestamps = file_read_write.get_modification_dates(setOf(DictionaryManager.m_file_extension))

            // sync with firebase
            cloud_firestore.push(map_timestamps, dictionary_manager.m_modified_languages)

            // close the application
            finish()
        }

        return true
    }
}