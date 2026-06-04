package com.YanandWang.ourandroidproject.ui.sleep

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

//改用AndroidViewModel，自带Application上下文，弃用私有构造+手动单例
class SleepViewModel(application: android.app.Application) : AndroidViewModel(application) {

    //子线程初始化数据库，禁止主线程build
    private var db: AppSleepDatabase? = null
    private var dao: SleepRecordDao? = null
    private val sp = SleepSpHelper(application)

    private val _records = MutableStateFlow<List<SleepRecord>>(emptyList())
    val records: StateFlow<List<SleepRecord>> = _records.asStateFlow()

    private val splitTime = LocalTime.of(4, 0)
    private var lastOff = sp.getLastScreenOff()
    private var firstOn = sp.getFirstScreenOn()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            db = Room.databaseBuilder(
                getApplication(),
                AppSleepDatabase::class.java,
                AppSleepDatabase.NAME
            ).fallbackToDestructiveMigration()
                .build()
            dao = db?.sleepDao()

            //从全局缓存拿开关机时间，生成记录
            val off = SleepTimeCache.lastOff
            val on = SleepTimeCache.firstOn
            if(off != null && on != null){
                generateRecordInner(off,on)
                SleepTimeCache.lastOff = null
                SleepTimeCache.firstOn = null
                sp.clearCache()
            }

            dao?.getAllRecord()
                ?.catch { }
                ?.collect { _records.value = it }
        }
    }

    private fun generateRecordInner(sleep:LocalDateTime,wake:LocalDateTime){
        val hours = Duration.between(sleep, wake).toMinutes() / 60.0
        val suggest = getSuggest(sleep, hours)
        val record = SleepRecord(sleepTime = sleep,wakeTime = wake,sleepHour = hours,suggest = suggest)
        viewModelScope.launch(Dispatchers.IO) {
            try { dao?.insert(record) }catch (_:Exception){}
        }
    }

    fun recordScreenOff(time: LocalDateTime) {
        if (time.toLocalTime() < splitTime) {
            lastOff = time
            sp.saveLastScreenOff(time)
        }
    }

    fun recordScreenOn(time: LocalDateTime) {
        if (firstOn != null) return
        if (time.toLocalTime() >= splitTime) {
            firstOn = time
            sp.saveFirstScreenOn(time)
            generateRecord()
        }
    }

    private fun generateRecord() {
        val sleep = lastOff ?: return
        val wake = firstOn ?: return
        val daoLocal = dao ?: return

        val hours = Duration.between(sleep, wake).toMinutes() / 60.0
        val suggest = getSuggest(sleep, hours)

        val record = SleepRecord(
            sleepTime = sleep,
            wakeTime = wake,
            sleepHour = hours,
            suggest = suggest
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                daoLocal.insert(record)
            } catch (_: Exception) {}
        }

        lastOff = null
        firstOn = null
        sp.clearCache()
    }

    private fun getSuggest(sleep: LocalDateTime, hour: Double): String {
        val h = sleep.hour
        return when {
            h >= 23 -> when {
                hour < 5 -> "熬夜严重，睡眠不足5小时"
                hour in 5.0..7.0 -> "熬夜入睡，时长中等"
                else -> "晚睡但睡眠时间充足"
            }
            h == 22 -> when {
                hour in 7.0..9.0 -> "作息完美！"
                hour < 6 -> "入睡合适但睡得偏短"
                else -> "睡眠时间偏长"
            }
            else -> when {
                hour in 7.0..9.0 -> "早睡健康作息"
                hour < 6 -> "入睡早但睡眠偏少"
                else -> "入睡过早、睡眠时间过长"
            }
        }
    }
}