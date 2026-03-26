package com.YanandWang.ourandroidproject.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.YanandWang.ourandroidproject.ui.SplashScreen
import com.YanandWang.ourandroidproject.ui.MainScreen
import com.YanandWang.ourandroidproject.ui.AvatarScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen {
                navController.navigate("main") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }

        composable("main") {
            MainScreen {
                navController.navigate("avatar")
            }
        }

        composable("avatar") {
            AvatarScreen()
        }
    }
}