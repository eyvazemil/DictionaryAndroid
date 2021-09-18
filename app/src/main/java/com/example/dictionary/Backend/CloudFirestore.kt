package com.example.dictionary.Backend

import android.net.Uri
import android.util.Log
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

    private companion object {
        val TAG = "CloudFirestore"
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

        map_file_timestamps.forEach {
            if(!set_user_langs.contains(it.key))
                map_langs_for_uploading.put(it.key, it.value)
        }

        modified_languages.forEach {
            if(!map_langs_for_uploading.containsKey(it))
                map_langs_for_uploading.put(it, map_file_timestamps.get(it)!!)
        }

        Log.d(TAG, "Files to be uploaded: $map_langs_for_uploading")

        GlobalScope.launch {
            // traverse through modified languages and add files to the cloud storage
            for(lang_time_stamp in map_langs_for_uploading) {
                val lang_name = lang_time_stamp.key
                val lang_modification_date = lang_time_stamp.value

                Log.d(TAG, "Uploaded language's name: $lang_name")

                // add language to the hash map if user doesn't have this language in firestore
                data_langs.put(lang_name, Timestamp(lang_modification_date))

                // create language file in firebase cloud storage
                val file_lang = "$lang_name${DictionaryManager.m_file_extension}"
                val file = Uri.fromFile(File("${files_dir}$file_lang"))
                val storage_ref = storage.reference.child("${user_email}/$file_lang")

                // upload the language file to the firebase storage and await its finishing
                val result = storage_ref.putFile(file).await()
                if (result.task.isSuccessful) {
                    // task_snapshot.metadata contains file metadata such as size, content-type, etc.
                    Log.d(TAG, "Successfully uploaded language file $file_lang")
                } else
                    Log.d(TAG, "Couldn't upload language file $file_lang")
            }

            // upload hash map to the cloud firestore
            db.collection("users").document(user_email)
                .set(data_langs, SetOptions.merge()).await()
        }
    }
}