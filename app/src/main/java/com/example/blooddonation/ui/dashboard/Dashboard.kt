package com.example.blooddonation.ui.dashboard

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.blooddonation.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    username: String,
    imageUri: String?
) {
    val redColor = Color(0xFFB71C1C)  // Crimson Red
    val whiteColor = Color(0xFFFFFFFF)  // White
    val blackColor = Color(0xFF000000)  // Black

    val customColors = lightColorScheme(
        primary = redColor,
        onPrimary = whiteColor,
        secondary = blackColor,
        onSecondary = whiteColor,
        surface = whiteColor,
        onSurface = blackColor,
        background = blackColor,
        onBackground = whiteColor
    )

    MaterialTheme(colorScheme = customColors) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("CrimsonSync", color = whiteColor) },
                    navigationIcon = {
                        IconButton(onClick = { /* Add action if needed */ }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = whiteColor)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = redColor)
                )
            },
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    // Header with profile pic and name
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
                                .background(whiteColor)
                        ) {
                            if (imageUri != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = imageUri,
                                        placeholder = painterResource(id = R.drawable.blood_background) // Add your placeholder
                                    ),
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
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
                                style = MaterialTheme.typography.titleMedium.copy(color = whiteColor)
                            )
                            Text(
                                text = username.ifEmpty { "User" },
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = whiteColor
                                )
                            )
                        }
                    }

                    // Grid with navigation cards
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
                                backgroundColor = blackColor,
                                iconColor = whiteColor
                            ) {
                                navController.navigate("request_blood") // ✅ Navigates to RequestBloodScreen
                            }
                        }
                        item {
                            DashboardCard(
                                title = "My Profile",
                                icon = Icons.Default.Person,
                                backgroundColor = redColor,
                                iconColor = whiteColor
                            ) {
                                navController.navigate("profile")
                            }
                        }
                        item {
                            DashboardCard(
                                title = "Settings",
                                icon = Icons.Default.Settings,
                                backgroundColor = blackColor,
                                iconColor = whiteColor
                            ) {
                                navController.navigate("settings")
                            }
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardCard(
    title: String,
    icon: ImageVector,
    backgroundColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = "$title Icon",
                modifier = Modifier.size(48.dp),
                tint = iconColor
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = iconColor
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}










