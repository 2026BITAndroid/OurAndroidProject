package com.YanandWang.ourandroidproject.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.YanandWang.ourandroidproject.ui.SplashScreen
import com.YanandWang.ourandroidproject.ui.FeaturesScreen
import com.YanandWang.ourandroidproject.ui.ProfileScreen
import com.YanandWang.ourandroidproject.ui.TimeCapsuleScreen
// 新增忏悔录
import com.YanandWang.ourandroidproject.ui.confession.ConfessionScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in listOf(NavRoutes.FEATURES, NavRoutes.PROFILE)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    listOf(BottomNavItem.Features, BottomNavItem.Profile).forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.SPLASH,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoutes.SPLASH) {
                SplashScreen {
                    navController.navigate(NavRoutes.FEATURES) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                }
            }

            composable(NavRoutes.FEATURES) {
                FeaturesScreen(
                    onGoToTimeCapsule = { navController.navigate(NavRoutes.TIME_CAPSULE) },
                    // 新增忏悔录
                    onGoToConfession = { navController.navigate(NavRoutes.CONFESSION) },
                    onGoToConfessionHistory = { navController.navigate(NavRoutes.CONFESSION_HISTORY) }
                )
            }

            composable(NavRoutes.PROFILE) { ProfileScreen() }

            composable(NavRoutes.TIME_CAPSULE) { TimeCapsuleScreen(navController = navController) }

            // 新增忏悔录
            composable(NavRoutes.CONFESSION) {
                ConfessionScreen(onBack = { navController.popBackStack() })
            }

        }
    }
}