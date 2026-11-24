package com.example.amerly.ui.components

import android.content.Context
import android.widget.Toast
import com.example.amerly.data.model.AchievementCatalog

fun showAchievementNotifications(
    context: Context,
    achievementIds: List<String>
) {

    val messages = achievementIds.mapNotNull { id ->

        AchievementCatalog.all
            .firstOrNull { it.id == id }
            ?.let { "${it.emoji} ${it.title}" }
    }

    if (messages.isEmpty()) return

    val message = "🏆 Достижение открыто!\n" +
            messages.joinToString("\n")

    Toast.makeText(
        context,
        message,
        Toast.LENGTH_LONG
    ).show()
}
