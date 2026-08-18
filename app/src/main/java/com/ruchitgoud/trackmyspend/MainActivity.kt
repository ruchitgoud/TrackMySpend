package com.ruchitgoud.trackmyspend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ruchitgoud.trackmyspend.data.AppDatabase
import com.ruchitgoud.trackmyspend.data.ThemePreference
import com.ruchitgoud.trackmyspend.data.TransactionRepository
import com.ruchitgoud.trackmyspend.data.UserPreferencesRepository
import com.ruchitgoud.trackmyspend.ui.screens.LandingScreen
import com.ruchitgoud.trackmyspend.ui.screens.TrackerScreen
import com.ruchitgoud.trackmyspend.ui.theme.TrackMySpendTheme
import com.ruchitgoud.trackmyspend.ui.viewmodel.TransactionViewModel
import com.ruchitgoud.trackmyspend.ui.viewmodel.TransactionViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = AppDatabase.getDatabase(this)
        val repository = TransactionRepository(database.transactionDao())
        val userPreferencesRepository = UserPreferencesRepository(this)
        
        enableEdgeToEdge()
        setContent {
            val viewModel: TransactionViewModel = viewModel(
                factory = TransactionViewModelFactory(repository, userPreferencesRepository)
            )
            val themePreference by viewModel.themePreference.collectAsState()
            
            val darkTheme = when (themePreference) {
                ThemePreference.LIGHT -> false
                ThemePreference.DARK -> true
                ThemePreference.SYSTEM -> isSystemInDarkTheme()
            }

            TrackMySpendTheme(darkTheme = darkTheme) {
                MainApp(viewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: TransactionViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "landing",
        enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(500)) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(500)) + fadeOut() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(500)) + fadeIn() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(500)) + fadeOut() }
    ) {
        composable("landing") {
            LandingScreen(
                onGetStarted = { navController.navigate("tracker") }
            )
        }
        composable("tracker") {
            TrackerScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
