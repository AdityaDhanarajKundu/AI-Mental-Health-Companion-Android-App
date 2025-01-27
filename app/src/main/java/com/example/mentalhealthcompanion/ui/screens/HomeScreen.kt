package com.example.mentalhealthcompanion.ui.screens

import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.example.mentalhealthcompanion.viewmodel.AuthViewModel
import com.example.mentalhealthcompanion.viewmodel.JournalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, onSignOut : () -> Unit = {}, authViewModel: AuthViewModel) {
    val quote = remember { mutableStateOf<Quote?>(null) }
    val viewModel = JournalViewModel()
    var feeling by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val userData by authViewModel.userData.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.fetchUserDataFromFirestore { isSuccess ->
            if (!isSuccess) {
                Log.e("AuthViewModel", "Failed to fetch user data from Firestore")
            }
        }
    }
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
    LaunchedEffect(key1 = feeling) {
        if (feeling){
            Toast.makeText(context, "Feeling saved successfully!", Toast.LENGTH_SHORT).show()
            feeling = false
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                title = { Text(text = "E-MotionAI", style = MaterialTheme.typography.titleLarge)},
                actions = {
                    IconButton(onClick = {navController.navigate("profile")}) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Made with ❤️ by Aditya Dhanaraj Kundu",
                    style = MaterialTheme.typography.bodyMedium
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
            Text(text = "Welcome ${userData?.username ?: userData?.email ?: ""}, to E-MotionAI", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 8.dp))
            Text(text = "Take a moment for yourself today and choose what feels right for you.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 16.dp))
            //Welcome Image
            Image(
                painter = painterResource(id = R.drawable.welcome),
                contentDescription = "Welcome Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .padding(bottom = 16.dp)
            )
            // Navigation Buttons
            Button(
                onClick = { navController.navigate("journal") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Daily Check In Journal")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("meditation") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text("Guided Meditation")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("mood") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                )
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
                    color = MaterialTheme.colorScheme.tertiary
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
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = "- ${quote.value?.a ?: ""}",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp, top = 0.dp, start = 260.dp),
                color = MaterialTheme.colorScheme.tertiary
            )

            // Daily Check-In Section
            var dailyCheckIn by remember {mutableStateOf("")}

            Text(
                text = "Daily Check-In",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
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
                    if (dailyCheckIn.isNotEmpty()){
                        viewModel.saveCheckIn(dailyCheckIn)
                        dailyCheckIn = ""
                        feeling = true
                    }
                },
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Submit")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Out Option
            Button(
                onClick = {
                    onSignOut()
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(200.dp)
                    .padding(8.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
            ) {
                Text("Sign Out")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview(){
    HomeScreen(navController = NavController(MainActivity()), onSignOut = {}, authViewModel = AuthViewModel())
}