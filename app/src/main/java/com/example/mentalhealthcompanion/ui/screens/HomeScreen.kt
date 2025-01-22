package com.example.mentalhealthcompanion.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mentalhealthcompanion.MainActivity
import com.example.mentalhealthcompanion.R
import com.example.mentalhealthcompanion.service.Quote
import com.example.mentalhealthcompanion.utils.Constants.tips

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, onSignOut : () -> Unit = {}) {
    val quote = remember { mutableStateOf<Quote?>(null) }
    LaunchedEffect(Unit) {
        try {
            val result = Quote.RetrofitInstance.api.getRandomQuote()
            println(result)
            if (result.isNotEmpty()) {
                quote.value = result[0]
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                title = { Text(text = "E-MotionAI")},
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Made with ❤️ by Aditya Dhanaraj Kundu",
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "Welcome Back to E-MotionAI", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 8.dp))
            Text(text = "Take a moment for yourself today and choose what feels right for you.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 16.dp))
            //Welcome Image
            Image(
                painter = painterResource(id = R.drawable.welcome),
                contentDescription = "Welcome Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp)
            )
            // Navigation Buttons
            Button(
                onClick = { navController.navigate("journal_screen") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Journal Your Thoughts")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("meditation_screen") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guided Meditation")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("mood_analysis_screen") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mood Analysis")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Tips Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ){
                Text(
                    text = "Quick Tips",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(imageVector = Icons.Filled.CheckBox, contentDescription = "tips")
            }
            val randomTips = remember {tips.shuffled().take(3)}
            randomTips.forEach{
                Text(
                    text = "- $it",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 0.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(5.dp))

            // Motivational Quote Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ){
                Text(
                    text = "Quote of the Day",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(imageVector = Icons.Filled.Grade, contentDescription = "quote")
            }
            Text(
                text = "\"${quote.value?.q ?: "Fetching quote..."}\"",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "- ${quote.value?.a ?: ""}",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp, top = 0.dp, start = 260.dp),
                color = MaterialTheme.colorScheme.primary
            )

            // Daily Check-In Section
            var dailyCheckIn by remember {mutableStateOf("")}

            Text(
                text = "Daily Check-In",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = dailyCheckIn,
                onValueChange = { dailyCheckIn = it },
                label = { Text("How are you feeling today?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            Button(
                onClick = {
                    // Handle submission of daily check-in
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Submit")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Out Option
            ClickableText(
                text = AnnotatedString("Sign Out"),
                onClick = {
                    // Handle sign-out logic
                          onSignOut()
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview(){
    HomeScreen(navController = NavController(MainActivity()), onSignOut = {})
}