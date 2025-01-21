package com.example.mentalhealthcompanion.auth

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthScreen(
    activity: Activity?,
    onAuthSuccess:  () -> Unit,
    onGoogleAuth: () -> Unit,
) {
    var authState by remember {
        mutableStateOf<AuthState>(AuthState.Unauthenticated)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (authState){
            is AuthState.Loading -> CircularProgressIndicator()
            is AuthState.Error -> Text("Error: ${(authState as AuthState.Error).message}")
            is AuthState.EmailSignIn ->{
                EmailAuthUI(
                    onSignInSuccess = {
                        authState = AuthState.Authenticated
                        onAuthSuccess()
                    },
                    onError = {
                        authState = AuthState.Error(it)
                    }
                )
            }
            is AuthState.SignUp ->{
                SignUpUI(
                    onSignUpSuccess = {
                        authState = AuthState.SignedUp
                    },
                    onError = {
                        authState = AuthState.Error(it)
                    }
                )
            }
            else ->{
                // Default unauthenticated UI
                Column {
                    Text(
                        text = "Welcome to E-motionAI",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Please log in to continue",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                    Text("Choose an authentication method:")
                    Spacer(modifier = Modifier.height(18.dp))


                }

                GoogleAuthUI(
                    onSignInSuccess = onAuthSuccess,
                    onError = { message -> authState = AuthState.Error(message) },
                    onGoogleSignIn = onGoogleAuth
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    authState = AuthState.EmailSignIn
                }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = "Email Icon",
                            modifier = Modifier.size(24.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign In with Email")
                    }
                }
                Spacer(modifier = Modifier.height(45.dp))
                Text(text = "Don't have an account?")
                Button(onClick = {
                    authState = AuthState.SignUp
                }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Email Icon",
                            modifier = Modifier.size(24.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign Up")
                    }
                }
            }
        }
    }
}