package com.YanandWang.ourandroidproject.ui.sleep
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SleepRecord::class], version = 1)
abstract class AppSleepDatabase : RoomDatabase() {
    companion object {
        const val NAME = "sleep_db"
    }
    abstract fun sleepDao(): SleepRecordDao
}