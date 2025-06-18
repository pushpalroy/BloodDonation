package com.example.blooddonation.feature.splashscreen

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import com.example.blooddonation.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

@Composable
fun SplashScreen(
    onNavigateToAdminDashboard: () -> Unit,
    onNavigateToDashboard: (String) -> Unit,
    onNavigateToSignup: () -> Unit
) {
    val startAnimation by remember { mutableStateOf(true) }
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "Logo Bounce"
    )
    var currentUser by remember { mutableStateOf<FirebaseUser?>(null) }
    var authStateChecked by remember { mutableStateOf(false) }
    var navigationTriggered by remember { mutableStateOf(false) }

    val auth = remember { FirebaseAuth.getInstance() }
    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            currentUser = firebaseAuth.currentUser
            authStateChecked = true
            Log.d("SplashScreen", "currentUser: ${firebaseAuth.currentUser}")
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    LaunchedEffect(authStateChecked, currentUser) {
        if (authStateChecked && !navigationTriggered) {
            navigationTriggered = true
            val user = currentUser
            if (user != null) {
                try {
                    val doc = FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(user.uid)
                        .get()
                        .await()
                    val userDoc =
                        FirebaseFirestore.getInstance().collection("users")
                            .document(user.uid)
                    userDoc.update("fcmToken", FirebaseMessaging.getInstance().token)
                    val role = doc.getString("role") ?: ""
                    if (role == "Admin") {
                        onNavigateToAdminDashboard()
                    } else {
                        onNavigateToDashboard(user.uid)
                    }
                } catch (e: Exception) {
                    onNavigateToSignup()
                }
            } else {
                onNavigateToSignup()
            }
        }
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_crimson_sync_logo),
                contentDescription = "Crimson Sync Logo",
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale
                    )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Crimson Sync",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                fontSize = 28.sp
            )
            // Add progress bar so user always knows something is happening
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 32.dp)
            )
        }
    }
}
