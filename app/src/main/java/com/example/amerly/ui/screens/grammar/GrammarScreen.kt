
package com.example.amerly.ui.screens.grammar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.amerly.data.local.AchievementStorage
import com.example.amerly.ui.components.showAchievementNotifications

private data class GrammarQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String
)

private data class GrammarLesson(
    val title: String,
    val level: String,
    val description: String,
    val examples: List<Pair<String, String>>,
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String,
    val additionalQuestions: List<GrammarQuestion> = emptyList()
)

private val lessons = listOf(

    GrammarLesson(
        title = "Глагол To Be",
        level = "Начальный уровень",
        description = """
            To Be переводится как «быть» или «являться».
            
            В настоящем времени используются три формы:
            
            I am — я
            He / She / It is — он / она / оно
            You / We / They are — ты / вы / мы / они
            
            To Be используется, когда мы говорим о состоянии,
            профессии, возрасте или характеристике человека.
        """.trimIndent(),
        examples = listOf(
            "I am a student." to "Я студент.",
            "She is happy." to "Она счастлива.",
            "They are friends." to "Они друзья."
        ),
        question = "Выбери правильный вариант:\nShe ___ my friend.",
        options = listOf("am", "is", "are"),
        correctAnswer = "is",
        explanation = "С She используется форма is."
    ),

    GrammarLesson(
        title = "Present Simple",
        level = "Начальный уровень",
        description = """
            Present Simple используется для регулярных действий,
            привычек, фактов и расписаний.
            
            Например, когда рассказываешь, что делаешь каждый день.
            
            Важное правило:
            С he, she, it к обычному глаголу добавляется -s или -es.
            
            I work.
            She works.
        """.trimIndent(),
        examples = listOf(
            "I play games every day." to "Я играю в игры каждый день.",
            "She likes music." to "Ей нравится музыка.",
            "We study English." to "Мы изучаем английский."
        ),
        question = "Выбери правильный вариант:\nHe ___ music every day.",
        options = listOf("listen", "listens", "listening"),
        correctAnswer = "listens",
        explanation = "С He в Present Simple добавляем -s к глаголу."
    ),

    GrammarLesson(
        title = "Present Continuous",
        level = "Начальный уровень",
        description = """
            Present Continuous используется для действий,
            которые происходят прямо сейчас или в текущий период.
            
            Формула:
            am / is / are + глагол с окончанием -ing.
            
            Например:
            I am working.
            She is reading.
            They are playing.
        """.trimIndent(),
        examples = listOf(
            "I am watching a movie." to "Я смотрю фильм.",
            "She is reading a book." to "Она читает книгу.",
            "They are playing football." to "Они играют в футбол."
        ),
        question = "Выбери правильный вариант:\nI ___ learning English now.",
        options = listOf("am", "is", "are"),
        correctAnswer = "am",
        explanation = "С I используется am."
    ),

    GrammarLesson(
        title = "Артикли A / An",
        level = "Начальный уровень",
        description = """
            A и An используются перед исчисляемыми
            существительными в единственном числе.
            
            A ставится перед согласным звуком.
            
            An ставится перед гласным звуком.
            
            Важно: мы ориентируемся на звук, а не просто
            на первую букву слова.
        """.trimIndent(),
        examples = listOf(
            "A book." to "Книга.",
            "An apple." to "Яблоко.",
            "A university." to "Университет."
        ),
        question = "Какой артикль нужен?\nI have ___ apple.",
        options = listOf("a", "an", "the"),
        correctAnswer = "an",
        explanation = "Apple начинается с гласного звука, поэтому используем An."
    )
)

@Composable
fun GrammarScreen() {
    val context = LocalContext.current

    val achievementStorage = remember {
        AchievementStorage(context)
    }
    var selectedLesson by remember {
        mutableStateOf<GrammarLesson?>(null)
    }

    var selectedAnswer by remember {
        mutableStateOf<String?>(null)
    }

    if (selectedLesson == null) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {

            Text(
                text = "Грамматика",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Разбирай английский постепенно и без сложных объяснений.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(22.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "Твоя программа обучения",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Начни с основ и постепенно переходи к более сложным темам.",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Начальный уровень",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            lessons.forEachIndexed { index, lesson ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedLesson = lesson
                            selectedAnswer = null
                        },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {

                    Row(
                        modifier = Modifier.padding(17.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {

                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(13.dp)
                        ) {
                            Text(
                                text = "${index + 1}",
                                modifier = Modifier.padding(
                                    horizontal = 15.dp,
                                    vertical = 12.dp
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = lesson.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(5.dp))

                            Text(
                                text = lesson.level,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = "›",
                            fontSize = 25.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

    } else {

        val lesson = selectedLesson!!

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {

            TextButton(
                onClick = {
                    selectedLesson = null
                    selectedAnswer = null
                }
            ) {
                Text("← Назад к урокам")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = lesson.title,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = lesson.level,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(22.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = lesson.description,
                    modifier = Modifier.padding(18.dp),
                    fontSize = 15.sp,
                    lineHeight = 25.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Примеры",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            lesson.examples.forEach { example ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(15.dp)
                    ) {
                        Text(
                            text = example.first,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = example.second,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Проверка знаний",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = lesson.question,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            lesson.options.forEach { option ->

                OutlinedButton(
                    onClick = {
                        selectedAnswer = option

                        if (
                            option == lesson.correctAnswer &&
                            achievementStorage.unlock("first_grammar")
                        ) {
                            showAchievementNotifications(
                                context,
                                listOf("first_grammar")
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedAnswer == option) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    )
                ) {
                    Text(option)
                }

                Spacer(modifier = Modifier.height(5.dp))
            }

            selectedAnswer?.let { answer ->

                val correct = answer == lesson.correctAnswer

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (correct) {
                        "Правильно! Отличная работа."
                    } else {
                        "Пока неправильно. Правильный ответ: ${lesson.correctAnswer}"
                    },
                    color = if (correct) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = lesson.explanation,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(25.dp))
        }
    }
}
