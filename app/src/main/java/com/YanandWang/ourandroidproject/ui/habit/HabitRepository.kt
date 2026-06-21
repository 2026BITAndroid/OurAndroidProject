package com.YanandWang.ourandroidproject.ui.habit

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fatboyindustrial.gsonjavatime.Converters
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "habits")

class HabitRepository(private val context: Context) {
    private val HABITS_KEY = stringPreferencesKey("habits_list")
    private val gson = Converters.registerAll(GsonBuilder()).create()

    val habitsFlow: Flow<List<Habit>> = context.dataStore.data
        .map { preferences ->
            val json = preferences[HABITS_KEY] ?: "[]"
            val type = object : TypeToken<List<Habit>>() {}.type
            gson.fromJson(json, type)
        }

    suspend fun saveHabits(habits: List<Habit>) {
        context.dataStore.edit { preferences ->
            preferences[HABITS_KEY] = gson.toJson(habits)
        }
    }

    fun checkInHabit(habit: Habit): Habit {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        return when (habit.lastCheckInDate) {
            today -> habit
            yesterday -> habit.copy(
                totalDays = habit.totalDays + 1,
                currentStreak = habit.currentStreak + 1,
                maxStreak = maxOf(habit.maxStreak, habit.currentStreak + 1),
                lastCheckInDate = today,
                checkInDates = habit.checkInDates + today // 同步记录打卡日期
            )
            else -> habit.copy(
                totalDays = habit.totalDays + 1,
                currentStreak = 1,
                maxStreak = maxOf(habit.maxStreak, 1),
                lastCheckInDate = today,
                checkInDates = habit.checkInDates + today // 同步记录打卡日期
            )
        }
    }
}