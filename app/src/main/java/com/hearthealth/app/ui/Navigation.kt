package com.hearthealth.app.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hearthealth.app.ui.screens.DeepDiveScreen
import com.hearthealth.app.ui.screens.HomeScreen
import com.hearthealth.app.ui.screens.JournalScreen
import com.hearthealth.app.ui.screens.LifestyleScreen
import com.hearthealth.app.ui.screens.MedicationScreen
import com.hearthealth.app.ui.screens.ProfileScreen
import com.hearthealth.app.ui.viewmodel.AuthViewModel
import com.hearthealth.app.ui.viewmodel.GeminiViewModel
import com.hearthealth.app.ui.viewmodel.JournalViewModel
import com.hearthealth.app.ui.viewmodel.LifestyleViewModel
import com.hearthealth.app.ui.viewmodel.MedicationViewModel
import com.hearthealth.app.ui.viewmodel.SyncViewModel

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Medications : Screen("medications")
    data object Journal : Screen("journal")
    data object Lifestyle : Screen("lifestyle")
    data object DeepDive : Screen("deep_dive")
    data object Profile : Screen("profile")
}

@Composable
fun HeartHealthNavigation() {
    val navController = rememberNavController()

    val medicationViewModel: MedicationViewModel = viewModel()
    val journalViewModel: JournalViewModel = viewModel()
    val lifestyleViewModel: LifestyleViewModel = viewModel()
    val geminiViewModel: GeminiViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val syncViewModel: SyncViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToMedications = { navController.navigate(Screen.Medications.route) },
                onNavigateToJournal = { navController.navigate(Screen.Journal.route) },
                onNavigateToLifestyle = { navController.navigate(Screen.Lifestyle.route) },
                onNavigateToDeepDive = { navController.navigate(Screen.DeepDive.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
            )
        }
        composable(Screen.Medications.route) {
            MedicationScreen(
                viewModel = medicationViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Journal.route) {
            JournalScreen(
                viewModel = journalViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Lifestyle.route) {
            LifestyleScreen(
                viewModel = lifestyleViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.DeepDive.route) {
            DeepDiveScreen(
                medicationViewModel = medicationViewModel,
                geminiViewModel = geminiViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                authViewModel = authViewModel,
                syncViewModel = syncViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
