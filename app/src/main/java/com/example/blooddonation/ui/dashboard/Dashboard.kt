package com.example.blooddonation.ui.dashboard

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.blooddonation.R
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    uid: String
) {
    val redColor = Color(0xFFB71C1C)  // Crimson Red
    val whiteColor = Color(0xFFFFFFFF)
    val blackColor = Color(0xFF000000)

    val customColors = lightColorScheme(
        primary = redColor,
        onPrimary = whiteColor,
        secondary = blackColor,
        onSecondary = whiteColor,
        surface = whiteColor,
        onSurface = blackColor,
        background = whiteColor,
        onBackground = blackColor  // Fixed color scheme
    )

    var username by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val firestore = FirebaseFirestore.getInstance()
    val userDocRef = firestore.collection("users").document(uid)

    LaunchedEffect(uid) {
        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    username = document.getString("name") ?: "User"
                    imageUri = document.getString("profileImagePath")  // Get local image path from Firestore
                    isLoading = false
                } else {
                    errorMessage = "User profile not found"
                    isLoading = false
                }
            }
            .addOnFailureListener { e ->
                errorMessage = "Error loading profile: ${e.message}"
                isLoading = false
                Log.e("DashboardScreen", "Error fetching profile", e)
            }
    }

    MaterialTheme(colorScheme = customColors) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("CrimsonSync", color = whiteColor) },
                    navigationIcon = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = whiteColor)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = redColor)
                )
            },
            content = { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = redColor
                        )
                    } else if (errorMessage != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = errorMessage ?: "An error occurred",
                                color = redColor,
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(onClick = {
                                navController.navigate("signin") {
                                    popUpTo("dashboard") { inclusive = true }
                                }
                            }) {
                                Text("Go to Sign In")
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, redColor, CircleShape)
                                        .background(whiteColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!imageUri.isNullOrEmpty()) {
                                        // Load image from local storage path
                                        val imageFile = File(imageUri!!)
                                        if (imageFile.exists()) {
                                            AsyncImage(
                                                model = Uri.fromFile(imageFile),
                                                contentDescription = "Profile Picture",
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(CircleShape),
                                                contentScale = ContentScale.Crop,
                                                error = painterResource(id = android.R.drawable.ic_menu_gallery)
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = "Default Profile Picture",
                                                modifier = Modifier.size(50.dp),
                                                tint = blackColor
                                            )
                                        }
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Default Profile Picture",
                                            modifier = Modifier.size(50.dp),
                                            tint = blackColor
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                    Text(
                                        text = "Welcome,",
                                        style = MaterialTheme.typography.titleMedium.copy(color = blackColor)
                                    )
                                    Text(
                                        text = username,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = blackColor
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(48.dp))

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                item {
                                    DashboardCard(
                                        title = "View Donors",
                                        icon = Icons.Default.Person,
                                        backgroundColor = redColor,
                                        iconColor = whiteColor
                                    ) {
                                        navController.navigate("view_donors")
                                    }
                                }
                                item {
                                    DashboardCard(
                                        title = "Request Blood",
                                        icon = Icons.Default.Favorite,
                                        backgroundColor = redColor,
                                        iconColor = whiteColor
                                    ) {
                                        navController.navigate("request_blood")
                                    }
                                }
                                item {
                                    DashboardCard(
                                        title = "My Profile",
                                        icon = Icons.Default.Person,
                                        backgroundColor = redColor,
                                        iconColor = whiteColor
                                    ) {
                                        navController.navigate("profile/$uid")
                                    }
                                }
                                item {
                                    DashboardCard(
                                        title = "Settings",
                                        icon = Icons.Default.Settings,
                                        backgroundColor = redColor,
                                        iconColor = whiteColor
                                    ) {
                                        navController.navigate("settings")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}


@Composable
fun DashboardCard(
    title: String,
    icon: ImageVector,
    backgroundColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .height(140.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = iconColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}










