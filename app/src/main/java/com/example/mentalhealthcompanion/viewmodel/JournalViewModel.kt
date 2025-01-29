package com.example.mentalhealthcompanion.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mentalhealthcompanion.MainActivity
import com.example.mentalhealthcompanion.db.DailyCheckIn
import com.example.mentalhealthcompanion.db.DailyCheckInDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JournalViewModel(private val dao: DailyCheckInDao) : ViewModel() {
    @SuppressLint("SimpleDateFormat")
    fun saveCheckIn(feeling : String, sentiment : String){
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val checkIn = DailyCheckIn(date = date, feeling = feeling, sentiment = sentiment)

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

    fun getRecommendation(sentiment: String) : String{
        val recommendations = mapOf(
            "admiration" to listOf(
                "Celebrate your inspiration by sharing it with someone.",
                "Start a project that reflects your admiration.",
                "Write about why you admire something or someone."
            ),
            "amusement" to listOf(
                "Enjoy the moment and laugh wholeheartedly.",
                "Share a funny video or joke with a friend.",
                "Watch something entertaining to keep the amusement going."
            ),
            "anger" to listOf(
                "Take deep breaths to calm yourself down.",
                "Channel your energy into exercise or physical activity.",
                "Write down your thoughts to process the anger constructively."
            ),
            "annoyance" to listOf(
                "Step away from the situation causing annoyance.",
                "Focus on something that brings you joy to distract yourself.",
                "Practice mindfulness to calm your thoughts."
            ),
            "approval" to listOf(
                "Acknowledge your achievements and feel proud.",
                "Share your approval with others to spread positivity.",
                "Reflect on what made you feel this way and try to replicate it."
            ),
            "caring" to listOf(
                "Show your care through a kind gesture or message.",
                "Spend quality time with someone you care about.",
                "Volunteer or help someone in need to express your compassion."
            ),
            "confusion" to listOf(
                "Take a moment to organize your thoughts.",
                "Ask for help or clarification if you feel stuck.",
                "Break down the situation into smaller, manageable pieces."
            ),
            "curiosity" to listOf(
                "Dive into research or learning to satisfy your curiosity.",
                "Ask questions and explore new ideas.",
                "Engage in a creative activity that challenges your mind."
            ),
            "desire" to listOf(
                "Set goals to work toward fulfilling your desire.",
                "Visualize achieving what you want to stay motivated.",
                "Take small steps toward making your desire a reality."
            ),
            "disappointment" to listOf(
                "Allow yourself to feel disappointed, then focus on moving forward.",
                "Reflect on the lessons you can learn from the situation.",
                "Talk to a friend or mentor about how you feel."
            ),
            "disapproval" to listOf(
                "Reflect on why you feel this way and communicate respectfully.",
                "Seek to understand different perspectives.",
                "Focus on constructive ways to address the situation."
            ),
            "disgust" to listOf(
                "Take deep breaths to calm your reaction.",
                "Remove yourself from the source of disgust if possible.",
                "Focus on something pleasant to shift your mindset."
            ),
            "embarrassment" to listOf(
                "Remind yourself that everyone makes mistakes.",
                "Laugh it off and focus on moving forward.",
                "Talk to a trusted friend about the situation for perspective."
            ),
            "excitement" to listOf(
                "Channel your energy into a creative activity.",
                "Share your excitement with friends or family.",
                "Plan something fun to make the most of your enthusiasm."
            ),
            "fear" to listOf(
                "Face your fears one step at a time.",
                "Practice deep breathing or meditation to calm yourself.",
                "Seek support or talk to someone you trust about your fears."
            ),
            "gratitude" to listOf(
                "Write a list of things you’re grateful for.",
                "Express your gratitude to someone who has helped you.",
                "Take a moment to reflect on the positives in your life."
            ),
            "grief" to listOf(
                "Allow yourself to feel and process your grief.",
                "Reach out to friends or family for support.",
                "Engage in activities that bring you comfort and healing."
            ),
            "joy" to listOf(
                "Celebrate your happiness by doing something you love.",
                "Share your joy with someone close to you.",
                "Capture the moment by writing it down or taking a picture."
            ),
            "love" to listOf(
                "Express your love through words or actions.",
                "Spend quality time with someone you love.",
                "Reflect on what makes your relationships meaningful."
            ),
            "nervousness" to listOf(
                "Take slow, deep breaths to calm your nerves.",
                "Prepare yourself thoroughly for what’s causing the nervousness.",
                "Visualize a positive outcome to build confidence."
            ),
            "optimism" to listOf(
                "Use your optimism to encourage others around you.",
                "Focus on setting and achieving meaningful goals.",
                "Embrace challenges as opportunities for growth."
            ),
            "pride" to listOf(
                "Celebrate your accomplishments with those who supported you.",
                "Reflect on the effort it took to achieve your goals.",
                "Use your pride as motivation for future endeavors."
            ),
            "realization" to listOf(
                "Write down your thoughts to process the realization fully.",
                "Discuss your new understanding with a trusted friend.",
                "Take action based on what you’ve learned."
            ),
            "relief" to listOf(
                "Enjoy a moment of relaxation to savor the relief.",
                "Reflect on what caused the stress and how you overcame it.",
                "Share your relief with someone close to you."
            ),
            "remorse" to listOf(
                "Acknowledge your feelings and think about how to make amends.",
                "Apologize sincerely if your actions affected someone else.",
                "Reflect on the situation to prevent similar occurrences."
            ),
            "sadness" to listOf(
                "Comfort yourself with your favorite music or a warm drink.",
                "Talk to a friend or family member about how you feel.",
                "Spend some time journaling to release your emotions."
            ),
            "surprise" to listOf(
                "Share the surprising news with someone close to you.",
                "Take time to process the surprise before reacting.",
                "Embrace the unexpected and enjoy the moment."
            ),
            "neutral" to listOf(
                "Take this time to reflect on your day.",
                "Plan something exciting to bring more joy into your life.",
                "Focus on self-care and mindfulness."
            )
        )
        val emotionRecommendations = recommendations[sentiment.lowercase()]
        return emotionRecommendations?.random()
            ?: "Take some time to reflect on your emotions and focus on self-care."
    }

    fun addCheckIn(feeling: String){
        viewModelScope.launch {
            try {
                val sentiment = analyzeSentiment(feeling)
                saveCheckIn(feeling = feeling,sentiment)
            }catch (e : Exception){
                Log.e("JournalViewModel", "Error Adding Daily Check In", e)
            }
        }
    }

    suspend fun analyzeSentiment(feeling: String) : String{
        val client = OkHttpClient()
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val jsonBody = JSONObject()
        jsonBody.put("text", feeling)
        val requestBody = jsonBody.toString().toRequestBody(mediaType)

        return withContext(Dispatchers.IO){
            try {
                val request = Request.Builder()
                    .url("http://10.0.2.2:5000/analyze")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful){
                    val responseBody = response.body?.string()
                    val jsonResponse = JSONObject(responseBody ?: "")
                    val predictions = jsonResponse.getJSONArray("predictions")
                    val topEmotion = predictions.getJSONObject(0).getString("Emotion")
                    topEmotion
                }else{
                    "Neutral"
                }
            }catch (e : Exception){
                Log.e("JournalViewModel", "Sentiment Analysis Failed", e)
                "Neutral"
            }
        }
    }

    fun deleteCheckIn(entry : DailyCheckIn){
        viewModelScope.launch {
            try {
                dao.deleteCheckIn(entry)
            }catch (e: Exception){
                Log.e("JournalViewModel", "Error deleting check-in", e)
            }
        }
    }
}