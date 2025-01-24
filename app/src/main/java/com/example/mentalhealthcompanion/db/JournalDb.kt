package com.example.mentalhealthcompanion.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DailyCheckIn::class], version = 1)
abstract class JournalDb : RoomDatabase(){
    abstract fun dailyCheckInDao(): DailyCheckInDao
}