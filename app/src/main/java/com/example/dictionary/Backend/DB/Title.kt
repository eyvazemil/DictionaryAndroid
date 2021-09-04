package com.example.dictionary.Backend.DB

import com.example.dictionary.Miscelaneous.EnumStatus

class Title(title_name: String, private var language: LanguageModifier) {
    var m_title_name: String = ""

    var m_map_words:  MutableMap<String, Word> = mutableMapOf()
        get() = field
        private set

    var m_list_words: MutableList<Word> = mutableListOf()
        get() = field
        private set

    init {
        this.m_title_name = title_name
    }

    fun find_word(word: String): Word? {
        return this.m_map_words[word]
    }

    fun add_word(word: String, definition: String, flag_read: Boolean = false): EnumStatus {
        if(find_word(word) != null)
            return EnumStatus.ALREADY_EXISTS

        // set the language modified flag to true
        if(!flag_read) {
            language.lang_modify()
            add_helper(word, definition)
        } else
            add_helper(word, definition, true)

        return EnumStatus.ADD_SUCCESS
    }

    fun remove_word(word: String): EnumStatus {
        // check if chosen word doesn't exist
        val chosen_word: Word = this.m_map_words[word] ?: return EnumStatus.DOES_NOT_EXIST

        // remove a word from list and map
        remove_helper(chosen_word)

        // set the language modified flag to true
        language.lang_modify()

        return EnumStatus.REMOVE_SUCCESS
    }

    fun change_word(old_word: String, new_word: String, new_definition: String): EnumStatus {
        val chosen_old_word: Word? = find_word(old_word)
        val chosen_new_word: Word? = find_word(new_word)

        // check if chosen word for doesn't exist
        if(chosen_old_word == null)
            return EnumStatus.DOES_NOT_EXIST

        // check if new word exists
        if(chosen_new_word != null && chosen_new_word === chosen_old_word && new_definition == chosen_old_word.m_definition)
            return EnumStatus.ALREADY_EXISTS

        // remove an old word
        remove_helper(chosen_old_word)

        // add a new word
        add_helper(new_word, new_definition)

        // set the language modified flag to true
        language.lang_modify()

        return EnumStatus.CHANGE_SUCCESS
    }

    private fun add_helper(word: String, definition: String, flag_read: Boolean = false) {
        // add new word into the list
        if(!flag_read)
            this.m_list_words.add(0, Word(word, definition))
        else
            this.m_list_words.add(Word(word, definition))

        // add new word into the map
        this.m_map_words.put(word, this.m_list_words[0])
    }

    private fun remove_helper(chosen_word: Word) {
        // remove the word from the list
        this.m_list_words.remove(chosen_word)

        // remove the word from the map
        this.m_map_words.remove(chosen_word.m_word)
    }
}