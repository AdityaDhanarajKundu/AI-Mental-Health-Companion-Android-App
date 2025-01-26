package com.example.mentalhealthcompanion.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.example.mentalhealthcompanion.MainActivity
import com.example.mentalhealthcompanion.R
import com.example.mentalhealthcompanion.auth.AuthState
import com.example.mentalhealthcompanion.auth.UserData
import com.example.mentalhealthcompanion.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    onSignOut: () -> Unit
) {
    val userData by authViewModel.userData.collectAsState(initial = null)
    var authState by remember { mutableStateOf<AuthState>(AuthState.Authenticated) }
    val state by authViewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        authViewModel.fetchUserDataFromFirestore { isSuccess ->
            if (!isSuccess) {
                Log.e("AuthViewModel", "Failed to fetch user data from Firestore")
            }
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Made with ❤️ by Aditya Dhanaraj Kundu",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // profile picture
            Surface(
                shape = CircleShape,
                shadowElevation = 8.dp,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                modifier = Modifier.size(140.dp).padding(12.dp)
            ) {
                if(userData?.profilePictureUrl != null){
                    AsyncImage(
                        model = userData!!.profilePictureUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(120.dp),
                        contentScale = ContentScale.Crop
                    )
                }else{
                    Image(
                        painter = painterResource(id = R.drawable.userprofile),
                        contentDescription = "Default Profile Picture",
                        modifier = Modifier.size(120.dp)
                    )
                }
            }

            // User Information
            Text(
                text = userData?.username ?: "Guest User",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = userData?.email ?: "Email not available",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(24.dp))

            //Profile Action Section
            Card(
                modifier = Modifier.fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                when(authState){
                    is AuthState.EditProfile -> {
                        EditProfileUI(
                            userData = userData!!,
                            onSave = {
                                Toast.makeText(context, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
                                authState = AuthState.Authenticated
                            },
                            authViewModel = authViewModel
                        )
                        state.errorMessage?.let {errorMessage ->
                            Snackbar(
                                action = {
                                    TextButton(
                                        onClick = {
                                            authViewModel.updateUserData(userData)
                                            authViewModel.resetState()
                                        }
                                    ) {
                                        Text("Retry", color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            ){
                                Text(errorMessage, color = MaterialTheme.colorScheme.onError)
                            }
                        }
                    }
                    is AuthState.ChangePassword -> {
                        ChangePasswordUI(
                            authViewModel = authViewModel,
                            onPasswordChange = {currentPassword, newPassword ->
                                authViewModel.updatePassword(newPassword){isSuccess, errorMessage ->
                                    if(isSuccess){
                                        Toast.makeText(context,"Password changed successfully!", Toast.LENGTH_SHORT).show()
                                        authState = AuthState.Authenticated
                                    }else{
                                        errorMessage?.let {
                                            Toast.makeText(context,"Failed to change password: $errorMessage", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            },
                            onCancel = {
                                authState = AuthState.Authenticated
                            }
                        )
                    }
                    else -> {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Profile Actions",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center,
                            )
                            ActionItem("Edit Profile", Icons.Default.Edit, onClick = {authState = AuthState.EditProfile})
                            ActionItem("View Reports", Icons.Default.BarChart, onClick = { /* Handle Reports */ })
                            ActionItem("Change Password", Icons.Default.Lock, onClick = { authState = AuthState.ChangePassword })
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            //Sign Out Button
            Button(
                onClick = {
                    onSignOut()
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(200.dp)
                    .padding(8.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
            ) {
                Text("Sign Out")
            }
        }
    }
}

@Composable
fun ActionItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ChangePasswordUI(
    authViewModel: AuthViewModel,
    onPasswordChange: (String, String) -> Unit,
    onCancel : () -> Unit
){
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = currentPassword,
            onValueChange = { currentPassword = it },
            label = { Text("Current Password") },
            trailingIcon = {
                IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible}) {
                    Icon(imageVector = if (currentPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = if (currentPasswordVisible) "Hide Password" else "Show Password")
                }
            },
            visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            trailingIcon = {
                IconButton(onClick = { newPasswordVisible = !newPasswordVisible}) {
                    Icon(imageVector = if (newPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = if (newPasswordVisible) "Hide Password" else "Show Password")
                }
            },
            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm New Password") },
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible}) {
                    Icon(imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = if (confirmPasswordVisible) "Hide Password" else "Show Password")
                }
            },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.primary
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    if (newPassword != confirmPassword){
                        errorMessage = "Passwords do not match"
                        return@Button
                    }
                    isLoading = true
                    authViewModel.verifyCurrentPassword(currentPassword){isValid ->
                        isLoading = false
                        if (isValid) {
                            onPasswordChange(currentPassword, newPassword)
                        } else {
                            errorMessage = "Current password is incorrect"
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Change Password")
            }
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
        }
    }
}

@Composable
fun EditProfileUI(userData: UserData, onSave: () -> Unit, authViewModel: AuthViewModel){
    var username by remember { mutableStateOf(userData?.username ?: "") }
    var email by remember { mutableStateOf(userData?.email ?: "") }
    var profilePictureUrl by remember { mutableStateOf<String?>(userData?.profilePictureUrl) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {uri ->
            uri?.let {
                profilePictureUrl = it.toString()
                authViewModel.updateUserProfilePic(it.toString())
            }
        }
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Image(
                painter = if (profilePictureUrl != null) rememberAsyncImagePainter(model = profilePictureUrl)
                else painterResource(id = R.drawable.userprofile),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable { launcher.launch("image/*") },
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(text = "Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {
                authViewModel.updateUserData(
                    UserData(
                        userId = userData.userId,
                        username = username,
                        email = email,
                        profilePictureUrl = profilePictureUrl
                    )
                )
                onSave()
            },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Text(text = "Save")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(navController = NavController(MainActivity()), authViewModel = AuthViewModel(), onSignOut = {})
}
