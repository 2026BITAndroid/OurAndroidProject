package com.YanandWang.ourandroidproject.ui.sleep
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepRecordDao {
    @Query("SELECT * FROM sleep_record ORDER BY sleepTime DESC")
    fun getAllRecord(): Flow<List<SleepRecord>>

    @Insert
    suspend fun insert(record: SleepRecord)
}