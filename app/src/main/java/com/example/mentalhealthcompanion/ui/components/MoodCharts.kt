package com.example.mentalhealthcompanion.ui.components

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mentalhealthcompanion.db.MoodData
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun MoodLineChart(moodData: List<MoodData>){
    AndroidView(
        factory = {context ->
            LineChart(context).apply {
                description.isEnabled = false
                val entries = mutableListOf<Entry>()
                val xValues = moodData.indices.map { it.toFloat() }
                val yValues = moodData.map { it.moodScore }
                xValues.forEachIndexed { index, value ->
                    entries.add(Entry(value, yValues[index]))
                }
                val dataSet = LineDataSet(entries, "Mood Trend").apply {
                    color = Color.BLUE // Line color
                    valueTextColor = Color.BLACK // Color of the text value for each point
                    lineWidth = 2f // Width of the line
                    setDrawCircles(true) // Enable circles for data points
                    circleColors = listOf(Color.RED)
                    setDrawValues(true) // Display values on each data point
                    setDrawFilled(true) // Fill the area under the line
                    valueTextSize = 10f // Size of the value text
                    fillColor = Color.parseColor("#80D3E3FF")
                }
                data = LineData(dataSet)
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    valueFormatter = IndexAxisValueFormatter(xValues.map { it.toString() })
                    granularity = 1f
                    setDrawGridLines(false)
                    axisLineColor = Color.BLACK
                    textColor = Color.BLACK
                }
                axisLeft.apply {
                    setDrawGridLines(true) // Enable grid lines for Y-axis
                    gridColor = Color.GRAY // Color for the Y-axis grid lines
                    axisLineColor = Color.BLACK // Color for Y-axis line
                    textColor = Color.BLACK
                }
                axisRight.isEnabled = false
                setBackgroundColor(Color.WHITE)
                isDragEnabled = true
                setScaleEnabled(true)
                setTouchEnabled(true)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ){lineChart ->
        val xValues = moodData.indices.map { it.toFloat() }
        val yValues = moodData.map { it.moodScore }
        val entries = xValues.mapIndexed { index, value ->
            Entry(value, yValues[index])
        }
        val dataset = LineDataSet(entries, "Mood Trend").apply {
            color = Color.BLUE // Line color
            valueTextColor = Color.BLACK // Color of the text value for each point
            lineWidth = 2f // Width of the line
            setDrawCircles(true) // Enable circles for data points
            circleColors = listOf(Color.RED) // Circle color for each data point
            setDrawValues(true) // Display values on each data point
            valueTextSize = 10f // Size of the value text
            setDrawFilled(true) // Fill the area under the line
            fillColor = Color.parseColor("#80D3E3FF")
        }
        lineChart.data = LineData(dataset)
        lineChart.invalidate()
    }
}

@Composable
fun MoodBarChart(moodData: List<MoodData>){
    AndroidView(
        factory = {context ->
            BarChart(context).apply {
                description.isEnabled = false
                val entries = mutableListOf<BarEntry>()
                val moodGroups = moodData.groupBy { it.moodLabel }
                val xValues = moodGroups.keys.toList()
                val yValues = moodGroups.values.map { it.size.toFloat() }

                xValues.forEachIndexed { index, label ->
                    entries.add(BarEntry(index.toFloat(), yValues[index]))
                }
                val dataSet = BarDataSet(entries, "Mood Distribution").apply {
                    // Customize the appearance of the bars
                    color = Color.parseColor("#FF6200EE") // Bar color
                    valueTextColor = Color.BLACK // Color of the text value for each bar
                    valueTextSize = 10f // Size of the value text
                    setDrawValues(true) // Display values on top of each bar
                    setDrawBarShadow(false) // Disable the shadow behind the bars
                }
                data = BarData(dataSet)
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM // Position at the bottom of the chart
                    valueFormatter = IndexAxisValueFormatter(xValues) // Set labels for the X-axis
                    granularity = 1f // Ensure each bar has its label
                    setDrawGridLines(false) // Disable grid lines for X-axis
                    axisLineColor = Color.BLACK // Color for the axis line
                    textColor = Color.BLACK // Color for X-axis labels
                }
                axisLeft.apply {
                    setDrawGridLines(true) // Enable grid lines for Y-axis
                    gridColor = Color.GRAY // Color for the Y-axis grid lines
                    axisLineColor = Color.BLACK // Color for Y-axis line
                    textColor = Color.BLACK // Color for Y-axis labels
                }
                axisRight.isEnabled = false
                setBackgroundColor(Color.WHITE)
                isDragEnabled = true
                setScaleEnabled(true)
                setTouchEnabled(true)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ){barChart ->
        val moodGroups = moodData.groupBy { it.moodLabel }
        val xValues = moodGroups.keys.toList()
        val yValues = moodGroups.values.map { it.size.toFloat() }
        val entries = xValues.mapIndexed { index, label ->
            BarEntry(index.toFloat(), yValues[index])
        }
        val dataSet = BarDataSet(entries, "Mood Distribution").apply {
            // Customize the appearance of the bars
            color = Color.parseColor("#FF6200EE") // Bar color
            valueTextColor = Color.BLACK // Color of the text value for each bar
            valueTextSize = 10f // Size of the value text
            setDrawValues(true) // Display values on top of each bar
        }
        barChart.data = BarData(dataSet)
        barChart.invalidate() // refresh the chart
    }
}