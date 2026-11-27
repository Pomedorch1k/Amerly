package com.example.amerly.data.local
import android.content.Context
import android.util.AtomicFile
import com.example.amerly.data.model.WordEntry
import com.example.amerly.data.model.WordStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import com.example.amerly.data.model.WordType
import com.example.amerly.data.local.AchievementStorage

class WordStorage(context: Context) {

    private val appContext = context.applicationContext

    private val file = AtomicFile(
        File(appContext.filesDir, "dictionary.json")
    )

    private val achievementStorage =
        AchievementStorage(appContext)
    suspend fun getAllWords(): List<WordEntry> =
        withContext(Dispatchers.IO) {
            readWords()
        }

    suspend fun updateWordTranslation(
        id: String,
        translation: String
    ) = withContext(Dispatchers.IO) {

        val words = readWords()

        val updatedWords = words.map { word ->

            if (word.id == id) {
                word.copy(
                    translation = translation,
                    status = WordStatus.PROCESSED
                )
            } else {
                word
            }
        }

        writeWords(updatedWords)
    }


    suspend fun addWord(word: WordEntry): List<String> =
        withContext(Dispatchers.IO) {

            val words = readWords()

            val alreadyExists = words.any {
                it.type == word.type &&
                        it.word.trim().equals(word.word.trim(), ignoreCase = true)
            }

            if (alreadyExists) {
                return@withContext emptyList()
            }

            val updatedWords = words + word
            writeWords(updatedWords)

            val unlockedAchievements = mutableListOf<String>()

            // Первое сохранённое слово или выражение
            val hasPreviousSameType = words.any {
                it.type == word.type
            }

            if (!hasPreviousSameType) {

                val achievementId = when (word.type) {
                    WordType.WORD -> "first_word"
                    WordType.EXPRESSION -> "first_expression"
                }

                if (achievementStorage.unlock(achievementId)) {
                    unlockedAchievements.add(achievementId)
                }
            }

            // 50 уникальных слов
            val uniqueWords = updatedWords
                .filter { it.type == WordType.WORD }
                .map { it.word.trim().lowercase() }
                .filter { it.isNotBlank() }
                .distinct()

            if (
                uniqueWords.size >= 50 &&
                achievementStorage.unlock("fifty_words")
            ) {
                unlockedAchievements.add("fifty_words")
            }

            unlockedAchievements
        }



    suspend fun deleteWord(id: String) =
        withContext(Dispatchers.IO) {
            val words = readWords()
            writeWords(words.filterNot { it.id == id })
        }

    private fun readWords(): List<WordEntry> {

        if (!file.baseFile.exists()) {
            return emptyList()
        }

        return try {

            val text = file.openRead()
                .bufferedReader()
                .use { it.readText() }

            val jsonArray = JSONArray(text)

            buildList {

                for (i in 0 until jsonArray.length()) {

                    val item = jsonArray.optJSONObject(i) ?: continue

                    val word = item.optString("word", "").trim()

                    if (word.isEmpty()) continue

                    val status = try {
                        WordStatus.valueOf(
                            item.optString("status", "PENDING")
                        )
                    } catch (_: IllegalArgumentException) {
                        WordStatus.PENDING
                    }

                    add(
                        WordEntry(
                            id = item.optString("id", ""),
                            word = word,
                            contextSentence = item.optString(
                                "contextSentence", ""
                            ),
                            source = item.optString("source", ""),
                            type = try {
                                val defaultType = if (
                                    item.optString("source") == "Библиотека Amerly"
                                ) {
                                    "EXPRESSION"
                                } else {
                                    "WORD"
                                }

                                WordType.valueOf(
                                    item.optString("type", defaultType)
                                )
                            } catch (_: IllegalArgumentException) {
                                WordType.WORD
                            },
                            translation = item.optString("translation", ""),
                            transcription = item.optString("transcription", ""),
                            status = status,
                            createdAt = item.optLong("createdAt", 0L)
                        )
                    )
                }
            }

        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun writeWords(words: List<WordEntry>) {

        val jsonArray = JSONArray()

        words.forEach { word ->

            val item = JSONObject().apply {
                put("id", word.id)
                put("word", word.word)
                put("contextSentence", word.contextSentence)
                put("source", word.source)
                put("type", word.type.name)
                put("translation", word.translation)
                put("transcription", word.transcription)
                put("status", word.status.name)
                put("createdAt", word.createdAt)
            }

            jsonArray.put(item)
        }

        val stream = file.startWrite()

        try {
            stream.write(
                jsonArray.toString(2).toByteArray(Charsets.UTF_8)
            )

            file.finishWrite(stream)

        } catch (error: Exception) {
            file.failWrite(stream)
            throw error
        }
    }
}
