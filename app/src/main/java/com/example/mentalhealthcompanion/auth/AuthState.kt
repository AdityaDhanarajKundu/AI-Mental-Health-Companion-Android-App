package com.example.mentalhealthcompanion.auth

sealed class AuthState {
    data object Authenticated : AuthState()
    data object Unauthenticated : AuthState()
    data object SignedUp : AuthState()
    data object Loading : AuthState()
    data class Error(val message: String) : AuthState()
    data object EmailSignIn : AuthState()
    data object SignUp : AuthState()
}