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

    @Query("SELECT AVG(feeling) FROM daily_check_ins WHERE date BETWEEN :start AND :end")
    suspend fun getAverageMood(start : String, end : String) : Float

    @Query("SELECT * FROM daily_check_ins ORDER BY date DESC")
    suspend fun getMoodTrends() : List<DailyCheckIn>
}