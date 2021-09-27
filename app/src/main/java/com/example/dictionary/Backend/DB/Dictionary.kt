package com.example.dictionary.Backend.DB

import com.example.dictionary.Miscelaneous.EnumStatus

class Dictionary {
    var m_map_languages: MutableMap<String, Language> = mutableMapOf()
        get() = field
        private set

    fun find_language(lang_name: String): Language? {
        return this.m_map_languages[lang_name]
    }

    fun add_language(lang_name: String): EnumStatus {
        if(find_language(lang_name) != null)
            return EnumStatus.ALREADY_EXISTS

        // add new language
        this.m_map_languages.put(lang_name, Language(lang_name))

        return EnumStatus.ADD_SUCCESS
    }

    fun remove_language(lang_name: String): EnumStatus {
        // check if chosen language doesn't exist
        find_language(lang_name) ?: return EnumStatus.DOES_NOT_EXIST

        // remove language from the map
        this.m_map_languages.remove(lang_name)

        return EnumStatus.REMOVE_SUCCESS
    }

    fun change_language(old_lang_name: String, new_lang_name: String): EnumStatus {
        val chosen_old_language: Language? = find_language(old_lang_name)
        val chosen_new_language: Language? = find_language(new_lang_name)

        // default language may not be changed
        if(old_lang_name == "")
            return EnumStatus.DEFAULT_TITLE_CHANGE

        // check if chosen language for doesn't exist
        if(chosen_old_language == null)
            return EnumStatus.DOES_NOT_EXIST

        // check if new title exists
        if(chosen_new_language != null)
            return EnumStatus.ALREADY_EXISTS

        // change language name
        chosen_old_language.m_lang_name = new_lang_name

        // add a language with the new name to the map
        this.m_map_languages.put(new_lang_name, chosen_old_language)

        // remove an old title
        this.m_map_languages.remove(old_lang_name)

        // set the language modified flag to true
        chosen_old_language.lang_modify()

        return EnumStatus.CHANGE_SUCCESS
    }
}