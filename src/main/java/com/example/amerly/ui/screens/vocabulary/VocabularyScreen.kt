
package com.example.amerly.ui.screens.vocabulary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.amerly.data.local.WordStorage
import com.example.amerly.data.model.WordEntry
import com.example.amerly.data.model.WordStatus
import com.example.amerly.data.model.WordType
import com.example.amerly.ui.components.showAchievementNotifications
import kotlinx.coroutines.launch

private data class VocabularyItem(
    val word: String,
    val translation: String,
    val example: String,
    val exampleTranslation: String
)

private data class QuizQuestion(
    val correctWord: WordEntry,
    val options: List<WordEntry>
)

private val vocabulary = listOf(

    VocabularyItem(
        "Actually",
        "На самом деле",
        "I actually like this movie.",
        "На самом деле мне нравится этот фильм."
    ),

    VocabularyItem(
        "Although",
        "Хотя, несмотря на то что",
        "Although it was raining, we went outside.",
        "Хотя шёл дождь, мы вышли на улицу."
    ),

    VocabularyItem(
        "Improve",
        "Улучшать, совершенствовать",
        "I want to improve my English.",
        "Я хочу улучшить свой английский."
    ),

    VocabularyItem(
        "Enough",
        "Достаточно",
        "I have enough time.",
        "У меня достаточно времени."
    ),

    VocabularyItem(
        "Probably",
        "Вероятно, скорее всего",
        "I'll probably stay home today.",
        "Я, скорее всего, останусь сегодня дома."
    ),

    VocabularyItem(
        "Remember",
        "Помнить, вспоминать",
        "I remember that place.",
        "Я помню это место."
    ),

    VocabularyItem(
        "Believe",
        "Верить, считать",
        "I believe you.",
        "Я тебе верю."
    ),

    VocabularyItem(
        "Perhaps",
        "Возможно, может быть",
        "Perhaps we should wait.",
        "Возможно, нам стоит подождать."
    ),

    VocabularyItem(
        "Eventually",
        "В конечном итоге",
        "I will eventually find a solution.",
        "В конечном итоге я найду решение."
    ),

    VocabularyItem(
        "Instead",
        "Вместо этого",
        "Let's stay home instead.",
        "Давай вместо этого останемся дома."
    )
)

private fun createQuizQuestion(
    words: List<WordEntry>
): QuizQuestion? {

    // Для практики нужны слова с готовым переводом.
    // Одинаковые переводы не должны создавать повторяющиеся варианты.

    val pool = words
        .filter {
            it.type == WordType.WORD &&
                    it.translation.isNotBlank()
        }
        .distinctBy {
            it.translation.trim().lowercase()
        }

    if (pool.size < 4) {
        return null
    }

    val correct = pool.random()

    val wrongOptions = pool
        .filter { it.id != correct.id }
        .shuffled()
        .take(3)

    return QuizQuestion(
        correctWord = correct,
        options = (wrongOptions + correct).shuffled()
    )
}

