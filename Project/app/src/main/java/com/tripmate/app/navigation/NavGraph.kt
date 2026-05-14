package com.tripmate.app.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tripmate.app.screens.*

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Home : Screen("home")
    object Explore : Screen("explore")
    object TripDetail : Screen("trip_detail/{tripId}") {
        fun createRoute(tripId: String) = "trip_detail/$tripId"
    }
    object Expense : Screen("expense/{tripId}") {
        fun createRoute(tripId: String) = "expense/$tripId"
    }
    object Checklist : Screen("checklist/{tripId}") {
        fun createRoute(tripId: String) = "checklist/$tripId"
    }
    object Event : Screen("event/{tripId}") {
        fun createRoute(tripId: String) = "event/$tripId"
    }
    object Collaboration : Screen("collaboration/{tripId}") {
        fun createRoute(tripId: String) = "collaboration/$tripId"
    }
    object Notifications : Screen("notifications")
    object Profile : Screen("profile")
    object Todo : Screen("todo")
    object Community : Screen("community")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(500)) + fadeIn(animationSpec = tween(500))
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(500)) + fadeOut(animationSpec = tween(500))
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(500)) + fadeIn(animationSpec = tween(500))
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(500)) + fadeOut(animationSpec = tween(500))
        }
    ) {
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Signup.route) { SignupScreen(navController) }
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Explore.route) { ExploreScreen(navController) }
        composable(Screen.TripDetail.route) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            TripDetailScreen(navController, tripId)
        }
        composable(Screen.Expense.route) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            ExpenseScreen(navController, tripId)
        }
        composable(Screen.Checklist.route) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            ChecklistScreen(navController, tripId)
        }
        composable(Screen.Event.route) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            EventScreen(navController, tripId)
        }
        composable(Screen.Collaboration.route) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            CollaborationScreen(navController, tripId)
        }
        composable(Screen.Notifications.route) { NotificationScreen(navController) }
        composable(Screen.Profile.route) { ProfileScreen(navController) }
        composable(Screen.Todo.route) { TodoScreen(navController) }
        composable(Screen.Community.route) { CommunityScreen(navController) }
    }
}
