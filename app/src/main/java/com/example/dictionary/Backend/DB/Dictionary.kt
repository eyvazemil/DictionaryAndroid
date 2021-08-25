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
}