package com.example.dictionary.Backend.DB

import com.example.dictionary.Backend.DB.*
import com.example.dictionary.Miscelaneous.EnumStatus
import junit.framework.TestCase
import org.junit.Test

class TitleTest : TestCase() {
    private val language: Language = Language("Dummy_language")
    private val title: Title = Title("Dummy_title", language)

    @Test
    fun testAdd_Find_Remove_word() {
        assert(title.add_word("Hello", "World") == EnumStatus.ADD_SUCCESS)
        assert(title.add_word("A", "B") == EnumStatus.ADD_SUCCESS)
        assert(title.add_word("A", "C") == EnumStatus.ALREADY_EXISTS)

        assert(title.find_word("B") == null)
        assert(title.find_word("A") != null)

        assert(title.remove_word("B") == EnumStatus.DOES_NOT_EXIST)
        assert(title.remove_word("A") == EnumStatus.REMOVE_SUCCESS)
    }

    @Test
    fun testChange_word() {
        assert(title.add_word("A", "B") == EnumStatus.ADD_SUCCESS)
        assert(title.add_word("F", "G") == EnumStatus.ADD_SUCCESS)

        assert(title.m_list_words[0].m_word == "F")
        assert(title.m_list_words[1].m_word == "A")

        assert(title.change_word("A", "A", "B") == EnumStatus.ALREADY_EXISTS)
        assert(title.change_word("C", "A", "C") == EnumStatus.DOES_NOT_EXIST)
        assert(title.change_word("A", "A", "C") == EnumStatus.CHANGE_SUCCESS)
        assert(title.change_word("A", "B", "C") == EnumStatus.CHANGE_SUCCESS)

        assert(title.m_list_words[0].m_word == "B")
        assert(title.m_list_words[1].m_word == "F")
    }
}