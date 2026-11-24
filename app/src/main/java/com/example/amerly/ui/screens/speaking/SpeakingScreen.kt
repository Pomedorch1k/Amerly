
package com.example.amerly.ui.screens.speaking

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import com.example.amerly.data.local.AchievementStorage
import com.example.amerly.ui.components.showAchievementNotifications

private data class DialogueLine(
    val speaker: String,
    val english: String,
    val russian: String
)

private data class Dialogue(
    val id: String,
    val title: String,
    val situation: String,
    val lines: List<DialogueLine>
)

private val dialogues = listOf(

    Dialogue(
        id = "coffee",
        title = "At a Coffee Shop",
        situation = "Заказ кофе в американской кофейне",
        lines = listOf(
            DialogueLine(
                "Barista",
                "Hi! What can I get for you?",
                "Привет! Что вам предложить?"
            ),
            DialogueLine(
                "Customer",
                "Could I get a small iced coffee, please?",
                "Можно мне маленький холодный кофе, пожалуйста?"
            ),
            DialogueLine(
                "Barista",
                "Sure! Anything else?",
                "Конечно! Что-нибудь ещё?"
            ),
            DialogueLine(
                "Customer",
                "That's all, thanks.",
                "Это всё, спасибо."
            ),
            DialogueLine(
                "Barista",
                "No problem. Have a great day!",
                "Без проблем. Хорошего дня!"
            )
        )
    ),

    Dialogue(
        id = "meeting",
        title = "Meeting Someone",
        situation = "Знакомство с новым человеком",
        lines = listOf(
            DialogueLine(
                "Alex",
                "Hey! Is this seat taken?",
                "Привет! Это место занято?"
            ),
            DialogueLine(
                "Sam",
                "No, go ahead.",
                "Нет, садись."
            ),
            DialogueLine(
                "Alex",
                "Thanks! I'm Alex, by the way.",
                "Спасибо! Кстати, я Алекс."
            ),
            DialogueLine(
                "Sam",
                "Nice to meet you, Alex. I'm Sam.",
                "Приятно познакомиться, Алекс. Я Сэм."
            ),
            DialogueLine(
                "Alex",
                "Nice to meet you too!",
                "Мне тоже приятно познакомиться!"
            )
        )
    ),

    Dialogue(
        id = "weekend",
        title = "Weekend Plans",
        situation = "Планы на выходные",
        lines = listOf(
            DialogueLine(
                "Mike",
                "Hey, do you have any plans for the weekend?",
                "Привет, у тебя есть планы на выходные?"
            ),
            DialogueLine(
                "Chris",
                "Not really. Why?",
                "Особо нет. А что?"
            ),
            DialogueLine(
                "Mike",
                "A few friends and I are going to the movies.",
                "Мы с несколькими друзьями собираемся в кино."
            ),
            DialogueLine(
                "Chris",
                "Sounds good! Can I come?",
                "Звучит отлично! Можно мне с вами?"
            ),
            DialogueLine(
                "Mike",
                "Of course! I'll text you the details.",
                "Конечно! Я напишу тебе подробности."
            )
        )
    )
)

@Composable
fun SpeakingScreen() {

    val context = LocalContext.current.applicationContext
    val achievementStorage = remember {
        AchievementStorage(context)
    }

    var selectedDialogueId by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var ttsEngine by remember {
        mutableStateOf<TextToSpeech?>(null)
    }

    var isTtsReady by remember {
        mutableStateOf(false)
    }
    val preferences = remember {
        context.getSharedPreferences("speaking_settings", 0)
    }

    var speechRate by remember {
        mutableFloatStateOf(
            preferences.getFloat("speech_rate", 1.0f)
        )
    }
    DisposableEffect(context) {

        var engine: TextToSpeech? = null

        engine = TextToSpeech(context) { status ->

            if (status == TextToSpeech.SUCCESS) {

                val result = engine?.setLanguage(Locale.US)

                isTtsReady = result != null &&
                        result != TextToSpeech.LANG_MISSING_DATA &&
                        result != TextToSpeech.LANG_NOT_SUPPORTED
            }
        }

        ttsEngine = engine

        onDispose {

            engine?.stop()
            engine?.shutdown()

            ttsEngine = null
            isTtsReady = false
        }
    }

    val selectedDialogue = dialogues.find {
        it.id == selectedDialogueId
    }

    val speak: (String) -> Unit = { text ->

        if (isTtsReady && ttsEngine != null) {
            ttsEngine?.setSpeechRate(speechRate)
            val result = ttsEngine?.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "amerly_dialogue"
            )

            if (
                result == TextToSpeech.SUCCESS &&
                achievementStorage.unlock("first_dialogue")
            ) {
                showAchievementNotifications(
                    context,
                    listOf("first_dialogue")
                )
            }
        }
    }

    if (selectedDialogue == null) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {

            Text(
                text = "Говорение",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Слушай и изучай реальные разговорные ситуации.",
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
                        text = "🇺🇸 American English",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(7.dp))

                    Text(
                        text = "Изучай английский через повседневные диалоги. Слушай реплики и смотри их перевод.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Библиотека диалогов",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            dialogues.forEach { dialogue ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedDialogueId = dialogue.id
                        },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {

                        Text(
                            text = "ДИАЛОГ",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(7.dp))

                        Text(
                            text = dialogue.title,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = dialogue.situation,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "${dialogue.lines.size} реплик  ·  Открыть →",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

    } else {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {

            TextButton(
                onClick = {
                    ttsEngine?.stop()
                    selectedDialogueId = null
                }
            ) {
                Text("← Назад к диалогам")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = selectedDialogue.title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = selectedDialogue.situation,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Скорость озвучивания",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f).forEach { rate ->

                    FilterChip(
                        selected = speechRate == rate,
                        onClick = {
                            speechRate = rate

                            preferences.edit()
                                .putFloat("speech_rate", rate)
                                .apply()

                            ttsEngine?.stop()
                        },
                        label = {
                            Text(
                                text = "${rate}x",
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    val fullDialogue = selectedDialogue.lines
                        .joinToString(separator = " ") {
                            it.english
                        }

                    speak(fullDialogue)
                },
                enabled = isTtsReady,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("▶ Прослушать весь диалог")
            }

            OutlinedButton(
                onClick = {
                    ttsEngine?.stop()
                },
                enabled = ttsEngine != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("■ Остановить")
            }

            if (!isTtsReady) {

                Text(
                    text = "Озвучка загружается или английский голос недоступен. Проверь настройки синтеза речи Android.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Реплики диалога",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            selectedDialogue.lines.forEach { line ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(17.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = line.speaker,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(7.dp))

                        Text(
                            text = line.english,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = line.russian,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = {
                                speak(line.english)
                            },
                            enabled = isTtsReady
                        ) {
                            Text("▶ Прослушать реплику")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
