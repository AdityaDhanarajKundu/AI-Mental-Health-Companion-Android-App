package com.example.mentalhealthcompanion.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mentalhealthcompanion.auth.SignInResult
import com.example.mentalhealthcompanion.auth.SignInState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel: ViewModel(){
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult){
        _state.update { it.copy(
            isSignInSuccessful = result.data != null,
            errorMessage = result.errorMessage
        ) }
    }

    fun resetState(){
        _state.update { SignInState() }
    }
}