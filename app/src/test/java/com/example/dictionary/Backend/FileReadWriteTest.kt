package com.example.dictionary.Backend

import com.example.dictionary.Backend.DB.Language
import junit.framework.TestCase
import org.junit.Test
import java.io.File

class FileReadWriteTest : TestCase() {
    val dummy_dir: String = "./src/test/java/com/example/dictionary/Backend/DummyLanguagesDir/"
    val file_read_write: FileReadWrite = FileReadWrite(dummy_dir)

    fun create_dummy_dir(dir: String) {
        File(dir).mkdirs()
    }

    fun remove_dummy_dir(dir: String) {
        File(dir).deleteRecursively()
    }

    fun create_dummy_file(file_path: String, file_contents: String? = null) {
        File(file_path).createNewFile()
        File(file_path).writeText(file_contents ?: "")
    }

    fun read_file(file_path: String): String {
        return File(file_path).readText()
    }

    @Test
    fun testRead_dir() {
        create_dummy_dir(dummy_dir)

        // list with expected file names
        val list_file_names: List<String> = listOf("0", "1", "2", "3", "4")

        // create dummy files
        for(i in 0..4)
            create_dummy_file("${dummy_dir}${i}.txt")

        // read the directory
        val list: List<String> = file_read_write.read_dir(setOf(".txt"))
        for(i in 0..4)
            assert(list_file_names.contains(list[i]))

        remove_dummy_dir(dummy_dir)
    }

    @Test
    fun testRead() {
        create_dummy_dir(dummy_dir)

        // string with test file's contents
        val file_contents: String = """
            :
            Hello - World
            A - B
            C - V
            A\-\B - l\:V\:
            00:
            Halo - Hello
            a - b
            
        """.trimIndent()

        val file_name: String = "0"

        // create a new file and write into it
        create_dummy_file("${dummy_dir}${file_name}.txt", file_contents)

        // read the file via reader into language object
        val language: Language = file_read_write.read(file_name)

        // check correctness of language object
        assert(language.m_map_titles.size == 2 && language.m_map_titles.containsKey("") && language.m_map_titles.containsKey("00"))
        assert(language.m_map_titles.getValue("").m_list_words.size == 4 &&
                language.m_map_titles.getValue("").m_list_words[3].m_word == "Hello" &&
                language.m_map_titles.getValue("").m_list_words[2].m_word == "A" &&
                language.m_map_titles.getValue("").m_list_words[1].m_word == "C" &&
                language.m_map_titles.getValue("").m_list_words[0].m_word == "A-\\B"
        )
        assert(language.m_map_titles.getValue("00").m_list_words.size == 2 &&
                language.m_map_titles.getValue("00").m_list_words[1].m_word == "Halo" &&
                language.m_map_titles.getValue("00").m_list_words[0].m_word == "a"
        )

        remove_dummy_dir(dummy_dir)
    }

    @Test
    fun testWrite() {
        create_dummy_dir(dummy_dir)

        val lang_name: String = "0"

        // create language object
        val language: Language = Language(lang_name)
        language.add_title("00")
        language.find_title("")?.add_word("Hello", "World")
        language.find_title("")?.add_word("A", "B")
        language.find_title("")?.add_word("C", "V")
        language.find_title("")?.add_word("A-\\B", "l:V:")
        language.add_title("00")
        language.find_title("00")?.add_word("Halo", "Hello")
        language.find_title("00")?.add_word("a", "b")

        // write language into the file
        file_read_write.write(language)

        // reference file contents
        val file_contents: String = """
            :
            Hello - World
            A - B
            C - V
            A\-\B - l\:V\:
            00:
            Halo - Hello
            a - b
            
        """.trimIndent()

        // read the file contents and compare with reference
        assert(read_file("${dummy_dir}${lang_name}.txt") == file_contents)

        remove_dummy_dir(dummy_dir)
    }

    @Test
    fun testGetModificationDates() {
        create_dummy_dir(dummy_dir)

        create_dummy_file("${dummy_dir}dummy_file.txt")

        println("Dummy file's last modification date: ${file_read_write.get_modification_dates(setOf(".txt"))}")

        remove_dummy_dir(dummy_dir)
    }
}