package com.example.amerly.data.local

import android.content.Context

class AchievementStorage(context: Context) {

    private val preferences = context.applicationContext
        .getSharedPreferences(
            "amerly_achievements",
            Context.MODE_PRIVATE
        )

    fun unlock(id: String): Boolean {

        if (preferences.contains("unlocked_$id")) {
            return false
        }

        preferences.edit()
            .putLong(
                "unlocked_$id",
                System.currentTimeMillis()
            )
            .apply()

        return true
    }

    fun isUnlocked(id: String): Boolean {
        return preferences.contains("unlocked_$id")
    }

    fun getUnlockedIds(): Set<String> {

        return preferences.all.keys
            .filter { it.startsWith("unlocked_") }
            .map { it.removePrefix("unlocked_") }
            .toSet()
    }

    fun getUnlockedCount(): Int {
        return getUnlockedIds().size
    }
}
