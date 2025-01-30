package com.example.mentalhealthcompanion.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DailyCheckInDao {
    @Insert
    suspend fun insertCheckIn(checkIn: DailyCheckIn)

    @Query("SELECT * FROM daily_check_ins ORDER BY id DESC")
    suspend fun getAllCheckIns(): List<DailyCheckIn>

    @Delete
    suspend fun deleteCheckIn(checkIn: DailyCheckIn)
}