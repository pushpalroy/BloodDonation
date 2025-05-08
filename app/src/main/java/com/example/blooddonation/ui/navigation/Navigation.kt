package com.example.blooddonation.ui.navigation

import android.net.Uri
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.blooddonation.ui.dashboard.DashboardScreen
import com.example.blooddonation.ui.profile.ProfileCreationScreen
import com.example.blooddonation.ui.registration.RegistrationScreen
import com.example.blooddonation.ui.registration.UserViewModel
import com.example.blooddonation.ui.splashscreen.SplashScreen
import com.example.blooddonation.ui.requestblood.RequestBloodScreen
import signin.SignInScreen





@Composable
fun AppNavigation(navController: NavHostController) {
    val userViewModel: UserViewModel = viewModel()

    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(navController = navController)
        }

        composable("registration") {
            RegistrationScreen(
                userViewModel = userViewModel,
                onNavigateToProfile = { uid ->
                    navController.navigate("profile/$uid") {
                        popUpTo("registration") { inclusive = true }
                    }
                },
                onSignInClick = {
                    navController.navigate("signin") {
                        launchSingleTop = true
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("signin") {
            SignInScreen(
                navController = navController,
                onSignInSuccess = { uid ->
                    navController.navigate("dashboard/$uid") {
                        popUpTo("signin") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = "profile/{uid}",
            arguments = listOf(navArgument("uid") { type = NavType.StringType })
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            ProfileCreationScreen(
                navController = navController,
                uid = uid
            ) { username, imageUri ->
                val encodedUri = Uri.encode(imageUri.toString())
                navController.navigate("dashboard/$username/$encodedUri/$uid") {
                    popUpTo("profile/$uid") { inclusive = true }
                }
            }
        }

        // Simplified Dashboard route for SignIn
        composable(
            route = "dashboard/{uid}",
            arguments = listOf(
                navArgument("uid") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            DashboardScreen(navController, uid)
        }

        // Full Dashboard route after Profile Creation
        composable(
            route = "dashboard/{username}/{imageUriEncoded}/{uid}",
            arguments = listOf(
                navArgument("username") { type = NavType.StringType },
                navArgument("imageUriEncoded") { type = NavType.StringType },
                navArgument("uid") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            DashboardScreen(navController, uid)
        }


// Other screens
        composable("view_donors") {
            ViewDonorsScreen(navController)
        }
        composable("request_blood") {
            RequestBloodScreen(navController)
        }
        composable("settings") {
            SettingsScreen(navController)
        }
    }
}


@Composable
fun ViewDonorsScreen(navController: NavController) {
    // Your UI for the View Donors screen
    Text("View Donors Screen")
}


@Composable
fun SettingsScreen(navController: NavController) {
    // Your UI for the Settings screen
    Text("Settings Screen")
}

