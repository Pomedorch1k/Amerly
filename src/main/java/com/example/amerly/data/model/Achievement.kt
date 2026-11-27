
package com.example.amerly.data.model

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String
)

object AchievementCatalog {

    val all = listOf(

        Achievement(
            id = "first_launch",
            title = "А это точно не черный",
            description = "Ты впервые вошли в Amerly.",
            emoji = "🌚"
        ),

        Achievement(
            id = "first_word",
            title = "Слово за слово",
            description = "Ты сохранил своё первое английское слово.",
            emoji = "📖"
        ),

        Achievement(
            id = "first_expression",
            title = "Фраза не воробей",
            description = "Ты сохранил своё первое выражение.",
            emoji = "💬"
        ),

        Achievement(
            id = "first_grammar",
            title = "To Be Continued",
            description = "Ты правильно ответил на первое задание.",
            emoji = "🧠"
        ),

        Achievement(
            id = "first_dialogue",
            title = "Уши развесил",
            description = "Ты впервые послушал английский диалог.",
            emoji = "🎧"
        ),

        Achievement(
            id = "fifty_words",
            title = "Словарный запас или склад?",
            description = "Ты собрал 50 английских слов.",
            emoji = "🏆"
        )
    )
}
