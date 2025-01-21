package com.example.mentalhealthcompanion

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mentalhealthcompanion.auth.AuthScreen
import com.example.mentalhealthcompanion.ui.theme.MentalHealthCompanionTheme
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    private val REQ_ONE_TAP = 2
    private var showOneTapUI = true

    private val oneTapSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ){
        if(it.resultCode == RESULT_OK){
            try{
                val credential = oneTapClient.getSignInCredentialFromIntent(it.data)
                val idToken = credential.googleIdToken
                if(idToken != null){
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                //updateUI(user)
                            } else {
                                Log.w(TAG, "signInWithCredential:failure", task.exception)
                                //updateUI(null)
                                Toast.makeText(
                                    this,
                                    "Authentication failed: ${task.exception?.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                } else {
                    Log.d(TAG, "No ID token received!")
                    Toast.makeText(
                        this, "Failed to retrieve ID token. Please try again.", Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: ApiException) {
                Log.e(TAG, "ApiException while retrieving credentials: ${e.localizedMessage}", e)
                Toast.makeText(
                    this,
                    "An error occurred during sign-in: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during sign-in: ${e.localizedMessage}", e)
                Toast.makeText(
                    this, "An unexpected error occurred. Please try again.", Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()  // Initialize the firebase authentication instance
        // initialize the one tap client
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder().setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder().setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false).build()
            ).build()

        setContent {
            MentalHealthCompanionTheme {
                val isAuthenticated = remember { mutableStateOf(false) }
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen(
                        activity = this,
                        onAuthSuccess = { isAuthenticated.value = true },
                        onGoogleAuth = {
                            startGoogleSignIn()
                            isAuthenticated.value = true
                        }
                    )
                    if (isAuthenticated.value) {
                        Text(text = "Authenticated User", style = TextStyle(fontSize = 30.sp))
                    } else {
                        Text(text = "Unauthenticated User", style = TextStyle(fontSize = 30.sp))
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }

    private fun startGoogleSignIn() {
        oneTapClient.beginSignIn(signInRequest).addOnSuccessListener(this) { result ->
                try {
                    oneTapSignInLauncher.launch(
                        IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.e(TAG, "Failed to launch sign-in IntentSender: ${e.localizedMessage}", e)
                    Toast.makeText(
                        this, "Failed to start Google Sign-In. Please try again.", Toast.LENGTH_LONG
                    ).show()
                }
            }.addOnFailureListener(this) {
                Log.e(TAG, "Error starting Google SignIn : ${it.localizedMessage}", it)
                Toast.makeText(
                    this, "Google Sign-In failed: ${it.localizedMessage}", Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun reload() {
        // Logic to refresh the activity or navigate to the appropriate screen
    }
//    private fun updateUI(user: FirebaseUser?) {
//        if (user != null) {
//            // Navigate to the next screen or update UI with user's info
//
//        } else {
//            // Stay on the current screen or show an error
//        }
//    }
}

@Composable
fun LoginScreen(
    activity: Activity?,
    onAuthSuccess: () -> Unit,
    onGoogleAuth: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "E-motionAI logo",
                modifier = Modifier.size(250.dp).padding(bottom = 0.dp)
            )
            AuthScreen(
                activity = activity,
                onAuthSuccess = onAuthSuccess,
                onGoogleAuth = onGoogleAuth,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuickPreview() {
    MentalHealthCompanionTheme {
        LoginScreen(
            activity = null,
            onAuthSuccess = {},
            onGoogleAuth = {}
        )
    }
}