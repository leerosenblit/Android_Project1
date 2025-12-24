package com.example.ex1

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ScoreManager(val context: Context) {
    private val SP_FILE = "MY_SP"
    private val KEY_SCORES = "KEY_SCORES"

    fun saveScore(newScore: Score) {
        val currentScores = getAllScores().toMutableList()

        currentScores.add(newScore)

        currentScores.sortByDescending { it.score }
        if (currentScores.size > 10) {
            currentScores.removeAt(currentScores.lastIndex)
        }

        val gson = Gson()
        val jsonString = gson.toJson(currentScores)

        val sharedPreferences = context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_SCORES, jsonString).apply()
    }

    fun getAllScores(): List<Score> {
        val sharedPreferences = context.getSharedPreferences(SP_FILE, Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString(KEY_SCORES, null) ?: return emptyList()

        val gson = Gson()
        val type = object : TypeToken<List<Score>>() {}.type
        return gson.fromJson(jsonString, type)
    }
}