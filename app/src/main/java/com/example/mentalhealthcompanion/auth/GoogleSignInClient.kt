package com.example.mentalhealthcompanion.auth

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.example.mentalhealthcompanion.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class GoogleSignInClient(
    private val context: Context,
    private val oneTapClient: SignInClient
) {
    private val auth = FirebaseAuth.getInstance()

    suspend fun signInWithGoogle(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        }catch (e: Exception){
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun signOut(){
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        }catch (e: Exception){
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    fun getSignedInUser(): UserData? = auth.currentUser?.run {
        UserData(
            userId = uid,
            username = displayName,
            email = email,
            profilePictureUrl = photoUrl.toString()
        )
    }

    suspend fun signInWithIntent(intent : Intent) : SignInResult{
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try{
            val user = auth.signInWithCredential(firebaseCredential).await().user
            SignInResult(
                data = UserData(
                    userId = user?.uid,
                    username = user?.displayName,
                    email = user?.email,
                    profilePictureUrl = user?.photoUrl.toString()
                ),
                errorMessage = null
            )
        }catch (e: Exception){
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(null, e.message)
        }
    }

    private fun buildSignInRequest(): BeginSignInRequest{
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder().setSupported(true)
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false).build()
            ).build()
    }
}