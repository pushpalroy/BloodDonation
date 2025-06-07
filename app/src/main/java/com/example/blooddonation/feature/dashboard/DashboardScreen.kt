package com.example.blooddonation.feature.dashboard

import android.net.Uri
import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.blooddonation.R
import kotlinx.coroutines.launch
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    uid: String,
    viewModel: DashboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
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

    var showBotDialog by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(uid) {
        viewModel.loadUser(uid)
    }

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
        viewModel.loadUser(uid)
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
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = redColor
                            )
                        } else if (uiState.errorMessage != null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = uiState.errorMessage ?: "An error occurred",
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
                                        if (!uiState.imageUri.isNullOrEmpty()) {
                                            val imageFile = File(uiState.imageUri!!)
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
                                            text = uiState.username,
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
                                            navController.navigate("my_profile/$uid")
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




/* ---------- shared palette ---------- */
private val Crimson   = Color(0xFFB71C1C)
private val OnCrimson = Color.White
private val JetBlack  = Color(0xFF000000)

/* ---------- reusable template ---------- */
@Composable
private fun StaticInfoScreen(
    @DrawableRes hero: Int,
    title: String,
    body: String
) {
    /* full-screen gradient */
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Crimson, JetBlack),
                    startY = 0f,
                    endY   = Float.POSITIVE_INFINITY
                )
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        /* card that floats above gradient */
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            colors = CardDefaults.cardColors(containerColor = OnCrimson),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                /* hero image */
                Image(
                    painter = painterResource(id = hero),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )

                /* slim accent under image */
                Spacer(
                    modifier = Modifier
                        .height(4.dp)
                        .fillMaxWidth()
                        .background(Crimson, RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp))
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color       = Crimson,
                        fontWeight  = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color     = JetBlack,
                        lineHeight = 22.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/* ---------- individual screens ---------- */

@Composable
fun AboutUsScreen() = StaticInfoScreen(
    hero  = R.drawable.aboutus,
    title = "About CrimsonSync",
    body  = "CrimsonSync is a community-powered platform dedicated to connecting blood donors "
            + "with recipients quickly and efficiently. Our mission is to save lives by building "
            + "a trustworthy and fast blood-donation ecosystem."
)

@Composable
fun OurWorkScreen() = StaticInfoScreen(
    hero  = R.drawable.ourwork,
    title = "What Drives Us",
    body  = "“Every drop counts. We connect hearts to help save lives.”\n\n"
            + "CrimsonSync works to make blood donation seamless, reliable, and community-driven."
)

@Composable
fun HelpScreen() = StaticInfoScreen(
    hero  = R.drawable.helpus,
    title = "Need Help?",
    body  = "For any queries or assistance, reach out to us at:\n\n"
            + "crimsonsync@gmail.com\n\n"
            + "Our team is here to support you 24 × 7."
)


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










