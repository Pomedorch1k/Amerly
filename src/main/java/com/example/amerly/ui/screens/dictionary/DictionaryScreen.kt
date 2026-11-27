package com.example.amerly.ui.screens.dictionary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.amerly.data.local.WordStorage
import com.example.amerly.data.model.WordEntry
import com.example.amerly.data.model.WordStatus
import com.example.amerly.data.model.WordType
import kotlinx.coroutines.launch
import com.example.amerly.ui.components.showAchievementNotifications
import com.example.amerly.data.remote.MyMemoryTranslationService

@Composable
fun DictionaryScreen() {

    val context = LocalContext.current
    val storage = remember { WordStorage(context) }
    val scope = rememberCoroutineScope()

    var words by remember {
        mutableStateOf<List<WordEntry>>(emptyList())
    }

    var selectedType by remember {
        mutableStateOf(WordType.WORD)
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }
    var translatingIds by remember {
        mutableStateOf<Set<String>>(emptySet())
    }
    LaunchedEffect(Unit) {
        try {
            words = storage.getAllWords()
            isLoading = false

            val pendingWords = words.filter {
                it.status == WordStatus.PENDING &&
                        it.translation.isBlank()
            }

            for (pendingWord in pendingWords) {
                translatingIds = translatingIds + pendingWord.id

                try {
                    val translation =
                        MyMemoryTranslationService.translate(pendingWord.word)

                    storage.updateWordTranslation(
                        pendingWord.id,
                        translation
                    )

                } catch (e: Exception) {
                    errorMessage =
                        "Перевод временно недоступен. " +
                                "Попробуем снова при следующем открытии словаря."

                    break

                } finally {
                    translatingIds = translatingIds - pendingWord.id
                }
            }

            words = storage.getAllWords()

        } catch (e: Exception) {
            errorMessage = "Не удалось загрузить словарь."
        } finally {
            isLoading = false
        }
    }

    val filteredWords = words.filter {
        it.type == selectedType
    }

    val wordCount = words.count {
        it.type == WordType.WORD
    }

    val expressionCount = words.count {
        it.type == WordType.EXPRESSION
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Мой словарь",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Твои сохранённые слова и выражения.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(22.dp))

        // Переключение между категориями

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            FilterChip(
                selected = selectedType == WordType.WORD,
                onClick = {
                    selectedType = WordType.WORD
                },
                label = {
                    Text("Слова ($wordCount)")
                },
                modifier = Modifier.weight(1f)
            )

            FilterChip(
                selected = selectedType == WordType.EXPRESSION,
                onClick = {
                    selectedType = WordType.EXPRESSION
                },
                label = {
                    Text("Выражения ($expressionCount)")
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Заголовок выбранной категории

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = if (selectedType == WordType.WORD) {
                    "Мои слова"
                } else {
                    "Мои выражения"
                },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Button(
                onClick = {
                    showDialog = true
                }
            ) {
                Text("＋ Добавить")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        when {

            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            filteredWords.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = if (selectedType == WordType.WORD) {
                                "📖"
                            } else {
                                "💬"
                            },
                            fontSize = 50.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (selectedType == WordType.WORD) {
                                "Здесь пока нет слов"
                            } else {
                                "Здесь пока нет выражений"
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Добавь свою первую запись.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            else -> {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        items = filteredWords,
                        key = { it.id }
                    ) { word ->

                        WordCard(
                            word = word,
                            isTranslating = word.id in translatingIds,
                            onDelete = {
                                scope.launch {
                                    try {
                                        storage.deleteWord(word.id)
                                        words = storage.getAllWords()
                                        errorMessage = null
                                    } catch (e: Exception) {
                                        errorMessage = "Не удалось удалить запись."
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {

        AddWordDialog(
            isExpression = selectedType == WordType.EXPRESSION,
            onDismiss = {
                showDialog = false
            },
            onSave = { word, sentence, source ->
                scope.launch {
                    try {
                        val existingWords = storage.getAllWords()

                        val duplicate = existingWords.any {
                            it.type == selectedType &&
                                    it.word.trim().equals(word.trim(), ignoreCase = true)
                        }

                        if (duplicate) {
                            errorMessage = "Такая запись уже есть в словаре."
                            showDialog = false
                            return@launch
                        }

                        val newEntry = WordEntry(
                            word = word,
                            contextSentence = sentence,
                            source = source,
                            type = selectedType,
                            status = WordStatus.PENDING
                        )

                        val unlocked = storage.addWord(newEntry)

                        showAchievementNotifications(context, unlocked)

                        words = storage.getAllWords()
                        showDialog = false
                        errorMessage = null

                        translatingIds = translatingIds + newEntry.id

                        try {
                            val translation =
                                MyMemoryTranslationService.translate(newEntry.word)

                            storage.updateWordTranslation(
                                newEntry.id,
                                translation
                            )

                        } catch (e: Exception) {
                            errorMessage =
                                "Слово сохранено, но перевод пока недоступен. " +
                                        "Попробуем снова при следующем открытии словаря."

                        } finally {
                            translatingIds = translatingIds - newEntry.id
                            words = storage.getAllWords()
                        }

                    } catch (e: Exception) {
                        errorMessage = "Не удалось сохранить запись."
                    }
                }
            }
        )
    }
}

@Composable
fun WordCard(
    word: WordEntry,
    isTranslating: Boolean = false,
    onDelete: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = word.word,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                TextButton(
                    onClick = onDelete
                ) {
                    Text(
                        text = "Удалить",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (word.translation.isNotBlank()) {
                Text(
                    text = word.translation,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (word.contextSentence.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = word.contextSentence,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (word.source.isNotBlank()) {
                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Источник: ${word.source}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = when {
                    isTranslating -> "Получаем перевод..."
                    word.status == WordStatus.PENDING -> "Ожидает перевода"
                    word.status == WordStatus.PROCESSED -> "Перевод готов"
                    else -> "Ошибка обработки"
                },
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun AddWordDialog(
    isExpression: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {

    var word by remember {
        mutableStateOf("")
    }

    var sentence by remember {
        mutableStateOf("")
    }

    var source by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                if (isExpression) {
                    "Добавить выражение"
                } else {
                    "Добавить слово"
                }
            )
        },

        text = {

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedTextField(
                    value = word,
                    onValueChange = { word = it },
                    label = {
                        Text(
                            if (isExpression) {
                                "Английское выражение"
                            } else {
                                "Английское слово"
                            }
                        )
                    },
                    singleLine = true
                )

                OutlinedTextField(
                    value = sentence,
                    onValueChange = { sentence = it },
                    label = {
                        Text("Пример предложения")
                    }
                )

                OutlinedTextField(
                    value = source,
                    onValueChange = { source = it },
                    label = {
                        Text("Источник (необязательно)")
                    },
                    singleLine = true
                )
            }
        },

        confirmButton = {

            TextButton(
                onClick = {
                    onSave(
                        word.trim(),
                        sentence.trim(),
                        source.trim()
                    )
                },
                enabled = word.isNotBlank()
            ) {
                Text("Сохранить")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("Отмена")
            }
        }
    )
}
