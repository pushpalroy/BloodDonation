
package com.example.registeractivity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clip
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RegisterActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        setContent {
            val navController = rememberNavController()
            AppNavigation(navController)

            LaunchedEffect(Unit) {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val name = currentUser.displayName ?: "Default Name"
                    val imageUri = currentUser.photoUrl?.toString() ?: "default_image_uri"

                    // Logging the values before navigation
                    Log.d("RegisterActivity", "Navigating to dashboard with name: $name, imageUri: $imageUri")
                    if (name.isNotEmpty() && imageUri.isNotEmpty()) {
                        navController.navigate("dashboard/$name/$imageUri")
                    } else {
                        Log.e("RegisterActivity", "Navigation arguments are invalid: name='$name', imageUri='$imageUri'")
                    }
                }
            }
        }
    }
}



@OptIn(DelicateCoroutinesApi::class)
@Composable
fun RegistrationScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val bloodGroups = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Register to CrimsonSync",
            style = MaterialTheme.typography.headlineMedium.copy(color = Color(0xFF6200EE)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        // UI elements for name, email, phone, password, and blood group
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { expanded = !expanded }) {
                Text(text = if (bloodGroup.isEmpty()) "Select Blood Group" else bloodGroup)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                bloodGroups.forEach { group ->
                    DropdownMenuItem(
                        text = { Text(text = group) },
                        onClick = {
                            bloodGroup = group
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Register button with Firebase Authentication and Firestore logic
        Button(
            onClick = {
                if (name.isNotEmpty() && email.isNotEmpty() &&
                    phoneNumber.isNotEmpty() && password.isNotEmpty() &&
                    bloodGroup.isNotEmpty()) {
                    isLoading = true
                    coroutineScope.launch {
                        try {
                            // Create user in Firebase Auth
                            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                            val userId = authResult.user?.uid ?: throw Exception("Failed to get user ID")

                            // Create user document in Firestore
                            val user = hashMapOf(
                                "name" to name,
                                "email" to email,
                                "phoneNumber" to phoneNumber,
                                "bloodGroup" to bloodGroup
                            )

                            firestore.collection("users")
                                .document(userId)
                                .set(user)
                                .await()

                            // Important: Reset loading state and navigate AFTER successful registration
                            isLoading = false

                            // Show success message
                            Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()

                            // Navigate to profile screen
                            navController.navigate("profile") {
                                popUpTo("registration") { inclusive = true }
                            }

                        } catch (e: FirebaseAuthWeakPasswordException) {
                            isLoading = false
                            Toast.makeText(context, "Password should be at least 6 characters", Toast.LENGTH_LONG).show()
                        } catch (e: FirebaseAuthUserCollisionException) {
                            isLoading = false
                            Toast.makeText(context, "Email already exists", Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            isLoading = false
                            Log.e("Registration", "Error during registration", e)
                            Toast.makeText(context, "Registration failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Please fill all details", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = "Register", color = Color.White)
            }
        }
    }
}


@Composable
fun ProfileCreationScreen(navController: NavHostController) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var username by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val bloodGroups = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Create Your Profile",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent(),
                    onResult = { uri: Uri? -> imageUri = uri }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Profile Picture Selector
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    imageUri?.let {
                        // Display the selected image
                        Image(
                            painter = rememberAsyncImagePainter(it),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                        )
                    } ?: run {
                        // Display default icon if no image selected
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Default Profile Picture",
                            modifier = Modifier.size(80.dp),
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Button to select image from gallery
                Button(
                    onClick = {
                        launcher.launch("image/*")  // Open gallery to select image
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "Select Profile Picture")
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Username Field
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Bio Field
                TextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Dropdown for BloodGroup
                Box(modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = { expanded = !expanded }) {
                        Text(text = if (bloodGroup.isEmpty()) "Select Blood Group" else bloodGroup)
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        bloodGroups.forEach { group ->
                            DropdownMenuItem(
                                text = { Text(text = group) },
                                onClick = {
                                    bloodGroup = group
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                Button(
                    onClick = {
                        // Ensure imageUri is not null before navigating
                        if (imageUri != null && username.isNotBlank()) {
                            Log.d(
                                "ProfileCreationScreen",
                                "Navigating to dashboard with username: $username, imageUri: ${imageUri.toString()}"
                            )
                            navController.navigate("dashboard/${username}/${Uri.encode(imageUri.toString())}")
                        } else {
                            Log.e("ProfileCreationScreen", "Username or ImageUri is null!")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                ) {
                    Text(
                        text = "Create Profile",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

