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
import requestblood.RequestBloodScreen
import signin.SignInScreen



@Composable
fun AppNavigation(navController: NavHostController) {
    val userViewModel: UserViewModel = viewModel()

    NavHost(navController = navController, startDestination = "splash") {

        // Splash Screen
        composable("splash") {
            SplashScreen(navController = navController)
        }

        // Registration Screen
        composable("registration") {
            RegistrationScreen(
                userViewModel = userViewModel,
                onNavigateToProfile = { uid ->
                    navController.navigate("profile/$uid")
                },
                onSignInClick = { navController.navigate("signin") }
            )
        }

        // Sign In Screen
        composable("signin") {
            SignInScreen(navController = navController)
        }

        // Profile Creation Screen
        composable("profile/{uid}") { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            ProfileCreationScreen(navController = navController, uid = uid)
        }

        // Dashboard Screen
        composable(
            route = "dashboard/{uid}",
            arguments = listOf(
                navArgument("uid") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            DashboardScreen(
                navController = navController,
                uid = uid
            )
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

