package com.example.blooddonation.feature

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.blooddonation.feature.theme.BloodBankTheme
import com.example.blooddonation.feature.theme.LocalIsDarkTheme
import com.example.blooddonation.feature.theme.LocalToggleTheme
import com.example.blooddonation.navigation.AppNavigation

@Composable
fun BloodBankApp() {
    var isDarkTheme by remember { mutableStateOf(false) }
    CompositionLocalProvider(
        LocalIsDarkTheme provides isDarkTheme,
        LocalToggleTheme provides { isDarkTheme = !isDarkTheme }
    ) {
        BloodBankTheme(darkTheme = isDarkTheme) {
            val navController = rememberNavController()
            AppNavigation(navController)
        }
    }
}