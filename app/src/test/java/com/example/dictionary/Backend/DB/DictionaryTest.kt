package com.example.dictionary.Backend.DB

import com.example.dictionary.Miscelaneous.EnumStatus
import junit.framework.TestCase
import org.junit.Test

class DictionaryTest : TestCase() {
    private val dictionary: Dictionary = Dictionary()

    @Test
    fun testAdd_Find_Remove_language() {
        assert(dictionary.add_language("A") == EnumStatus.ADD_SUCCESS)
        assert(dictionary.add_language("A") == EnumStatus.ALREADY_EXISTS)
        assert(dictionary.add_language("B") == EnumStatus.ADD_SUCCESS)
        assert(dictionary.add_language("C") == EnumStatus.ADD_SUCCESS)

        assert(dictionary.find_language("A") != null)
        assert(dictionary.find_language("V") == null)

        assert(dictionary.remove_language("R") == EnumStatus.DOES_NOT_EXIST)
        assert(dictionary.remove_language("C") == EnumStatus.REMOVE_SUCCESS)

        assert(dictionary.m_map_languages.size == 2)
    }
}