package com.example.blooddonation.ui.dashboard

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.blooddonation.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, uid: String) {
    val redColor = Color(0xFFB71C1C)
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
        onBackground = blackColor
    )

    var username by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showBotDialog by remember { mutableStateOf(false) }

    val firestore = FirebaseFirestore.getInstance()
    val userDocRef = firestore.collection("users").document(uid)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val botAnswers = mapOf(
        "how to request blood?" to "Go to the Dashboard and tap 'Request Blood'. Fill the form and submit.",
        "how to become a donor?" to "Create a profile and enable 'Donor Mode' in settings.",
        "how to update profile?" to "Tap 'My Profile' from Dashboard and click 'Edit'.",
        "what is crimsonsync?" to "CrimsonSync is a blood bank app for connecting donors and recipients.",
        "how to view donors?" to "Tap on 'View Donors' on the Dashboard to see a list of donors.",
        "how to contact a donor?" to "Request blood first. If accepted, you can chat with the donor.",
        "is crimsonsync free?" to "Yes! CrimsonSync is completely free to use.",
        "how to logout?" to "Open the menu and select 'Logout' at the bottom."
    )

    LaunchedEffect(uid) {
        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    username = document.getString("name") ?: ""
                    imageUri = document.getString("profileImagePath")
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
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "CrimsonSync",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                    Divider()
                    NavigationDrawerItem(
                        label = { Text("About Us") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("about_us")
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Our Work") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("our_work")
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Help") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("help")
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Logout") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("signin") {
                                popUpTo("dashboard") { inclusive = true }
                            }
                        }
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("CrimsonSync", color = whiteColor) },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = whiteColor)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = redColor)
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { showBotDialog = true },
                        containerColor = redColor
                    ) {
                        Icon(imageVector = Icons.Default.Create, contentDescription = "Chatbot", tint = whiteColor)
                    }
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
                                            icon = Icons.Default.Face,
                                            backgroundColor = redColor,
                                            iconColor = whiteColor
                                        ) {
                                            navController.navigate("view_donors/$uid")
                                        }
                                    }
                                    item {
                                        DashboardCard(
                                            title = "Request Blood",
                                            icon = Icons.Default.Favorite,
                                            backgroundColor = redColor,
                                            iconColor = whiteColor
                                        ) {
                                            navController.navigate("request_blood/$uid")
                                        }
                                    }
                                    item {
                                        DashboardCard(
                                            title = "My Profile",
                                            icon = Icons.Default.Person,
                                            backgroundColor = redColor,
                                            iconColor = whiteColor
                                        ) {
                                            navController.navigate("my_profile")
                                        }
                                    }
                                    item {
                                        DashboardCard(
                                            title = "Events",
                                            icon = Icons.Default.Search,
                                            backgroundColor = redColor,
                                            iconColor = whiteColor
                                        ) {
                                            navController.navigate("blood_camp_list")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (showBotDialog) {
                        AlertDialog(
                            onDismissRequest = { showBotDialog = false },
                            confirmButton = {
                                TextButton(onClick = { showBotDialog = false }) {
                                    Text("Close", color = redColor)
                                }
                            },
                            title = { Text("CrimsonBot - FAQs", color = redColor) },
                            text = {
                                Column {
                                    var userQuestion by remember { mutableStateOf("") }
                                    var botReply by remember { mutableStateOf("") }

                                    OutlinedTextField(
                                        value = userQuestion,
                                        onValueChange = {
                                            userQuestion = it
                                        },
                                        label = { Text("Ask a question") },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(
                                        onClick = {
                                            val cleanedInput = userQuestion.trim().lowercase()
                                            botReply = botAnswers.entries.firstOrNull { it.key.lowercase() == cleanedInput }?.value
                                                ?: "Sorry, I didn't understand that. Try asking a different question."
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = redColor)
                                    ) {
                                        Text("Send", color = whiteColor)
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    if (botReply.isNotEmpty()) {
                                        Text(
                                            text = botReply,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = blackColor
                                        )
                                    }

                                }
                            }
                        )
                    }
                }
            )
        }
    }
}




@Composable
fun AboutUsScreen() {
    val redColor = Color(0xFFB71C1C)
    val whiteColor = Color(0xFFFFFFFF)
    val blackColor = Color(0xFF000000)

    Surface(color = whiteColor) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.aboutus),
                contentDescription = "About Us",
                modifier = Modifier
                    .height(220.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "About CrimsonSync",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = redColor,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "CrimsonSync is a community-powered platform dedicated to connecting blood donors with recipients quickly and efficiently. Our mission is to save lives by building a trustworthy and fast blood donation ecosystem.",
                style = MaterialTheme.typography.bodyLarge.copy(color = blackColor),
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun OurWorkScreen() {
    val redColor = Color(0xFFB71C1C)
    val whiteColor = Color(0xFFFFFFFF)
    val blackColor = Color(0xFF000000)

    Surface(color = whiteColor) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ourwork),
                contentDescription = "Our Work",
                modifier = Modifier
                    .height(220.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "What Drives Us",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = redColor,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "\"Every drop counts. We connect hearts to help save lives.\"\n\nCrimsonSync works to make blood donation seamless, reliable, and community-driven.",
                style = MaterialTheme.typography.bodyLarge.copy(color = blackColor),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun HelpScreen() {
    val redColor = Color(0xFFB71C1C)
    val whiteColor = Color(0xFFFFFFFF)
    val blackColor = Color(0xFF000000)

    Surface(color = whiteColor) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.helpus),
                contentDescription = "Help",
                modifier = Modifier
                    .height(220.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Need Help?",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = redColor,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "For any queries or assistance, feel free to reach out to us at:\n\ncrimsonsync@gmail.com\n\nOur team is here to support you 24/7.",
                style = MaterialTheme.typography.bodyLarge.copy(color = blackColor),
                textAlign = TextAlign.Center
            )
        }
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










