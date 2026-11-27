package com.example.amerly
import com.example.amerly.ui.screens.phrases.PhraseScreen
import com.example.amerly.ui.screens.dictionary.DictionaryScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.amerly.ui.theme.AmerlyTheme
import com.example.amerly.ui.screens.vocabulary.VocabularyScreen
import com.example.amerly.ui.screens.grammar.GrammarScreen
import com.example.amerly.ui.screens.speaking.SpeakingScreen
import com.example.amerly.ui.screens.achievements.AchievementScreen
import com.example.amerly.data.local.AchievementStorage
import com.example.amerly.ui.screens.vocabulary.VocabularyScreen
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import kotlinx.coroutines.delay
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val achievementStorage = AchievementStorage(this)

        val firstLaunchUnlocked = achievementStorage.unlock("first_launch")

        if (firstLaunchUnlocked) {
            Toast.makeText(
                this,
                "🏆 Достижение открыто: А это точно не чёрный",
                Toast.LENGTH_LONG
            ).show()
        }

        val preferences = getSharedPreferences(
            "amerly_settings",
            MODE_PRIVATE
        )

        setContent {

            var darkTheme by rememberSaveable {
                mutableStateOf(
                    preferences.getBoolean("dark_theme", false)
                )
            }
            var showSplash by remember {
                mutableStateOf(true)
            }

            LaunchedEffect(Unit) {
                delay(1800)
                showSplash = false
            }

            AmerlyTheme(darkTheme = darkTheme) {
                Crossfade(
                    targetState = showSplash,
                    animationSpec = tween(450)
                ) { splashVisible ->

                    if (splashVisible) {
                        AmerlySplashScreen()
                    } else {
                        AmerlyApp(
                            darkTheme = darkTheme,
                            onThemeChange = { enabled ->
                                darkTheme = enabled

                                preferences.edit()
                                    .putBoolean("dark_theme", enabled)
                                    .apply()
                            }
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun AmerlySplashScreen() {

    val progress = remember {
        Animatable(0f)
    }

    var logoVisible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        logoVisible = true

        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1500,
                easing = LinearEasing
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AnimatedVisibility(
                visible = logoVisible,
                enter = fadeIn(
                    animationSpec = tween(650)
                ) + scaleIn(
                    initialScale = 0.94f,
                    animationSpec = tween(650)
                )
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "AMERLY",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = ".",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD64B55)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            Box(
                modifier = Modifier
                    .width(86.dp)
                    .height(3.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress.value)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(20.dp)
                        )
                )
            }
        }
    }
}@Composable
fun AmerlyApp(
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {

    var currentScreen by rememberSaveable {
        mutableStateOf("home")
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = currentScreen == "home",
                    onClick = { currentScreen = "home" },
                    icon = { Text("⌂", fontSize = 22.sp) },
                    label = { Text("Главная") }
                )

                NavigationBarItem(
                    selected = currentScreen == "vocabulary",
                    onClick = { currentScreen = "vocabulary" },
                    icon = { Text("📚") },
                    label = { Text("Слова") }
                )

                NavigationBarItem(
                    selected = currentScreen == "grammar",
                    onClick = { currentScreen = "grammar" },
                    icon = { Text("📝") },
                    label = { Text("Грамматика") }
                )

                NavigationBarItem(
                    selected = currentScreen == "speaking",
                    onClick = { currentScreen = "speaking" },
                    icon = { Text("💬") },
                    label = { Text("Практика") }
                )
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            when (currentScreen) {

                "home" -> HomeScreen(
                    onOpenScreen = { currentScreen = it },
                    darkTheme = darkTheme,
                    onThemeChange = onThemeChange
                )
                "achievements" -> AchievementScreen()
                "vocabulary" -> VocabularyScreen()
                "grammar" -> GrammarScreen()

                "speaking" -> SpeakingScreen()

                "dictionary" -> DictionaryScreen()

                "phrases" -> PhraseScreen()
            }
        }
    }
}

@Composable
fun HomeScreen(
    onOpenScreen: (String) -> Unit,
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "AMERLY",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 3.sp
            )

            TextButton(
                onClick = {
                    onThemeChange(!darkTheme)
                }
            ) {
                Text(
                    text = if (darkTheme) "☀️" else "🌙",
                    fontSize = 22.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Привет! 👋",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Давай немного улучшим твой английский.",
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(25.dp))

        // Main card

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {

            Column(
                modifier = Modifier.padding(22.dp)
            ) {

                Text(
                    text = "🇺🇸 American English",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Твой путь к свободному английскому начинается здесь.",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 29.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Учи слова, понимай грамматику и практикуй общение.",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Обучение",
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(14.dp))

        SectionCard(
            emoji = "📚",
            title = "Изучение слов",
            description = "Новые слова, перевод и произношение.",
            color = Color(0xFFE8EEFF),
            onClick = { onOpenScreen("vocabulary") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        SectionCard(
            emoji = "📝",
            title = "Грамматика",
            description = "Правила, примеры и упражнения.",
            color = Color(0xFFE9F5EF),
            onClick = { onOpenScreen("grammar") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        SectionCard(
            emoji = "💬",
            title = "Говорение",
            description = "Американские диалоги и аудирование.",
            color = Color(0xFFFFF1E4),
            onClick = { onOpenScreen("speaking") }
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Твои материалы",
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            SmallCard(
                emoji = "📖",
                title = "Мой словарь",
                modifier = Modifier.weight(1f),
                onClick = { onOpenScreen("dictionary") }
            )

            SmallCard(
                emoji = "💡",
                title = "Фразы",
                modifier = Modifier.weight(1f),
                onClick = { onOpenScreen("phrases") }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                onOpenScreen("achievements")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("🏆", fontSize = 20.sp)

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Достижения",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun SectionCard(
    emoji: String,
    title: String,
    description: String,
    color: Color,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Row(
            modifier = Modifier.padding(17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                shape = RoundedCornerShape(15.dp),
                color = color,
                modifier = Modifier.size(55.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = emoji,
                        fontSize = 25.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(15.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 17.sp
                )
            }

            Text(
                text = "›",
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SmallCard(
    emoji: String,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier
            .height(125.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = emoji,
                fontSize = 27.sp
            )

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SectionScreen(
    title: String,
    emoji: String,
    description: String
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(25.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = emoji,
            fontSize = 55.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = title,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = description,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 23.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(15.dp)
        ) {

            Text(
                text = "Скоро появится",
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 12.dp
                ),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
