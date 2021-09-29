package com.example.dictionary.Backend

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import com.example.dictionary.Backend.FileReadWrite
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*

class CloudFirestore(val files_dir: String) {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val user_email = auth.currentUser?.email!!
    private val set_user_langs: MutableSet<String> = mutableSetOf()

    companion object {
        private val TAG = "CloudFirestore"

        fun get_user_img_url() = FirebaseAuth.getInstance().currentUser?.photoUrl
        fun get_user_name() = FirebaseAuth.getInstance().currentUser?.displayName
        fun get_user_email() = FirebaseAuth.getInstance().currentUser?.email
    }

    fun pull(map_file_timestamps: Map<String, Date>) = runBlocking {
        var doc_langs: DocumentSnapshot? = null

        GlobalScope.launch {
            // get user's language files
            doc_langs = db.collection("users").document(user_email).get().await()

            Log.d(TAG, "User languages document: $doc_langs")

            if(!doc_langs?.data.isNullOrEmpty()) {
                doc_langs?.data?.forEach {
                    val lang_name = it.key
                    val lang_modification_date = (it.value as Timestamp).toDate()

                    Log.d(TAG, "Language: ${lang_name}/$lang_modification_date")

                    // download a file from firebase only if its modification date is later
                    // than a modification date of language file on local device
                    if (!map_file_timestamps.containsKey(lang_name) ||
                        (map_file_timestamps.containsKey(lang_name) &&
                                lang_modification_date > map_file_timestamps.get(lang_name))
                    ) {
                        Log.d(TAG, "Language to be updated: ${lang_name}/" +
                                "${map_file_timestamps.get(lang_name)}/$lang_modification_date"
                        )

                        // create local file
                        val file_local = File("${files_dir}$lang_name${DictionaryManager.m_file_extension}")
                        if (!file_local.exists())
                            file_local.createNewFile()
                        else {
                            // if file exists, delete then create it again to erase all file contents
                            file_local.delete()
                            file_local.createNewFile()
                        }

                        // download file from storage to the local file
                        val result = storage.reference.child("${user_email}/$lang_name${DictionaryManager.m_file_extension}")
                            .getFile(file_local).await()
                        if (result.task.isSuccessful)
                            Log.d(TAG, "Successfully downloaded a language file: $lang_name${DictionaryManager.m_file_extension}")
                        else
                            Log.d(TAG, "Couldn't download a language file: $lang_name${DictionaryManager.m_file_extension}")
                    }

                    set_user_langs.add(lang_name)
                }
            }
            Log.d(TAG, "Set of user's languages: $set_user_langs")
        }
    }

    fun push(map_file_timestamps: Map<String, Date>, modified_languages: List<String>) = runBlocking {
        Log.d(TAG, "File timestamps: $map_file_timestamps")

        // create a hash map of languages that will be uploaded to cloud firestore
        val data_langs = hashMapOf<String, Timestamp>()

        // create a map from modified languages, languages that aren't even in the firebase
        val map_langs_for_uploading: MutableMap<String, Date> = mutableMapOf()

        // languages that must be removed from the firebase storage
        val list_remove_langs: MutableList<String> = mutableListOf()

        map_file_timestamps.forEach {
            if(!map_langs_for_uploading.contains(it.key) &&
                (!set_user_langs.contains(it.key) || modified_languages.contains(it.key))
            ) {
                map_langs_for_uploading.put(it.key, it.value)
            }

            // add language to the hash map if user doesn't have this language in firestore
            data_langs.put(it.key, Timestamp(it.value))
        }

        set_user_langs.forEach { lang_name ->
            if(!data_langs.contains(lang_name))
                list_remove_langs.add(lang_name)
        }

        Log.d(TAG, "Data langs: $data_langs")

        Log.d(TAG, "Files to be uploaded: $map_langs_for_uploading")

        Log.d(TAG, "Files to be removed: $list_remove_langs")

        // upload language files to firebase storage
        GlobalScope.launch {
            // traverse through modified languages and add files to the cloud storage
            for(lang_time_stamp in map_langs_for_uploading) {
                val lang_name = lang_time_stamp.key

                Log.d(TAG, "Uploaded language's name: $lang_name")

                // create language file in firebase cloud storage
                val file_lang = "$lang_name${DictionaryManager.m_file_extension}"
                val file = Uri.fromFile(File("${files_dir}$file_lang"))

                Log.d(TAG, "File uri: $file")

                val storage_ref = storage.reference.child("${user_email}/$file_lang")

                // upload the language file to the firebase storage and await its finishing
                val result = storage_ref.putFile(file).await()
                if(result.task.isSuccessful)
                    Log.d(TAG, "Successfully uploaded language file $file_lang")
                else
                    Log.d(TAG, "Couldn't upload language file $file_lang")
            }

            Log.d(TAG, "Email: $user_email")

            // upload hash map to the cloud firestore
            db.collection("users").document(user_email).set(data_langs).await()

            Log.d(TAG, "Email: $user_email")
        }

        // remove language files from firebase storage
        GlobalScope.launch {
            for(lang_name in list_remove_langs) {
                val file_lang = "$lang_name${DictionaryManager.m_file_extension}"
                val storage_ref = storage.reference.child("${user_email}/$file_lang")
                storage_ref.delete().await()
            }
        }
    }
}