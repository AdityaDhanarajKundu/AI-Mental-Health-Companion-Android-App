package com.example.mentalhealthcompanion.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mentalhealthcompanion.auth.SignInResult
import com.example.mentalhealthcompanion.auth.SignInState
import com.example.mentalhealthcompanion.auth.UserData
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel: ViewModel(){
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()
    private val _userData = MutableStateFlow<UserData?>(null)
    val userData : StateFlow<UserData?> = _userData.asStateFlow()

    fun onSignInResult(result: SignInResult){
        _state.update { it.copy(
            isSignInSuccessful = result.data != null,
            errorMessage = result.errorMessage
        ) }
    }

    fun updateUserData(userData: UserData?){
        _userData.value = userData
    }

    fun resetState(){
        _state.update { SignInState() }
        _userData.value = null
    }
}