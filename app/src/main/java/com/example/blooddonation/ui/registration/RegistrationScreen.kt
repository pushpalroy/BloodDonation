package com.example.blooddonation.ui.registration

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun RegistrationScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("Pushpal Roy") }
    var email by remember { mutableStateOf("pushpalroy2007@gmail.com") }
    var phoneNumber by remember { mutableStateOf("9007727246") }
    var password by remember { mutableStateOf("Pushpal12345") }
    var bloodGroup by remember { mutableStateOf("B+") }
    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val bloodGroups = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val auth = FirebaseAuth.getInstance()

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
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
                    bloodGroup.isNotEmpty()
                ) {
                    isLoading = true
                    coroutineScope.launch {
                        try {
                            // Create user in Firebase Auth
                            val authResult =
                                auth.createUserWithEmailAndPassword(email, password).await()
                            val userId =
                                authResult.user?.uid ?: throw Exception("Failed to get user ID")

                            // Create user document in Firestore
                            val user = hashMapOf(
                                "name" to name,
                                "email" to email,
                                "phoneNumber" to phoneNumber,
                                "bloodGroup" to bloodGroup
                            )

                            val db = Firebase.firestore
                            db.collection("users")
                                .document(userId)
                                .set(user)
                                .await()

                            // Important: Reset loading state and navigate AFTER successful registration
                            isLoading = false

                            // Show success message
                            Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT)
                                .show()

                            // Navigate to profile screen
                            navController.navigate("profile") {
                                popUpTo("registration") { inclusive = true }
                            }

                        } catch (e: FirebaseAuthWeakPasswordException) {
                            isLoading = false
                            Toast.makeText(
                                context,
                                "Password should be at least 6 characters",
                                Toast.LENGTH_LONG
                            ).show()
                        } catch (e: FirebaseAuthUserCollisionException) {
                            isLoading = false
                            Toast.makeText(context, "Email already exists", Toast.LENGTH_LONG)
                                .show()
                        } catch (e: Exception) {
                            isLoading = false
                            Log.e("Registration", "Error during registration", e)
                            Toast.makeText(
                                context,
                                "Registration failed: ${e.localizedMessage}",
                                Toast.LENGTH_LONG
                            ).show()
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