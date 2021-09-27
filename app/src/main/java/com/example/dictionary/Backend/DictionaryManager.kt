package com.example.dictionary.Backend

import android.util.Log
import com.example.dictionary.Backend.DB.*
import com.example.dictionary.Miscelaneous.EnumStatus

class DictionaryManager(private val dir: String) {
    private var m_dictionary: Dictionary = Dictionary()

    var m_chosen_language: String? = null
        get() = field
        private set

    var m_chosen_title: String? = null
        get() = field
        private set

    val m_modified_languages: MutableList<String> = mutableListOf()
    private val dir_languages: String = dir
    private val file_read_write: FileReadWrite = FileReadWrite(dir_languages)

    companion object {
        private val TAG = "DictionaryManager"
        val m_file_extension = ".txt"
    }

    fun initialize() {
        // read the language files and write into the dictionary object
        file_read_write.read_dir(setOf(m_file_extension)).forEach {
            m_dictionary.add_language(it)
        }

        Log.d(TAG, "Dictionary manager initialization went successfully with ${m_dictionary.m_map_languages.size} languages added")
    }

    fun finish() {
        // if any language is open and modified, write it into the file
        if(m_chosen_language != null && m_dictionary.find_language(m_chosen_language!!)?.m_flag_modified!!)
            close_file(m_chosen_language!!)
    }

    private fun open_file(lang_name: String) {
        file_read_write.read(lang_name).m_map_titles.forEach {
            val title_name: String = it.key

            // add title
            if(m_dictionary.find_language(lang_name)!!.find_title(title_name) == null)
                m_dictionary.find_language(lang_name)!!.add_title(title_name, true)

            // add words into title
            it.value.m_list_words.forEach {
                m_dictionary.find_language(lang_name)!!.find_title(title_name)!!.add_word(it.m_word, it.m_definition, true)
            }
        }
    }

    private fun close_file(lang_name: String) {
        // write the language to the file
        file_read_write.write(m_dictionary.find_language(lang_name)!!)

        // write this language name to the list of modified languages
        m_modified_languages.add(lang_name)
    }

    fun choose_language(lang_name: String): EnumStatus {
        // check if language is already chosen
        if(m_chosen_language != null && m_chosen_language == lang_name)
            return EnumStatus.CHOOSE_SUCCESS

        // write current language file only if the language was actually modified
        if(m_chosen_language != null) {
            // write the file only if language was modified
            if(m_dictionary.find_language(m_chosen_language!!)?.m_flag_modified!!)
                close_file(m_chosen_language!!)

            // free memory from this language
            m_dictionary.remove_language(m_chosen_language!!)
            m_dictionary.add_language(m_chosen_language!!)
        }

        // choose the language
        m_chosen_language = m_dictionary.find_language(lang_name)?.m_lang_name ?: return EnumStatus.DOES_NOT_EXIST
        m_chosen_title = null

        // open chosen language file
        open_file(m_chosen_language!!)

        return EnumStatus.CHOOSE_SUCCESS
    }

    fun choose_title(title_name: String): EnumStatus {
        if(m_chosen_language == null)
            return EnumStatus.LANG_NOT_CHOSEN
        else
            m_chosen_title = m_dictionary.find_language(m_chosen_language!!)!!.find_title(title_name)?.m_title_name ?: return EnumStatus.DOES_NOT_EXIST

        return EnumStatus.CHOOSE_SUCCESS
    }

    fun get_languages(): List<String> {
        val languages: MutableList<String> = mutableListOf()
        m_dictionary.m_map_languages.keys.toSortedSet().forEach {
            languages.add(it)
        }

        return languages
    }

    fun get_titles(): List<String> {
        val titles: MutableList<String> = mutableListOf()

        if(m_chosen_language != null) {
            m_dictionary.find_language(m_chosen_language!!)?.m_map_titles!!.keys.toSortedSet().forEach {
                titles.add(it)
            }
        }

        return titles
    }

    fun get_words(): List<Pair<String, String>> {
        val words: MutableList<Pair<String, String>> = mutableListOf()

        if(m_chosen_title != null) {
            m_dictionary.find_language(m_chosen_language!!)?.find_title(m_chosen_title!!)?.m_list_words!!.forEach {
                words.add(Pair<String, String>(it.m_word, it.m_definition))
            }
        }

        return words
    }

    fun get_words_count(): Int {
        var count = 0

        if(m_chosen_title != null)
            count = m_dictionary.find_language(m_chosen_language!!)?.find_title(m_chosen_title!!)?.m_list_words!!.size

        return count
    }

    fun add_language(lang_name: String): EnumStatus {
        val status: EnumStatus = m_dictionary.add_language(lang_name)

        // to modify the language, any operation should be done on it
        if(status == EnumStatus.ADD_SUCCESS) {
            // set new language as chosen one
            m_chosen_language = lang_name
            m_chosen_title = null
        }

        return status
    }

    fun remove_language(lang_name: String): EnumStatus {
        val status: EnumStatus = m_dictionary.remove_language(lang_name)

        if(status == EnumStatus.REMOVE_SUCCESS && m_chosen_language == lang_name) {
            m_chosen_language = null
            m_chosen_title = null
        }

        return status
    }

    fun change_language_name(old_lang_name: String, new_lang_name: String): EnumStatus {
        val status: EnumStatus = m_dictionary.change_language(old_lang_name, new_lang_name)

        if(status == EnumStatus.CHANGE_SUCCESS)
            m_chosen_title = null

        return status
    }

    fun add_title(title_name: String): EnumStatus {
        val status: EnumStatus = m_dictionary.find_language(m_chosen_language!!)?.add_title(title_name)!!

        if(status == EnumStatus.ADD_SUCCESS)
            m_chosen_title = title_name

        return status
    }

    fun remove_title(title_name: String): EnumStatus {
        val status: EnumStatus = m_dictionary.find_language(m_chosen_language!!)?.remove_title(title_name)!!

        if(status == EnumStatus.REMOVE_SUCCESS)
            m_chosen_title = null

        return status
    }

    fun change_title_name(old_title_name: String, new_title_name: String): EnumStatus {
        val status: EnumStatus = m_dictionary.find_language(m_chosen_language!!)?.change_title(old_title_name, new_title_name)!!

        if(status == EnumStatus.CHANGE_SUCCESS)
            m_chosen_title = null

        return status
    }

    fun add_word(word: String, definition: String): EnumStatus {
        return m_dictionary.find_language(m_chosen_language!!)?.find_title(m_chosen_title!!)?.add_word(word, definition)!!
    }

    fun remove_word(word: String): EnumStatus {
        return m_dictionary.find_language(m_chosen_language!!)?.find_title(m_chosen_title!!)?.remove_word(word)!!
    }

    fun change_word(word: String, new_word: String, new_definition: String): EnumStatus {
        return m_dictionary.find_language(m_chosen_language!!)?.find_title(m_chosen_title!!)?.change_word(word, new_word, new_definition)!!
    }

    fun find_word(word: String): Word? {
        return m_dictionary.find_language(m_chosen_language!!)?.find_title(m_chosen_title!!)?.find_word(word)
    }
}