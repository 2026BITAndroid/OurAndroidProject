package com.YanandWang.ourandroidproject.ui.sleep
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "sleep_record")
data class SleepRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sleepTime: LocalDateTime,
    val wakeTime: LocalDateTime?,
    val sleepHour: Double = 0.0,
    val suggest: String = ""
)