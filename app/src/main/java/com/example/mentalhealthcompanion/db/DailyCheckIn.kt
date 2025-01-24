package com.example.mentalhealthcompanion.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("daily_check_ins")
data class DailyCheckIn(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val feeling: String
)
