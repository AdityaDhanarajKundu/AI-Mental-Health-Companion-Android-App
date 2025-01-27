package com.example.mentalhealthcompanion.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mentalhealthcompanion.MainActivity
import com.example.mentalhealthcompanion.db.DailyCheckIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JournalViewModel : ViewModel() {
    private val dao = MainActivity.database.dailyCheckInDao()
    @SuppressLint("SimpleDateFormat")
    fun saveCheckIn(feeling : String){
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val checkIn = DailyCheckIn(date = date, feeling = feeling)

        viewModelScope.launch {
            try {
                dao.insertCheckIn(checkIn)
            } catch (e: Exception) {
                Log.e("JournalViewModel", "Error inserting check-in", e)
            }
        }
    }

    fun getAllCheckIns() : List<DailyCheckIn>{
        return runBlocking {
            dao.getAllCheckIns()
        }
    }
}