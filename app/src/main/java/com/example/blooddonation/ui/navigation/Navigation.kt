package com.example.blooddonation.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun AppNavigation(navController: NavHostController,userViewModel: UserViewModel) {
    NavHost(navController = navController, startDestination = "registration") {
        composable("registration") {
            // Pass userViewModel to RegistrationScreen
            RegistrationScreen(userViewModel = userViewModel) {
                navController.navigate("profile")
            }
        }
        composable("profile") {
            ProfileCreationScreen(navController)
        }
        composable(
            route = "dashboard/{name}/{imageUri}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("imageUri") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            val imageUri = backStackEntry.arguments?.getString("imageUri")
            if (name != null && imageUri != null) {
                DashboardScreen(navController, name, imageUri)
            }
        }
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
fun RequestBloodScreen(navController: NavController) {
    // Your UI for the Request Blood screen
    Text("Request Blood Screen")
}

@Composable
fun SettingsScreen(navController: NavController) {
    // Your UI for the Settings screen
    Text("Settings Screen")
}
