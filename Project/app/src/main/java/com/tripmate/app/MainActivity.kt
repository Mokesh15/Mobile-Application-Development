package com.tripmate.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tripmate.app.navigation.NavGraph
import com.tripmate.app.navigation.Screen
import com.tripmate.app.ui.theme.TripMateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripMateTheme(darkTheme = com.tripmate.app.data.MockDataProvider.isDarkMode) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val showBottomBar = currentDestination?.route in listOf(
                    Screen.Home.route,
                    Screen.Explore.route,
                    Screen.Community.route,
                    Screen.Profile.route
                )

                Scaffold(
                    bottomBar = {
                        AnimatedVisibility(
                            visible = showBottomBar,
                            enter = slideInVertically(initialOffsetY = { it }),
                            exit = slideOutVertically(targetOffsetY = { it })
                        ) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 8.dp
                            ) {
                                val items = listOf(
                                    NavigationItem("Home", Screen.Home.route, Icons.Default.Home),
                                    NavigationItem("Explore", Screen.Explore.route, Icons.Default.Explore),
                                    NavigationItem("Community", Screen.Community.route, Icons.Default.Group),
                                    NavigationItem("Profile", Screen.Profile.route, Icons.Default.Person)
                                )
                                items.forEach { item ->
                                    val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                                    NavigationBarItem(
                                        icon = { 
                                            Icon(
                                                item.icon, 
                                                contentDescription = item.title,
                                                modifier = Modifier.animateContentSize()
                                            ) 
                                        },
                                        label = { Text(item.title) },
                                        selected = selected,
                                        onClick = {
                                            if (!selected) {
                                                navController.navigate(item.route) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavGraph(navController = navController)
                    }
                }
            }
        }
    }
}

data class NavigationItem(val title: String, val route: String, val icon: ImageVector)
