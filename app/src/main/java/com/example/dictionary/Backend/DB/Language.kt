package com.example.dictionary.Backend.DB

import com.example.dictionary.Miscelaneous.EnumStatus

class Language(val m_lang_name: String): LanguageModifier() {
    var m_map_titles: MutableMap<String, Title> = mutableMapOf()
        get() = field
        private set

    init {
        add_helper("")
    }

    fun find_title(title_name: String): Title? {
        return this.m_map_titles[title_name]
    }

    fun add_title(title_name: String): EnumStatus {
        if(find_title(title_name) != null)
            return EnumStatus.ALREADY_EXISTS

        // add a new word
        add_helper(title_name)

        // set the language modified flag to true
        lang_modify()

        return EnumStatus.ADD_SUCCESS
    }

    fun remove_title(title_name: String): EnumStatus {
        // check if chosen title doesn't exist
        val chosen_title: Title = find_title(title_name) ?: return EnumStatus.DOES_NOT_EXIST

        // it is not possible to remove default title
        if(title_name == "")
            return EnumStatus.DEFAULT_TITLE_REMOVAL

        // remove a title from map
        remove_helper(chosen_title)

        // set the language modified flag to true
        lang_modify()

        return EnumStatus.REMOVE_SUCCESS
    }

    fun change_title(old_title_name: String, new_title_name: String): EnumStatus {
        val chosen_old_title: Title? = find_title(old_title_name)
        val chosen_new_title: Title? = find_title(new_title_name)

        // default title may not be changed
        if(old_title_name == "")
            return EnumStatus.DEFAULT_TITLE_CHANGE

        // check if chosen title for doesn't exist
        if(chosen_old_title == null)
            return EnumStatus.DOES_NOT_EXIST

        // check if new title exists
        if(chosen_new_title != null)
            return EnumStatus.ALREADY_EXISTS

        // remove an old title
        remove_helper(chosen_old_title)

        // add a new title
        add_helper(new_title_name)

        // set the language modified flag to true
        lang_modify()

        return EnumStatus.CHANGE_SUCCESS
    }

    private fun add_helper(title_name: String) {
        // add new title into the map
        this.m_map_titles.put(title_name, Title(title_name, this))
    }

    private fun remove_helper(chosen_title: Title) {
        // remove the title from the map
        this.m_map_titles.remove(chosen_title.m_title_name)
    }
}