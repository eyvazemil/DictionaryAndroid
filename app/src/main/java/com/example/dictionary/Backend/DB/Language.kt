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
        find_title(title_name) ?: return EnumStatus.DOES_NOT_EXIST

        // it is not possible to remove default title
        if(title_name == "")
            return EnumStatus.DEFAULT_TITLE_REMOVAL

        // remove a title from map
        remove_helper(title_name)

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

        // change title name
        chosen_old_title.m_title_name = new_title_name

        // add a title with the new name to the map
        add_helper(new_title_name, chosen_old_title)

        // remove an old title
        remove_helper(old_title_name)

        // set the language modified flag to true
        lang_modify()

        return EnumStatus.CHANGE_SUCCESS
    }

    private fun add_helper(title_name: String, title: Title? = null) {
        // add title into the map
        if(title == null)
            this.m_map_titles.put(title_name, Title(title_name, this))
        else
            this.m_map_titles.put(title_name, title)
    }

    private fun remove_helper(title_name: String) {
        // remove the title from the map
        this.m_map_titles.remove(title_name)
    }
}