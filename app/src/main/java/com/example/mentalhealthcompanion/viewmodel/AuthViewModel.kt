package com.example.mentalhealthcompanion.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.mentalhealthcompanion.auth.SignInResult
import com.example.mentalhealthcompanion.auth.SignInState
import com.example.mentalhealthcompanion.auth.UserData
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
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
        if(userData != null){
            _userData.value = userData
            saveDataToFirestore(userData){isSuccess ->
                if(!isSuccess){
                    _state.update {
                        it.copy(errorMessage = "Failed to save changes. Please try again.")
                    }
                    Log.e("AuthViewModel", "Failed to save user data to Firestore")
                }
            }
        }
    }

    fun verifyCurrentPassword(currentPassword : String, onResult: (Boolean)-> Unit){
        val user = FirebaseAuth.getInstance().currentUser
        if(user != null && user.email != null){
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential)
                .addOnCompleteListener{
                    onResult(it.isSuccessful)
                }
        }else{
            onResult(false)
        }
    }

    fun updatePassword(newPassword : String, onResult: (Boolean, String?) -> Unit){
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null){
            user.updatePassword(newPassword)
                .addOnCompleteListener{
                    if (it.isSuccessful) onResult(true, null)
                    else onResult(false, it.exception?.message)
                }
        }else{
            onResult(false, "No user logged in")
        }
    }

    fun saveDataToFirestore(userData: UserData, onResult: (Boolean) -> Unit){
        val user = FirebaseAuth.getInstance().currentUser
        if(user != null){
            val userId = user.uid
            val firestore = FirebaseFirestore.getInstance()

            firestore.collection("users").document(userId)
                .set(
                    mapOf(
                        "username" to userData.username,
                        "email" to userData.email,
                        "profilePictureUrl" to userData.profilePictureUrl
                    )
                )
                .addOnSuccessListener {
                    onResult(true)
                }
                .addOnFailureListener{exception ->
                    onResult(false)
                    Log.e("AuthViewModel", "Error saving data to Firestore", exception)
                }
        }else{
            onResult(false)
        }
    }

    fun fetchUserDataFromFirestore(onResult: (Boolean) -> Unit){
        val user = FirebaseAuth.getInstance().currentUser
        if(user != null){
            val userId = user.uid
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userData = document.toObject(UserData::class.java)
                        _userData.value = userData
                        onResult(true)
                    } else {
                        onResult(false)
                    }
                }
                .addOnFailureListener { exception ->
                    onResult(false)
                }
        }else{
            onResult(false)
        }
    }

    fun updateUserProfilePic(newProfilePicUrl : String){
        _userData.value = _userData.value?.copy(profilePictureUrl = newProfilePicUrl)
    }

    fun resetState(){
        _state.update { SignInState() }
        _userData.value = null
    }
}