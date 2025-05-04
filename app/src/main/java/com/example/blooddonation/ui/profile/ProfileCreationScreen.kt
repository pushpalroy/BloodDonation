package com.example.blooddonation.ui.profile

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter


@Composable
fun ProfileCreationScreen(navController: NavHostController, uid: String) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var username by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val bloodGroups = remember { mutableStateListOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-") }

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
                    navController.navigate("dashboard/${username}/${Uri.encode(imageUri.toString())}/$uid") {
                        popUpTo("registration") { inclusive = true }
                    }
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