@Composable
fun VocabularyScreen() {

    val context = LocalContext.current
    val storage = remember { WordStorage(context) }
    val scope = rememberCoroutineScope()

    var selectedSection by rememberSaveable {
        mutableStateOf("practice")
    }

    var savedWords by remember {
        mutableStateOf<List<WordEntry>>(emptyList())
    }

    var currentIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    var showTranslation by rememberSaveable {
        mutableStateOf(false)
    }

    var quizQuestion by remember {
        mutableStateOf<QuizQuestion?>(null)
    }

    var selectedAnswerId by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var isSaving by remember {
        mutableStateOf(false)
    }

    var message by remember {
        mutableStateOf<String?>(null)
    }

    // Загружаем личный словарь.

    LaunchedEffect(Unit) {

        try {
            savedWords = storage.getAllWords()
                .filter { it.type == WordType.WORD }
        } finally {
            isLoading = false
        }
    }

    val newWords = vocabulary.filter { item ->
        savedWords.none {
            it.word.trim().equals(item.word, ignoreCase = true)
        }
    }

    val currentWord = newWords.getOrNull(currentIndex)

    // Формируем вопрос при входе в практику
    // и после обновления личного словаря.

    LaunchedEffect(selectedSection, savedWords) {

        if (selectedSection == "practice") {
            quizQuestion = createQuizQuestion(savedWords)
            selectedAnswerId = null
            message = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Text(
            text = "Изучение слов",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Учи новые слова и проверяй свои знания.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(22.dp))

        // Переключение между новыми словами и практикой.

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            FilterChip(
                selected = selectedSection == "practice",
                onClick = {
                    selectedSection = "practice"
                    message = null
                },
                label = {
                    Text("Практика")
                },
                modifier = Modifier.weight(1f)
            )

            FilterChip(
                selected = selectedSection == "new",
                onClick = {
                    selectedSection = "new"
                    message = null
                },
                label = {
                    Text("Новые")
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        if (isLoading) {

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        } else if (selectedSection == "new") {

            // РАЗДЕЛ НОВЫХ СЛОВ

            Text(
                text = "Новые слова",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Доступно новых слов: ${newWords.size}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (currentWord == null) {

                Text(
                    text = "🎉",
                    fontSize = 45.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (newWords.isEmpty()) {
                        "Ты изучил все слова из этой подборки!"
                    } else {
                        "Все слова этой подборки просмотрены."
                    },
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )

            } else {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "🇺🇸",
                            fontSize = 35.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = currentWord.word,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        if (showTranslation) {

                            HorizontalDivider()

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = currentWord.translation,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = "Пример использования:",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = currentWord.example,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = currentWord.exampleTranslation,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedButton(
                    onClick = {
                        showTranslation = !showTranslation
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (showTranslation) {
                            "Скрыть перевод"
                        } else {
                            "Показать перевод"
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {

                        scope.launch {

                            isSaving = true

                            try {

                                val unlocked = storage.addWord(
                                    WordEntry(
                                        word = currentWord.word,
                                        translation = currentWord.translation,
                                        contextSentence = currentWord.example,
                                        source = "Учебная библиотека Amerly",
                                        type = WordType.WORD,
                                        status = WordStatus.PROCESSED
                                    )
                                )

                                savedWords = storage.getAllWords()
                                    .filter {
                                        it.type == WordType.WORD
                                    }

                                showAchievementNotifications(
                                    context,
                                    unlocked
                                )

                                currentIndex = 0
                                showTranslation = false
                                message = "Слово добавлено в твой словарь!"

                            } catch (e: Exception) {

                                message = "Не удалось сохранить слово."

                            } finally {
                                isSaving = false
                            }
                        }
                    },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (isSaving) {
                            "Сохранение..."
                        } else {
                            "＋ Добавить в мой словарь"
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(
                    onClick = {
                        currentIndex =
                            if (newWords.isEmpty() ||
                                currentIndex >= newWords.lastIndex
                            ) {
                                0
                            } else {
                                currentIndex + 1
                            }

                        showTranslation = false
                        message = null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Следующее слово →")
                }
            }

        } else {

            // РАЗДЕЛ ПРАКТИКИ

            Text(
                text = "Проверка знаний",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Вспомни значение сохранённого слова.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            val question = quizQuestion

            if (question == null) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Text(
                            text = "📚",
                            fontSize = 40.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Пока недостаточно слов",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Для практики нужны хотя бы 4 разных сохранённых слова с переводом. Добавь их во вкладке «Новые» или в личном словаре.",
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

            } else {

                Text(
                    text = "Как переводится это слово?",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = question.correctWord.word,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                question.options.forEachIndexed { index, option ->

                    val isCorrectOption =
                        option.id == question.correctWord.id

                    val isSelected =
                        selectedAnswerId == option.id

                    val containerColor = when {
                        selectedAnswerId == null ->
                            MaterialTheme.colorScheme.surface

                        isCorrectOption ->
                            MaterialTheme.colorScheme.primaryContainer

                        isSelected ->
                            MaterialTheme.colorScheme.errorContainer

                        else ->
                            MaterialTheme.colorScheme.surface
                    }

                    OutlinedButton(
                        onClick = {
                            selectedAnswerId = option.id

                            message = if (isCorrectOption) {
                                "Верно! Отличная работа."
                            } else {
                                "Не совсем. Правильный ответ: ${question.correctWord.translation}"
                            }
                        },
                        enabled = selectedAnswerId == null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = containerColor
                        )
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "${index + 1}.",
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = option.translation,
                                modifier = Modifier.weight(1f)
                            )

                            if (selectedAnswerId != null && isCorrectOption) {
                                Text("✓")
                            } else if (isSelected) {
                                Text("✕")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))
                }

                if (selectedAnswerId != null) {

                    Text(
                        text = message.orEmpty(),
                        color = if (
                            selectedAnswerId == question.correctWord.id
                        ) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            quizQuestion = createQuizQuestion(savedWords)
                            selectedAnswerId = null
                            message = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Следующее слово →")
                    }
                }
            }
        }

        message?.let {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
