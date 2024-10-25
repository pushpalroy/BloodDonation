package com.example.registeractivity

import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    username: String,
    imageUri: String?,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    var bottomSheetState by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CrimsonSync") },
                navigationIcon = {
                    IconButton(onClick = { bottomSheetState = !bottomSheetState }) {
                        Icon(Icons.Default.Menu, contentDescription = "Open Menu")
                    }
                }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.background), // Background color
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "Welcome to CrimsonSync",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Hello, ${username.ifEmpty { "User" }}!",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Profile Picture with Border and Shadow
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .shadow(4.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUri),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Default Profile Picture",
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Show the menu content when bottomSheetState is true
                if (bottomSheetState) {
                    MenuContent(navController) { bottomSheetState = false }
                }
            }
        }
    )
}

@Composable
fun MenuContent(navController: NavController, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface) // Background color for menu
            .padding(16.dp)
            .shadow(4.dp)
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        MenuButton("View Donors") {
            navController.navigate("view_donors")
            onClose()
        }
        MenuButton("Request Blood") {
            navController.navigate("request_blood")
            onClose()
        }
        MenuButton("My Profile") {
            navController.navigate("profile")
            onClose()
        }
        MenuButton("Settings") {
            navController.navigate("settings")
            onClose()
        }
    }
}

@Composable
fun MenuButton(title: String, onClick: () -> Unit) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable(
                onClick = onClick,
                indication = LocalIndication.current,
                interactionSource = remember { MutableInteractionSource() } // Required for ripple to work
            )
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clip(MaterialTheme.shapes.medium)
    )
}







