package com.example.notestaking.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.notestaking.di.ViewModelFactory
import com.example.notestaking.ui.auth.LoginScreen
import com.example.notestaking.ui.auth.RegisterScreen
import com.example.notestaking.ui.home.HomeScreen
import com.example.notestaking.ui.note.NoteEditorScreen
import com.example.notestaking.ui.splash.SplashScreen

@Composable
fun NoteNovaNavGraph(factory: ViewModelFactory) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.SPLASH,
        enterTransition = { fadeIn() + slideInHorizontally { it / 6 } },
        exitTransition = { fadeOut() + slideOutHorizontally { -it / 6 } },
        popEnterTransition = { fadeIn() + slideInHorizontally { -it / 6 } },
        popExitTransition = { fadeOut() + slideOutHorizontally { it / 6 } }
    ) {
        composable(NavRoutes.SPLASH) {
            SplashScreen(
                factory = factory,
                onNavigateToHome = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                factory = factory,
                onNavigateToRegister = { navController.navigate(NavRoutes.REGISTER) },
                onLoginSuccess = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(NavRoutes.REGISTER) {
            RegisterScreen(
                factory = factory,
                onNavigateBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(NavRoutes.HOME) {
            HomeScreen(
                factory = factory,
                onNavigateToEditor = { noteId ->
                    navController.navigate(NavRoutes.noteEditor(noteId))
                },
                onLogout = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.HOME) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = NavRoutes.NOTE_EDITOR,
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
            NoteEditorScreen(
                noteId = noteId,
                factory = factory,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
