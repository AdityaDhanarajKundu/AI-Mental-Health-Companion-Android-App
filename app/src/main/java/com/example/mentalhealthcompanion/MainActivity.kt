package com.example.mentalhealthcompanion

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.mentalhealthcompanion.auth.AuthScreen
import com.example.mentalhealthcompanion.auth.GoogleSignInClient
import com.example.mentalhealthcompanion.auth.SignInState
import com.example.mentalhealthcompanion.db.DailyCheckInDao
import com.example.mentalhealthcompanion.db.JournalDb
import com.example.mentalhealthcompanion.ui.screens.HomeScreen
import com.example.mentalhealthcompanion.ui.screens.JournalScreen
import com.example.mentalhealthcompanion.ui.screens.MoodAnalysis
import com.example.mentalhealthcompanion.ui.screens.ProfileScreen
import com.example.mentalhealthcompanion.ui.theme.MentalHealthCompanionTheme
import com.example.mentalhealthcompanion.viewmodel.AuthViewModel
import com.example.mentalhealthcompanion.viewmodel.JournalViewModel
import com.example.mentalhealthcompanion.viewmodel.MoodViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val googleSignInClient by lazy {
        GoogleSignInClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    private val authViewModel = AuthViewModel()
    companion object{
        lateinit var database : JournalDb
            private set
    }
    private val dao : DailyCheckInDao by lazy {
        database.dailyCheckInDao()
    }
    private lateinit var journalViewModel: JournalViewModel
    private lateinit var moodViewModel: MoodViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Room.databaseBuilder(
            applicationContext,
            JournalDb::class.java,
            "journal_db",
        ).fallbackToDestructiveMigration()
            .build()

        journalViewModel = JournalViewModel(dao)
        moodViewModel = MoodViewModel(dao)
        setContent {
            MentalHealthCompanionTheme {
                val isAuthenticated = remember { mutableStateOf(false) }
                val navController = rememberNavController()

                val viewModel = authViewModel
                val state by viewModel.state.collectAsStateWithLifecycle()
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartIntentSenderForResult(),
                    onResult = {
                        if (it.resultCode == RESULT_OK) {
                            lifecycleScope.launch {
                                val signInResult = googleSignInClient.signInWithIntent(
                                    intent = it.data ?: return@launch
                                )
                                viewModel.onSignInResult(signInResult)
                                if (signInResult.data != null) {
                                    viewModel.updateUserData(signInResult.data)
                                }
                            }
                        }
                    }
                )
                NavHost(
                    navController = navController,
                    startDestination = if (isAuthenticated.value) "home" else "login",
                ) {
                    composable("login") {

                        LaunchedEffect(key1 = state.isSignInSuccessful) {
                            if (state.isSignInSuccessful) {
                                isAuthenticated.value = true
                                Toast.makeText(
                                    applicationContext,
                                    "Sign-in successful",
                                    Toast.LENGTH_LONG
                                ).show()
                                navController.navigate("home")
                            }
                        }
                        LoginScreen(
                            state = state,
                            activity = this@MainActivity,
                            onAuthSuccess = {
                                isAuthenticated.value = true
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onGoogleAuth = {
                                lifecycleScope.launch {
                                    val signInIntentSender = googleSignInClient.signInWithGoogle()
                                    launcher.launch(
                                        IntentSenderRequest.Builder(
                                            signInIntentSender ?: return@launch
                                        ).build()
                                    )
                                }
                            },
                            authViewModel = authViewModel
                        )
                    }
                    composable("home") {
                        HomeScreen(
                            navController = navController,
                            viewModel = journalViewModel,
                            onSignOut = {
                                isAuthenticated.value = false
                                FirebaseAuth.getInstance().signOut()
                                viewModel.resetState()
                                navController.navigate("login"){
                                    popUpTo("home") { inclusive = true } // Clear back stack
                                }
                            },
                            authViewModel = authViewModel
                        )
                    }
                    composable("journal") {
                        JournalScreen(
                            navController = navController,
                            journalViewModel = journalViewModel,
                            onSignOut = {
                                isAuthenticated.value = false
                                FirebaseAuth.getInstance().signOut()
                                viewModel.resetState()
                                navController.navigate("login"){
                                    popUpTo("home") { inclusive = true } // Clear back stack
                                }
                            }
                        )
                    }
                    composable("profile") {
                        ProfileScreen(
                            navController = navController,
                            authViewModel = authViewModel,
                            moodViewModel = moodViewModel,
                            onSignOut = {
                                isAuthenticated.value = false
                                FirebaseAuth.getInstance().signOut()
                                viewModel.resetState()
                                navController.navigate("login"){
                                    popUpTo("home") { inclusive = true } // Clear back stack
                                }
                            }
                        )
                    }
                    composable("mood") {
                        MoodAnalysis(
                            navController = navController,
                            moodViewModel = moodViewModel,
                            onSignOut = {
                                isAuthenticated.value = false
                                FirebaseAuth.getInstance().signOut()
                                viewModel.resetState()
                                navController.navigate("login"){
                                    popUpTo("home") { inclusive = true } // Clear back stack
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    state: SignInState,
    activity: Activity?,
    onAuthSuccess: () -> Unit,
    onGoogleAuth: () -> Unit,
    authViewModel: AuthViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ){
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 64.dp),
            contentAlignment = Alignment.Center
        ){
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "E-motionAI logo",
                        modifier = Modifier
                            .size(250.dp)
                            .padding(bottom = 0.dp)
                    )
                    AuthScreen(
                        state = state,
                        activity = activity,
                        onAuthSuccess = onAuthSuccess,
                        onGoogleAuth = onGoogleAuth,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuickPreview() {
    MentalHealthCompanionTheme {
        LoginScreen(
            state = SignInState(),
            activity = null,
            onAuthSuccess = {},
            onGoogleAuth = {},
            authViewModel = AuthViewModel()
        )
    }
}