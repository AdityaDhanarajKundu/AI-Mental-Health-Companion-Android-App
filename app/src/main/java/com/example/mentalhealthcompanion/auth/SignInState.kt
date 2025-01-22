package com.example.mentalhealthcompanion.auth

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val errorMessage: String? = null
)
