package com.example.mentalhealthcompanion.auth

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String?,
    val username: String?,
    val email: String?,
    val profilePictureUrl: String? = null // Optional for email-password signup/login
)
