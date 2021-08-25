package com.example.dictionary.Backend.DB

abstract class LanguageModifier {
    var m_flag_modified: Boolean = false
        get() = field
        private set

    fun lang_modify() {
        this.m_flag_modified = true
    }
}