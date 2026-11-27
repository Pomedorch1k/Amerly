package com.example.amerly.ui.screens.phrases

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
import com.example.amerly.data.local.WordStorage
import com.example.amerly.data.model.WordEntry
import com.example.amerly.data.model.WordStatus
import kotlinx.coroutines.launch
import com.example.amerly.data.model.WordType
import com.example.amerly.ui.components.showAchievementNotifications

private data class PhraseItem(
    val phrase: String,
    val translation: String,
    val category: String,
    val example: String,
    val exampleTranslation: String
)

private val phrases = listOf(

    PhraseItem(
        "Sounds good",
        "Звучит отлично. / Хорошо, договорились.",
        "Разговорное",
        "That sounds good to me.",
        "Меня это устраивает."
    ),

    PhraseItem(
        "No worries",
        "Не переживай. / Ничего страшного.",
        "Разговорное",
        "No worries, I can help you.",
        "Не переживай, я могу тебе помочь."
    ),

    PhraseItem(
        "Hang out",
        "Проводить время вместе. / Тусоваться.",
        "Разговорное",
        "Do you want to hang out after school?",
        "Хочешь провести время вместе после занятий?"
    ),

    PhraseItem(
        "Figure out",
        "Разобраться. / Выяснить.",
        "Фразовый глагол",
        "I need to figure out this problem.",
        "Мне нужно разобраться с этой проблемой."
    ),

    PhraseItem(
        "I'm down",
        "Я за. / Я согласен.",
        "Разговорное",
        "I'm down for pizza tonight.",
        "Я за пиццу сегодня вечером."
    ),

    PhraseItem(
        "Catch up",
        "Пообщаться и узнать новости.",
        "Фразовый глагол",
        "Let's catch up this weekend.",
        "Давай пообщаемся на этих выходных."
    ),

    PhraseItem(
        "Give me a hand",
        "Помоги мне. / Окажи мне помощь.",
        "Выражение",
        "Can you give me a hand with this?",
        "Можешь мне с этим помочь?"
    ),

    PhraseItem(
        "That's on me",
        "Я угощаю. / Это за мой счёт.",
        "Разговорное",
        "Don't worry about the coffee. That's on me.",
        "Не переживай насчёт кофе. Я угощаю."
    )
)

@Composable
fun PhraseScreen() {

    val context = LocalContext.current

    val storage = remember {
        WordStorage(context)
    }

    val scope = rememberCoroutineScope()

    var savedPhrases by remember {
        mutableStateOf<Set<String>>(emptySet())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(Unit) {
        try {
            savedPhrases = storage.getAllWords()
                .map { it.word.lowercase() }
                .toSet()
        } catch (error: Exception) {
            errorMessage = "Не удалось загрузить словарь."
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Text(
            text = "Фразы и выражения",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Американский английский для реальных разговоров.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(22.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "🇺🇸 Разговорный английский",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Изучай выражения, которые встречаются в повседневной жизни американцев.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Библиотека",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "${phrases.size} фраз",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (isLoading) {
            CircularProgressIndicator()
        }

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        phrases.forEach { phrase ->

            val isSaved =
                phrase.phrase.lowercase() in savedPhrases

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = phrase.category,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = phrase.phrase,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = phrase.translation,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Пример:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = phrase.example,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = phrase.exampleTranslation,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                try {
                                    storage.addWord(
                                        WordEntry(
                                            word = phrase.phrase,
                                            type = WordType.EXPRESSION,
                                            contextSentence = phrase.example,
                                            source = "Библиотека Amerly",
                                            translation = phrase.translation,
                                            status = WordStatus.PROCESSED
                                        )
                                    )

                                    savedPhrases =
                                        savedPhrases + phrase.phrase.lowercase()

                                    errorMessage = null

                                } catch (error: Exception) {
                                    errorMessage =
                                        "Не удалось сохранить выражение."
                                }
                            }
                        },
                        enabled = !isSaved && !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isSaved) {
                                "✓ Добавлено в словарь"
                            } else {
                                "＋ Добавить в мой словарь"
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}
