package com.example.dictionary.Backend.DB

import com.example.dictionary.Miscelaneous.EnumStatus
import junit.framework.TestCase
import org.junit.Test

class LanguageTest : TestCase() {
    private val language: Language = Language("Dummy_language")

    @Test
    fun testAdd_Find_Remove_title() {
        assert(language.add_title("A") == EnumStatus.ADD_SUCCESS)
        assert(language.add_title("B") == EnumStatus.ADD_SUCCESS)
        assert(language.add_title("A") == EnumStatus.ALREADY_EXISTS)

        assert(language.find_title("C") == null)
        assert(language.find_title("A") != null)

        assert(language.remove_title("C") == EnumStatus.DOES_NOT_EXIST)
        assert(language.remove_title("A") == EnumStatus.REMOVE_SUCCESS)
        assert(language.remove_title("B") == EnumStatus.REMOVE_SUCCESS)
        assert(language.remove_title("A") == EnumStatus.DOES_NOT_EXIST)
        assert(language.remove_title("") == EnumStatus.DEFAULT_TITLE_REMOVAL)
    }

    @Test
    fun testChange_title() {
        assert(language.add_title("A") == EnumStatus.ADD_SUCCESS)
        assert(language.add_title("B") == EnumStatus.ADD_SUCCESS)

        assert(language.change_title("C", "A") == EnumStatus.DOES_NOT_EXIST)
        assert(language.change_title("B", "A") == EnumStatus.ALREADY_EXISTS)
        assert(language.change_title("A", "F") == EnumStatus.CHANGE_SUCCESS)
    }
}