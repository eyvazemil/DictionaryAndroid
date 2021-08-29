package com.example.dictionary.Backend

import com.example.dictionary.Backend.DB.*
import java.io.File
import java.io.FileWriter

class FileReadWrite(private val dir: String) {
    private fun set_language_path(lang_name: String): String {
        return "${dir}${lang_name}.txt"
    }

    fun read_dir(allowed_extensions: Set<String>? = null): List<String> {
        val file_names: MutableList<String> = mutableListOf()

        // read the directory
        File(dir).walk().forEach {
            // truncate ".txt" extension from file name
            if(allowed_extensions != null && it.name.length > 4 &&
                allowed_extensions.contains(it.name.substring(it.name.length - 4))
            ) {
                file_names.add(it.name.substring(0, it.name.length - 4))
            }
        }

        return file_names
    }

    fun read(lang_name: String): Language {
        // language object
        val language: Language = Language(lang_name)

        // currently chosen title for language
        var chosen_title: Title? = null

        // read the file's contents string
        File(set_language_path(lang_name)).readLines().forEach {
            if((it.length == 1 && it[it.length - 1] == ':') ||
                (it.length > 1 && it[it.length - 1] == ':' && it[it.length - 2] != '\\')
            ) { // title
                val title_name: String = derefactor(it.substring(0, it.length - 1), setOf(':'))

                // add new title
                language.add_title(title_name)

                // choose the newly added title
                chosen_title = language.find_title(title_name)
            } else { // word
                val word: String = derefactor(it.substringBefore(" - "), setOf(':', '-'))
                val definition: String = derefactor(it.substringAfter(" - "), setOf(':', '-'))

                // add word to the language
                chosen_title?.add_word(word, definition)
            }
        }

        return language
    }

    fun write(language: Language) {
        // open file for writing
        val fileWriter = FileWriter(set_language_path(language.m_lang_name))

        // string with all titles and words in them in refactored format
        var file_string: String = ""

        // traverse through titles
        for(title in language.m_map_titles.values) {
            // refactor title name
            val refactored_title_name: String = refactor(title.m_title_name, setOf(':'))

            // write title name into the final string
            file_string += "$refactored_title_name:\n"

            // traverse through words in this title
            for(i in (title.m_list_words.size - 1) downTo 0)
                file_string += refactor(title.m_list_words[i].m_word, setOf(':', '-')) + " - " + refactor(title.m_list_words[i].m_definition, setOf(':', '-')) + "\n"
        }

        // write the refactored string into the file
        if(file_string == "")
            File(set_language_path(language.m_lang_name)).createNewFile()
        else
            fileWriter.write(file_string)

        // close file
        fileWriter.close()
    }

    private fun refactor(line: String, flag_chars: Set<Char>): String {
        // string where refactored line will be stored
        var refactored_str: String = ""

        // refactor the line
        for(i in line.indices) {
            if(flag_chars.contains(line[i]))
                refactored_str += '\\'
            refactored_str += line[i]
        }

        return refactored_str
    }

    private fun derefactor(line: String, flag_chars: Set<Char>): String {
        // string where refactored line will be stored
        var derefactored_str: String = ""

        // refactor the line
        for(i in line.indices) {
            if(i != line.length - 1 && line[i] == '\\' && flag_chars.contains(line[i + 1]))
                continue
            derefactored_str += line[i]
        }

        return derefactored_str
    }
}