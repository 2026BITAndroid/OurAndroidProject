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

// DataStore实例（全局唯一）
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sleep_record")
// 存储key
private val SLEEP_LIST_KEY = stringPreferencesKey("sleep_list")
// 存储【有效息屏窗口内】的最后一次息屏时间
private val LAST_VALID_SCREEN_OFF_KEY = stringPreferencesKey("last_valid_screen_off_time")
// 存储最后一次生成睡眠记录的日期（防重复，单日仅1条）
private val LAST_RECORDED_DATE_KEY = stringPreferencesKey("last_recorded_sleep_date")

/** 单日睡眠记录实体 */
data class SleepRecord(
    val sleepDate: String,          // 睡眠日期（永远是前一天的日期）
    val sleepTime: String,          // 入睡时间（前一天21:00-当天05:00最后一次息屏）
    val wakeTime: String,           // 起床时间（当天05:00-14:00第一次开屏）
    val sleepHour: Double,          // 睡眠时长(小时)
    val isOvernight: Boolean        // 是否熬夜（00:00-05:00入睡标记为熬夜）
)

/** 睡眠数据管理工具 */
class SleepManager(context: Context) {
    private val ctx = context
    private val gson = Gson()
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    private val dayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val zone: ZoneId = ZoneId.systemDefault()  // 获取系统默认时区，避免时差

    // ✅ 严格按照你的要求配置时间窗口
    private val SLEEP_OFF_START_HOUR = 21  // 有效息屏开始：前一天21:00
    private val SLEEP_OFF_END_HOUR = 5    // 有效息屏结束：当天05:00
    private val WAKE_UP_START_HOUR = 5    // 有效开屏开始：当天05:00
    private val WAKE_UP_END_HOUR = 14     // 有效开屏结束：当天14:00

    // 获取全部历史睡眠记录（增强容错：解析失败不丢失全部数据）
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

    // 保存睡眠记录列表
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

        // ✅ 核心判断：只有在有效息屏窗口内才处理
        if (hour >= SLEEP_OFF_START_HOUR || hour < SLEEP_OFF_END_HOUR) {
            ctx.dataStore.edit {
                it[LAST_VALID_SCREEN_OFF_KEY] = now.format(dateFormatter)
            }
        }
    }

    /**
     * 亮屏触发：只有在【有效开屏窗口】内的第一次亮屏才会生成记录
     * 其他所有亮屏完全忽略
     */
    suspend fun recordScreenOn() {
        val now = LocalDateTime.now()
        val hour = now.hour
        val todayStr = now.format(dayFormatter)

        // 1. 不在有效开屏窗口内 → 直接忽略
        if (hour < WAKE_UP_START_HOUR || hour >= WAKE_UP_END_HOUR) {
            return
        }

        // 2. 今天已经生成过睡眠记录 → 直接忽略（单日仅1条）
        val prefs = ctx.dataStore.data.first()
        val lastRecordedDate = prefs[LAST_RECORDED_DATE_KEY] ?: ""
        if (lastRecordedDate == todayStr) {
            return
        }

        // 3. 获取最后一次有效息屏时间 → 没有则直接返回
        val lastOffStr = prefs[LAST_VALID_SCREEN_OFF_KEY] ?: return
        val lastOffTime = LocalDateTime.parse(lastOffStr, dateFormatter)

        // 4. 计算这条睡眠记录归属的日期（永远是前一天）
        val sleepDate = now.minusDays(1).format(dayFormatter)

        // 5. 计算睡眠时长（精确到小数点后1位），使用系统默认时区
        val nowSec = now.atZone(zone).toEpochSecond()
        val offSec = lastOffTime.atZone(zone).toEpochSecond()
        val hourDiff = (nowSec - offSec) / 3600.0

        // 6. 过滤过短的睡眠（小于30分钟，排除误触）
        if (hourDiff < 0.5) {
            return
        }

        // 7. 生成睡眠记录
        val newRecord = SleepRecord(
            sleepDate = sleepDate,
            sleepTime = lastOffStr,
            wakeTime = now.format(dateFormatter),
            sleepHour = hourDiff,
            isOvernight = lastOffTime.hour in 0 until 5 // 00:00-05:00入睡标记为熬夜
        )

        // 8. 保存记录（再次防重复）
        val list = getAllSleepRecords()
        if (list.none { it.sleepDate == sleepDate }) {
            list.add(newRecord)
            saveList(list)
            // 标记今天已经生成过记录，后续所有亮屏都忽略
            ctx.dataStore.edit {
                it[LAST_RECORDED_DATE_KEY] = todayStr
            }
        }
    }

    // 获取近7天平均睡眠
    suspend fun getAvgWeekSleep(): Double {
        val validList = getAllSleepRecords().filter { it.sleepHour > 0 }.takeLast(7)
        return if(validList.isEmpty()) 0.0 else validList.sumOf { it.sleepHour } / validList.size
    }

    // 获取近7天熬夜天数
    suspend fun getOvernightCountWeek(): Int {
        return getAllSleepRecords().filter { it.isOvernight }.takeLast(7).count()
    }
}