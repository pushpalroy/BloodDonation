package com.example.blooddonation.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.blooddonation.navigation.Screen
import com.example.blooddonation.feature.chat.ChatScreen
import com.example.blooddonation.feature.admin.AdminDashboardScreen
import com.example.blooddonation.feature.admin.AdminViewModel
import com.example.blooddonation.feature.dashboard.AboutUsScreen
import com.example.blooddonation.feature.dashboard.DashboardScreen
import com.example.blooddonation.feature.dashboard.HelpScreen
import com.example.blooddonation.feature.dashboard.OurWorkScreen
import com.example.blooddonation.feature.events.BloodCampListScreen
import com.example.blooddonation.feature.profile.MyProfileScreen
import com.example.blooddonation.feature.profile.ProfileCreationScreen
import com.example.blooddonation.feature.signup.SignUpScreen
import com.example.blooddonation.feature.signup.SignupViewModel
import com.example.blooddonation.feature.requestblood.BloodRequestViewModel
import com.example.blooddonation.feature.splashscreen.SplashScreen
import com.example.blooddonation.feature.requestblood.BloodRequestScreen
import com.example.blooddonation.feature.signin.SignInScreen
import com.example.blooddonation.feature.viewdonors.ViewDonorsScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    val userViewModel: SignupViewModel = viewModel()
    val adminViewModel: AdminViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToAdminDashboard = {
                    navController.navigate(Screen.AdminDashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToDashboard = { uid ->
                    navController.navigate(Screen.Dashboard.createRoute(uid)) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToSignup = {
                    navController.navigate(Screen.SignUp.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                viewModel = userViewModel,
                onNavigateToProfile = { routeId ->
                    if (routeId == Screen.AdminDashboard.route) {
                        navController.navigate(Screen.AdminDashboard.route) {
                            popUpTo(Screen.SignUp.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Profile.createRoute(routeId)) {
                            popUpTo(Screen.SignUp.route) { inclusive = true }
                        }
                    }
                },
                onSignInClick = {
                    navController.navigate(Screen.SignIn.route) {
                        launchSingleTop = true
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.SignIn.route) {
            SignInScreen(
                onNavigateToProfile = { uid -> navController.navigate(Screen.Profile.createRoute(uid)) },
                onNavigateToSignup = {
                    navController.navigate(Screen.SignUp.route) {
                        launchSingleTop = true
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onSignInSuccess = { uid, isAdmin ->
                    if (isAdmin) {
                        navController.navigate(Screen.AdminDashboard.route) {
                            popUpTo(Screen.SignIn.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate(Screen.Dashboard.createRoute(uid)) {
                            popUpTo(Screen.SignIn.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        composable(
            route = Screen.Profile.routeWithArgs,
            arguments = listOf(navArgument(Screen.Profile.ARG_UID) { type = NavType.StringType })
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString(Screen.Profile.ARG_UID) ?: ""
            ProfileCreationScreen(
                uid = uid,
                onNavigateToDashboard = { username, imageUri, uid ->
                    val encodedUri = Uri.encode(imageUri)
                    navController.navigate(Screen.Dashboard.createRoute(username, encodedUri, uid)) {
                        popUpTo(Screen.Profile.createRoute(uid)) { inclusive = true }
                    }
                }
            )
        }


        composable(
            route = Screen.Dashboard.routeWithUid,
            arguments = listOf(navArgument(Screen.Dashboard.ARG_UID) { type = NavType.StringType })
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString(Screen.Dashboard.ARG_UID) ?: ""
            DashboardScreen(
                uid = uid,
                onAboutUs = { navController.navigate(Screen.AboutUs.route) },
                onOurWork = { navController.navigate(Screen.OurWork.route) },
                onHelp = { navController.navigate(Screen.Help.route) },
                onLogout = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Dashboard.createRoute(uid)) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onViewDonors = { navController.navigate(Screen.ViewDonors.createRoute(uid)) },
                onRequestBlood = { navController.navigate(Screen.RequestBlood.createRoute(uid)) },
                onMyProfile = { navController.navigate(Screen.MyProfile.createRoute(uid)) },
                onBloodCampList = { navController.navigate(Screen.BloodCampList.route) }
            )
        }

        composable(
            route = Screen.Dashboard.routeWithProfile,
            arguments = listOf(
                navArgument(Screen.Dashboard.ARG_USERNAME) { type = NavType.StringType },
                navArgument(Screen.Dashboard.ARG_IMAGE_URI) { type = NavType.StringType },
                navArgument(Screen.Dashboard.ARG_UID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString(Screen.Dashboard.ARG_UID) ?: ""
            DashboardScreen(
                uid = uid,
                onAboutUs = { navController.navigate(Screen.AboutUs.route) },
                onOurWork = { navController.navigate(Screen.OurWork.route) },
                onHelp = { navController.navigate(Screen.Help.route) },
                onLogout = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Dashboard.createRoute(uid)) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onViewDonors = { navController.navigate(Screen.ViewDonors.createRoute(uid)) },
                onRequestBlood = { navController.navigate(Screen.RequestBlood.createRoute(uid)) },
                onMyProfile = { navController.navigate(Screen.MyProfile.createRoute(uid)) },
                onBloodCampList = { navController.navigate(Screen.BloodCampList.route) }
            )
        }

        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                viewModel = adminViewModel
            )
        }


        // Static Screens
        composable(Screen.AboutUs.route) {
            AboutUsScreen()
        }

        composable(Screen.OurWork.route) {
            OurWorkScreen()
        }

        composable(Screen.Help.route) {
            HelpScreen()
        }


        // Other screens
        // Add NavType.StringType argument for currentUserId in both routes
        composable(
            route = Screen.ViewDonors.routeWithArgs,
            arguments = listOf(navArgument(Screen.ViewDonors.ARG_USER) { type = NavType.StringType })
        ) { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getString(Screen.ViewDonors.ARG_USER) ?: ""
            val bloodRequestViewModel: BloodRequestViewModel = viewModel()
            ViewDonorsScreen(
                onNavigateToChat = { chatId, currentId, requesterId ->
                    navController.navigate(Screen.Chat.createRoute(chatId, currentId, requesterId))
                },
                onBack = { navController.popBackStack() },
                viewModel = bloodRequestViewModel,
                currentUserId = currentUserId
            )
        }

        composable(
            route = Screen.RequestBlood.routeWithArgs,
            arguments = listOf(navArgument(Screen.RequestBlood.ARG_USER) { type = NavType.StringType })
        ) { backStackEntry ->
            val currentUserId = backStackEntry.arguments?.getString(Screen.RequestBlood.ARG_USER) ?: ""
            val bloodRequestViewModel: BloodRequestViewModel = viewModel()
            BloodRequestScreen(
                onBack = { navController.popBackStack() },
                onNavigateToChat = { chatId, currentId, donorId ->
                    navController.navigate(Screen.Chat.createRoute(chatId, currentId, donorId))
                },
                viewModel = bloodRequestViewModel,
                currentUserId = currentUserId
            )
        }
        
        composable(Screen.BloodCampList.route) {
            BloodCampListScreen()
        }

        composable(
            route = Screen.MyProfile.routeWithArgs,
            arguments = listOf(navArgument(Screen.MyProfile.ARG_UID) { type = NavType.StringType })
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString(Screen.MyProfile.ARG_UID) ?: ""
            MyProfileScreen(
                onBack = { navController.popBackStack() },
                uid = uid
            )
        }

        composable(Screen.Chat.routeWithArgs) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString(Screen.Chat.ARG_CHAT_ID) ?: ""
            val currentUserId = backStackEntry.arguments?.getString(Screen.Chat.ARG_CURRENT_ID) ?: ""
            val otherUserId = backStackEntry.arguments?.getString(Screen.Chat.ARG_OTHER_ID) ?: ""

            ChatScreen(
                chatId = chatId,
                currentUserId = currentUserId,
                otherUserId = otherUserId,
                onBack = { navController.popBackStack() },
            )
        }
    }
}





