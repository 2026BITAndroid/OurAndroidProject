package com.YanandWang.ourandroidproject.ui.sleep

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sleep_record")
// 存储key
private val SLEEP_LIST_KEY = stringPreferencesKey("sleep_list")

private val LAST_VALID_SCREEN_OFF_KEY = stringPreferencesKey("last_valid_screen_off_time")

private val LAST_RECORDED_DATE_KEY = stringPreferencesKey("last_recorded_sleep_date")

/** 单日睡眠记录实体 */
data class SleepRecord(
    val sleepDate: String,
    val sleepTime: String,
    val wakeTime: String,
    val sleepHour: Double,
    val isOvernight: Boolean
)

/** 睡眠数据管理工具 */
class SleepManager(context: Context) {
    private val ctx = context
    private val gson = Gson()
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    private val dayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val zone: ZoneId = ZoneId.systemDefault()


    private val SLEEP_OFF_START_HOUR = 21
    private val SLEEP_OFF_END_HOUR = 5
    private val WAKE_UP_START_HOUR = 5
    private val WAKE_UP_END_HOUR = 14


    suspend fun getAllSleepRecords(): MutableList<SleepRecord> {
        return try {
            val prefs = ctx.dataStore.data.first()
            val json = prefs[SLEEP_LIST_KEY] ?: "[]"
            val type = object : TypeToken<MutableList<SleepRecord>>() {}.type
            gson.fromJson(json, type) ?: mutableListOf()
        } catch (e: Exception) {
            e.printStackTrace()
            mutableListOf()
        }
    }

    suspend fun saveList(list: MutableList<SleepRecord>) {
        ctx.dataStore.edit { it[SLEEP_LIST_KEY] = gson.toJson(list) }
    }

    /**
     * 息屏触发：只有在【有效息屏窗口】内的息屏才会被记录
     * 自动保留窗口内的最后一次息屏时间
     */
    suspend fun recordSleepOff() {
        val now = LocalDateTime.now()
        val hour = now.hour

        if (hour >= SLEEP_OFF_START_HOUR || hour < SLEEP_OFF_END_HOUR) {
            ctx.dataStore.edit {
                it[LAST_VALID_SCREEN_OFF_KEY] = now.format(dateFormatter)
            }
        }
    }


    suspend fun recordScreenOn() {
        val now = LocalDateTime.now()
        val hour = now.hour
        val todayStr = now.format(dayFormatter)

        if (hour < WAKE_UP_START_HOUR || hour >= WAKE_UP_END_HOUR) {
            return
        }

        val prefs = ctx.dataStore.data.first()
        val lastRecordedDate = prefs[LAST_RECORDED_DATE_KEY] ?: ""
        if (lastRecordedDate == todayStr) {
            return
        }

        val lastOffStr = prefs[LAST_VALID_SCREEN_OFF_KEY] ?: return
        val lastOffTime = LocalDateTime.parse(lastOffStr, dateFormatter)

        val sleepDate = now.minusDays(1).format(dayFormatter)

        val nowSec = now.atZone(zone).toEpochSecond()
        val offSec = lastOffTime.atZone(zone).toEpochSecond()
        val hourDiff = (nowSec - offSec) / 3600.0

        if (hourDiff < 0.5) {
            return
        }

        val newRecord = SleepRecord(
            sleepDate = sleepDate,
            sleepTime = lastOffStr,
            wakeTime = now.format(dateFormatter),
            sleepHour = hourDiff,
            isOvernight = lastOffTime.hour in 0 until 5 // 00:00-05:00入睡标记为熬夜
        )

        val list = getAllSleepRecords()
        if (list.none { it.sleepDate == sleepDate }) {
            list.add(newRecord)
            saveList(list)
            ctx.dataStore.edit {
                it[LAST_RECORDED_DATE_KEY] = todayStr
            }
        }
    }

    suspend fun getAvgWeekSleep(): Double {
        val validList = getAllSleepRecords().filter { it.sleepHour > 0 }.takeLast(7)
        return if(validList.isEmpty()) 0.0 else validList.sumOf { it.sleepHour } / validList.size
    }

    suspend fun getOvernightCountWeek(): Int {
        return getAllSleepRecords().filter { it.isOvernight }.takeLast(7).count()
    }
}