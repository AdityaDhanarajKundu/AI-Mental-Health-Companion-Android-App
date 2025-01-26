package com.example.mentalhealthcompanion.auth

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    var userId: String? = null,
    var username: String? = null,
    var email: String? = null,
    var profilePictureUrl: String? = null  // Optional for email-password signup/login
){
    constructor() : this(null, null, null, null)
}
