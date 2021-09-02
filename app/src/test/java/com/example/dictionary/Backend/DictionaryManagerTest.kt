package com.example.dictionary.Backend

import com.example.dictionary.Miscelaneous.EnumStatus
import junit.framework.TestCase
import org.junit.Test

class DictionaryManagerTest : TestCase() {
    val dummy_dir: String = "./src/test/java/com/example/dictionary/Backend/"
    val dictionary_manager: DictionaryManager = DictionaryManager(dummy_dir)

    @Test
    fun testGet_languages() {
        dictionary_manager.initialize()

        val reference_lang_names: List<String> = listOf("French")

        dictionary_manager.get_languages().forEach {
            assert(reference_lang_names.contains(it))
        }

        dictionary_manager.finish()
    }

    @Test
    fun testGet_titles() {
        dictionary_manager.initialize()

        val reference_title_names: List<String> = listOf("")

        assert(dictionary_manager.choose_language("French") == EnumStatus.CHOOSE_SUCCESS)

        assert(dictionary_manager.get_titles().size == 1)
        dictionary_manager.get_titles().forEach {
            assert(reference_title_names.contains(it))
        }

        dictionary_manager.finish()
    }

    @Test
    fun testGet_words() {
        dictionary_manager.initialize()

        val reference_words: List<Pair<String, String>> = listOf(Pair("Coup d'etat", "ovethrow of the state"))

        assert(dictionary_manager.choose_language("French") == EnumStatus.CHOOSE_SUCCESS)
        assert(dictionary_manager.choose_title("") == EnumStatus.CHOOSE_SUCCESS)

        assert(dictionary_manager.get_words().size == 1)
        dictionary_manager.get_words().forEach {
            assert(reference_words.contains(it))
        }

        dictionary_manager.finish()
    }

    @Test
    fun testAdd_Remove_language() {
        dictionary_manager.initialize()

        assert(dictionary_manager.add_language("French") == EnumStatus.ALREADY_EXISTS)
        assert(dictionary_manager.add_language("German") == EnumStatus.ADD_SUCCESS)
        assert(dictionary_manager.remove_language("German") == EnumStatus.REMOVE_SUCCESS)
        assert(dictionary_manager.remove_language("German") == EnumStatus.DOES_NOT_EXIST)

        dictionary_manager.finish()
    }

    @Test
    fun testAdd_Change_Remove_title() {
        dictionary_manager.initialize()

        dictionary_manager.choose_language("French")
        assert(dictionary_manager.add_title("00") == EnumStatus.ADD_SUCCESS)
        assert(dictionary_manager.add_title("00") == EnumStatus.ALREADY_EXISTS)

        dictionary_manager.choose_title("00")

        assert(dictionary_manager.change_title_name("00", "02") == EnumStatus.CHANGE_SUCCESS)
        assert(dictionary_manager.change_title_name("02", "") == EnumStatus.ALREADY_EXISTS)
        assert(dictionary_manager.change_title_name("02", "00") == EnumStatus.CHANGE_SUCCESS)

        assert(dictionary_manager.remove_title("01") == EnumStatus.DOES_NOT_EXIST)
        assert(dictionary_manager.remove_title("00") == EnumStatus.REMOVE_SUCCESS)

        dictionary_manager.finish()
    }

    @Test
    fun testAdd_Change_Remove_word() {
        dictionary_manager.initialize()

        dictionary_manager.choose_language("French")
        dictionary_manager.choose_title("")

        assert(dictionary_manager.add_word("A", "B") == EnumStatus.ADD_SUCCESS)
        assert(dictionary_manager.add_word("A", "B") == EnumStatus.ALREADY_EXISTS)

        assert(dictionary_manager.change_word("A", "A", "B") == EnumStatus.ALREADY_EXISTS)
        assert(dictionary_manager.change_word("A", "A", "C") == EnumStatus.CHANGE_SUCCESS)
        assert(dictionary_manager.change_word("A", "B", "C") == EnumStatus.CHANGE_SUCCESS)
        assert(dictionary_manager.change_word("B", "A", "B") == EnumStatus.CHANGE_SUCCESS)

        assert(dictionary_manager.remove_word("B") == EnumStatus.DOES_NOT_EXIST)
        assert(dictionary_manager.remove_word("A") == EnumStatus.REMOVE_SUCCESS)

        dictionary_manager.finish()
    }

    @Test
    fun testFind_word() {
        dictionary_manager.initialize()

        dictionary_manager.choose_language("French")
        dictionary_manager.choose_title("")

        assert(dictionary_manager.find_word("Coup d'etat") != null)
        assert(dictionary_manager.find_word("A") == null)

        dictionary_manager.finish()
    }
}