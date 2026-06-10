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
    // 修复：注册所有Java 8日期时间类型的序列化/反序列化适配器
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
            today -> habit // 今天已打卡，不重复
            yesterday -> habit.copy(
                totalDays = habit.totalDays + 1,
                currentStreak = habit.currentStreak + 1,
                maxStreak = maxOf(habit.maxStreak, habit.currentStreak + 1),
                lastCheckInDate = today
            )
            else -> habit.copy(
                totalDays = habit.totalDays + 1,
                currentStreak = 1,
                maxStreak = maxOf(habit.maxStreak, 1),
                lastCheckInDate = today
            )
        }
    }
}