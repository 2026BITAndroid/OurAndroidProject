package com.YanandWang.ourandroidproject.ui.diary

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit // ✅ 修正：导入Preferences DataStore专属的edit函数
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.serialization.Serializable
// 数据模型
@Serializable
data class DiaryEntry(
    val id: String,
    val date: String,
    val emotion: String,
    val content: String,
    val timestamp: Long
)

// 情绪枚举
enum class Emotion(val label: String, val color: Long) {
    HAPPY("开心", 0xFFFFC107),
    CALM("平静", 0xFF2196F3),
    SAD("难过", 0xFF9C27B0),
    ANGRY("生气", 0xFFF44336),
    TIRED("疲惫", 0xFF795548)
}

// DataStore实例
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "diary")
private val DIARY_KEY = stringPreferencesKey("diary_entries")

class DiaryRepository(private val context: Context) {
    // 获取所有日记
    fun getAllDiaries(): Flow<List<DiaryEntry>> {
        return context.dataStore.data
            .map { preferences: Preferences ->
                val json = preferences[DIARY_KEY] ?: "[]"
                Json.decodeFromString<List<DiaryEntry>>(json)
            }
            .map { entries: List<DiaryEntry> ->
                entries.sortedByDescending { entry: DiaryEntry -> entry.timestamp }
            }
    }

    // 添加新日记
    suspend fun addDiary(emotion: String, content: String) {
        val currentDiaries = getAllDiaries().firstOrNull() ?: emptyList()
        val newEntry = DiaryEntry(
            id = System.currentTimeMillis().toString(),
            date = SimpleDateFormat("yyyy年MM月dd日 EEEE", Locale.CHINA).format(Date()),
            emotion = emotion,
            content = content,
            timestamp = System.currentTimeMillis()
        )
        val updatedDiaries = currentDiaries + newEntry
        saveDiaries(updatedDiaries)
    }

    // 删除日记
    suspend fun deleteDiary(id: String) {
        val currentDiaries = getAllDiaries().firstOrNull() ?: emptyList()
        val updatedDiaries = currentDiaries.filter { entry: DiaryEntry -> entry.id != id }
        saveDiaries(updatedDiaries)
    }

    // 保存到DataStore
    private suspend fun saveDiaries(diaries: List<DiaryEntry>) {
        val json = Json.encodeToString(diaries)
        context.dataStore.edit { preferences ->
            preferences[DIARY_KEY] = json
        }
    }
}