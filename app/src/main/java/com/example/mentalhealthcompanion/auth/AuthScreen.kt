package com.example.mentalhealthcompanion.auth

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthScreen(
    activity : Activity,
    onAuthSuccess:  () -> Unit,
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
                    Text("Please log in.", style = TextStyle(fontSize = 20.sp))

                }
                Text("Choose an authentication method:")
                Spacer(modifier = Modifier.height(18.dp))
                Button(onClick = {
                    authState = AuthState.EmailSignIn
                }) {
                    Text("Sign in with Email")
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(text = "Don't have an account?")
                Button(onClick = {
                    authState = AuthState.SignUp
                }) {
                    Text(text = "Sign Up")
                }
            }
        }
    }
}