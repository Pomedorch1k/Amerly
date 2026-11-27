
package com.example.amerly.data.model

import java.util.UUID

enum class WordStatus {
    PENDING,
    PROCESSED,
    FAILED
}

enum class WordType {
    WORD,
    EXPRESSION
}

data class WordEntry(
    val id: String = UUID.randomUUID().toString(),
    val word: String,
    val contextSentence: String = "",
    val source: String = "",
    val translation: String = "",
    val transcription: String = "",
    val type: WordType = WordType.WORD,
    val status: WordStatus = WordStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)
