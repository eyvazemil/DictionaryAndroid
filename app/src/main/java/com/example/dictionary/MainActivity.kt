package com.example.dictionary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.FileUtils.copy
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.dictionary.Backend.DictionaryManager
import com.example.dictionary.Backend.FileReadWrite
import com.example.dictionary.Miscelaneous.EnumStatus
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.File
import kotlin.math.sign


class MainActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {
    var scroll_languages: LinearLayout? = null
    private lateinit var dir: String

    companion object {
        private const val TAG = "MainActivity"
        lateinit var dictionary_manager: DictionaryManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // create the directory with languages if it doesn't exist
        dir = "${filesDir.path}/Languages/"
        if(!File(dir).exists()) {
            Log.d(TAG, FileReadWrite(dir).read_dir(setOf(".txt")).toString())
            File(dir).mkdirs()
        }

        // initialize Dictionary Manager object
        dictionary_manager = DictionaryManager(dir)
        dictionary_manager.initialize()

        // get scroll view reference by id
        scroll_languages = findViewById(R.id.languages_layout)

        // get languages list and add them as a button to the layout
        fill_scroll_window()
    }

    fun button_sign_out_callback(view: View?) {
        val pop_up = PopupMenu(this, view)
        pop_up.setOnMenuItemClickListener { item ->
            onMenuItemClick(item)
        }
        pop_up.inflate(R.menu.menu_log_out)
        pop_up.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.menu_item_log_out)
            sign_out()
        else if(item?.itemId == R.id.menu_item_user)
            dialog_user()

        return true
    }

    private fun sign_out() {
        Log.i(TAG, "Sign out")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this,gso)

        mGoogleSignInClient.signOut()

        // sign out current user
        FirebaseAuth.getInstance().signOut()

        // go to the Google sign in page
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun dialog_user() {
        val text_user = TextView(this)
        text_user.text = Firebase.auth.currentUser?.email
        val dialog = DialogAdd("User", this, text_user)
        dialog.show()
    }

    override fun onDestroy() {
        // finish dictionary manager
        dictionary_manager.finish()

        super.onDestroy()
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
        val intent = Intent(this, LanguageActivity::class.java)
        startActivity(intent)
    }
}