package com.example.mentalhealthcompanion.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.mentalhealthcompanion.MainActivity
import com.example.mentalhealthcompanion.R
import com.example.mentalhealthcompanion.db.DailyCheckInDao
import com.example.mentalhealthcompanion.service.Quote
import com.example.mentalhealthcompanion.utils.Constants.tips
import com.example.mentalhealthcompanion.viewmodel.AuthViewModel
import com.example.mentalhealthcompanion.viewmodel.JournalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: JournalViewModel, onSignOut : () -> Unit = {}, authViewModel: AuthViewModel) {
    val quote = remember { mutableStateOf<Quote?>(null) }
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
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
        ) {
            item {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)) {
                    Text(
                        text = "Welcome back, ${userData?.username?.split(" ")?.get(0) ?: "User"}!",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "Take a moment for yourself today and choose what feels right for you.",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
            //Welcome Animation
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    elevation = CardDefaults.cardElevation(4.dp)
                ) { HomeAnimationView(resId = R.raw.home, modifier = Modifier.size(300.dp)) }
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ){
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(listOf(
                            Triple("Journal", Icons.Outlined.Edit, "journal"),
                            Triple("Meditation", Icons.Outlined.SelfImprovement, "meditation"),
                            Triple("Mood Analysis", Icons.Filled.MonitorHeart, "mood"),
                            Triple("Info", Icons.Outlined.Info, "info")
                        )){(title, icon, route) ->
                            ElevatedCard(
                                onClick = { navController.navigate(route) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                ),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ){
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ){
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = title,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleMedium,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
            // Quick Tips Section
            item {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ){
                            Icon(
                                imageVector = Icons.Filled.Checklist,
                                contentDescription = "Tips",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Quick Tips",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            )
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
                    }
                }
            }
            // Motivational Quote Section
            item {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ){
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ){
                            Icon(
                                imageVector = Icons.Outlined.FormatQuote,
                                contentDescription = "Quote",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Quote of the day",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            )
                        }
                        Text(
                            text = "\"${quote.value?.q ?: "Fetching wisdom..."}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 16.dp),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = "- ${quote.value?.a ?: ""}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 16.dp, top = 0.dp, start = 200.dp),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
            // Daily Check-In Section
            item {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    var dailyCheckIn by remember {mutableStateOf("")}
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ){
                        Text(
                            text = "Daily Check-In",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        OutlinedTextField(
                            value = dailyCheckIn,
                            onValueChange = { dailyCheckIn = it },
                            label = { Text("How's your day today?") },
                            shape = MaterialTheme.shapes.large,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                            ),
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Mood,
                                    contentDescription = "Feeling"
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                if (dailyCheckIn.isNotEmpty()) {
                                    viewModel.addCheckIn(dailyCheckIn)
                                    dailyCheckIn = ""
                                    feeling = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            elevation = ButtonDefaults.buttonElevation(4.dp)
                        ) {
                            Text("Save CheckIn", modifier = Modifier.padding(4.dp))
                        }
                    }
                }
            }
            // Sign Out Option
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { onSignOut() },
                        modifier = Modifier.width(200.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Sign Out")
                    }
                }
            }
            item {
                Text(
                    text = "Made with ❤️ by Aditya Dhanaraj Kundu",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun HomeAnimationView(resId: Int, modifier: Modifier = Modifier){
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resId))
    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview(){
    HomeScreen(navController = NavController(MainActivity()), viewModel = JournalViewModel(dao = MainActivity.database.dailyCheckInDao()), onSignOut = {}, authViewModel = AuthViewModel())
}