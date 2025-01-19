package com.example.mentalhealthcompanion.auth

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun GoogleAuthUI(
    onSignInSuccess: () -> Unit,
    onError: (String) -> Unit,
    onGoogleSignIn: () -> Unit
) {
    Button(onClick = {
        onGoogleSignIn()
    }) {
        Text(text = "Sign in with Google")
    }
}