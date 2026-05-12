package com.YanandWang.ourandroidproject.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.YanandWang.ourandroidproject.ui.SplashScreen
import com.YanandWang.ourandroidproject.ui.MainScreen
import com.YanandWang.ourandroidproject.ui.AvatarScreen
import com.YanandWang.ourandroidproject.ui.TimeCapsuleScreen
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

        composable(route = "main") {
            MainScreen(
                onGoToAvatar = { navController.navigate(route = "avatar") },
                onGoToTimeCapsule = { navController.navigate(route = "time_capsule") }
            )
        }

        composable("avatar") {
            AvatarScreen()
        }
        composable("time_capsule") {
            TimeCapsuleScreen(navController = navController)
        }
        composable("main") {
            MainScreen(
                onGoToAvatar = { navController.navigate("avatar") },
                onGoToTimeCapsule = { navController.navigate("time_capsule") }  // 新增
            )
        }
    }
}