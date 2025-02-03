package com.example.mentalhealthcompanion.viewmodel

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mentalhealthcompanion.db.DailyCheckInDao
import com.example.mentalhealthcompanion.db.MoodData
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.LineChart
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MoodViewModel(private val dao : DailyCheckInDao) : ViewModel(){
    private val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1001
    private val _moodTrends = MutableLiveData<List<MoodData>>()
    val moodTrends : LiveData<List<MoodData>> get() = _moodTrends
    private val _averageMood = MutableLiveData<Float>()
    val averageMood : LiveData<Float> get() = _averageMood
    private val _suggestPsychiatrist = MutableLiveData<Boolean>()
    val suggestPsychiatrist: LiveData<Boolean> get() = _suggestPsychiatrist

    val lineChartRef = mutableStateOf<LineChart?>(null)
    val barChartRef = mutableStateOf<BarChart?>(null)

    fun updateChartReferences(lineChart: LineChart?, barChart: BarChart?){
        lineChartRef.value = lineChart
        barChartRef.value = barChart
    }

    // get all the daily checkin values convert them into mood data and store them in mood values after processing
    fun getMoodTrends(){
        viewModelScope.launch {
            try{
                val entries = dao.getAllCheckIns().map {
                    MoodData(
                        date = it.date,
                        moodLabel = it.sentiment,
                        moodScore = mapMoodToScore(it.sentiment)
                    )
                }
                _moodTrends.value = entries
                _averageMood.value = calculateAverageMood(entries)
                _suggestPsychiatrist.value = shouldSuggestPsychiatrist(entries)
            }catch (e : Exception){
                Log.e("MoodViewModel", "Error getting mood trends", e)
            }
        }
    }

    private fun mapMoodToScore(mood: String) : Float{
        return when (mood.lowercase()){
            "joy", "love", "gratitude", "excitement", "pride", "optimism" -> 4.5f
            "neutral", "approval", "curiosity", "realization", "amusement" -> 3.0f
            "mild sadness", "slightly down" -> 2.0f
            "sadness", "grief", "disappointment", "remorse", "nervousness" -> 1.5f
            "anger", "fear", "disgust", "annoyance", "disapproval" -> 1.0f
            else -> 3.0f  // Default neutral score
        }
    }

    fun calculateAverageMood(entries: List<MoodData>): Float {
        if (entries.isEmpty()) return 0f
        return entries.map { it.moodScore }.average().toFloat()
    }

    fun shouldSuggestPsychiatrist(entries: List<MoodData>) : Boolean{
        if (entries.isEmpty()) return false
        val negativeMoods = entries.count { it.moodScore <= 1.5f }
        val totalEntries = entries.size
        return negativeMoods.toFloat() / totalEntries.toFloat() >= 0.5       // if negative moods are more than or equal to half of the total entries size
    }

    fun generatePdfReport(context: Context, activity: Activity): String {
        return generatePdfReport(context, activity, lineChartRef.value, barChartRef.value)
    }

    fun generatePdfReport(context: Context, activity: Activity, moodLineChart: LineChart?, moodBarChart: BarChart?) : String{
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(600,900,1).create()
        val page = pdfDocument.startPage(pageInfo)

        val canvas = page.canvas
        val paint = android.graphics.Paint().apply {
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
        }

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        canvas.drawText("Mood Analysis Report - $currentDate", 20f, 40f, paint)

        paint.textSize = 14f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Summary:", 20f, 70f, paint)


        val trends = _moodTrends.value ?: emptyList()
        var yPosition = 150f
        val avgMood = _averageMood.value ?: 0.0
        canvas.drawText("Average Mood Score: ${String.format("%.2f", avgMood)}", 20f, 100f, paint)
        canvas.drawText("Total Entries: ${trends.size}", 20f, 120f, paint)


        for (entry in trends.take(5)) { // Show top 5 recent moods
            canvas.drawText("${entry.date}: ${entry.moodLabel} (${entry.moodScore})", 20f, yPosition, paint)
            yPosition += 25
        }

        // Insert Mood Line Chart
        val lineChartBitmap = moodLineChart?.let { getChartBitmap(it) }
        if (lineChartBitmap != null) {
            val scaledLineChart = Bitmap.createScaledBitmap(lineChartBitmap, 500, 250, true)
            canvas.drawBitmap(scaledLineChart, 50f, yPosition + 30, paint)
            yPosition += 280
        }

        // Insert Mood Bar Chart
        val barChartBitmap = moodBarChart?.let { getChartBitmap(it) }
        if (barChartBitmap != null) {
            val scaledBarChart = Bitmap.createScaledBitmap(barChartBitmap, 500, 250, true)
            canvas.drawBitmap(scaledBarChart, 50f, yPosition + 30, paint)
        }

        pdfDocument.finishPage(page)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            savePdfUsingMediaStore(context, pdfDocument, currentDate)
        }else{
            // Check and request storage permission for Android 9 and below
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_WRITE_EXTERNAL_STORAGE)
                return ""
            } else {
                return savePdfUsingFileOutputStream(context, currentDate, pdfDocument)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun savePdfUsingMediaStore(context : Context, pdfDocument: PdfDocument, currentDate: String) : String{
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "MoodAnalysisReport_$currentDate.pdf")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
        }
        val uri = context.contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
        return uri?.let {
            try {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                    Toast.makeText(context, "PDF saved to Documents", Toast.LENGTH_LONG).show()
                }
                pdfDocument.close()
                uri.toString()
            } catch (e: IOException) {
                Log.e("generatePdfReport", "Error writing PDF", e)
                Toast.makeText(context, "Failed to save PDF", Toast.LENGTH_LONG).show()
                ""
            }
        } ?: run {
            Log.e("generatePdfReport", "Failed to insert file into MediaStore.")
            Toast.makeText(context, "Failed to create file", Toast.LENGTH_LONG).show()
            ""
        }
    }

    fun getChartBitmap(chart: Chart<*>): Bitmap? {
        return try {
            chart.isDrawingCacheEnabled = true
            chart.buildDrawingCache()
            val bitmap = Bitmap.createBitmap(chart.drawingCache)
            chart.isDrawingCacheEnabled = false
            bitmap
        } catch (e: Exception) {
            Log.e("PDF Report", "Error capturing chart bitmap", e)
            null
        }
    }

    private fun savePdfUsingFileOutputStream(context: Context,currentDate: String, pdfDocument: PdfDocument): String {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            "MoodAnalysisReport_$currentDate.pdf"
        )

        return try {
            FileOutputStream(file).use { pdfDocument.writeTo(it) }
            pdfDocument.close()
            Toast.makeText(context, "PDF saved: ${file.absolutePath}", Toast.LENGTH_LONG).show()
            file.absolutePath
        } catch (e: IOException) {
            Log.e("generatePdfReport", "Error writing PDF", e)
            Toast.makeText(context, "Failed to save PDF", Toast.LENGTH_LONG).show()
            ""
        }
    }
}