package com.example.mentalhealthcompanion.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DailyCheckIn::class], version = 2, exportSchema = false)
abstract class JournalDb : RoomDatabase(){
    abstract fun dailyCheckInDao(): DailyCheckInDao
}