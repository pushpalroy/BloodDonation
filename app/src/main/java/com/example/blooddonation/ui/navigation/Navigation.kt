package com.example.blooddonation.ui.navigation

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
                onNavigateToProfile = { navController.navigate("profile") },
                onSignInClick = { navController.navigate("signin") }
            )
        }

        // Sign In Screen (you can implement this screen next)
        composable("signin") {
            SignInScreen(navController = navController)
        }

        // Profile Creation Screen
        composable("profile") {
            ProfileCreationScreen(navController)
        }

        // Dashboard Screen
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

