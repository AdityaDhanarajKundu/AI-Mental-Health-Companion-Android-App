package com.example.mentalhealthcompanion.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@Composable
fun EmailAuthUI(
    onSignInSuccess:  () -> Unit,
    onError: (String) -> Unit,
    onUserDataRetrieved: (UserData) -> Unit
) {
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it},
            label = { Text(text = "Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it},
            label = { Text(text = "Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Button(onClick = {
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val user = FirebaseAuth.getInstance().currentUser
                    user?.let {
                        onUserDataRetrieved(
                            UserData(
                                userId = it.uid,
                                username = it.displayName,
                                email = it.email,
                                profilePictureUrl = it.photoUrl?.toString()
                            )
                        )
                        onSignInSuccess()
                    }
                }
                .addOnFailureListener {
                    onError(it.message ?: "An error occurred!")
                }
        }) {
            Text(text = "Sign In")
        }
    }
}