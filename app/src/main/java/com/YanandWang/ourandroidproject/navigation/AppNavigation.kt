package com.YanandWang.ourandroidproject.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.YanandWang.ourandroidproject.ui.QuoteRepository
import com.YanandWang.ourandroidproject.ui.SplashScreen
import com.YanandWang.ourandroidproject.ui.FeaturesScreen
import com.YanandWang.ourandroidproject.ui.ProfileScreen
import com.YanandWang.ourandroidproject.ui.TimeCapsuleScreen
import com.YanandWang.ourandroidproject.ui.confession.ConfessionScreen
// 新增：事前一签页面导入
import com.YanandWang.ourandroidproject.ui.luckdraw.LuckDrawScreen
import com.YanandWang.ourandroidproject.ui.luckdraw.LuckDrawResultScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in listOf(NavRoutes.FEATURES, NavRoutes.PROFILE)

    // 记住当前显示的名言
    var currentQuote by remember { mutableStateOf("") }

    // 每次切换底部导航页面时刷新名言
    LaunchedEffect(currentDestination?.route) {
        if (showBottomBar) {
            currentQuote = QuoteRepository.getRandomQuote()
        }
    }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 页面内容区域（占满剩余空间）
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                NavHost(
                    navController = navController,
                    startDestination = NavRoutes.SPLASH,
                    modifier = Modifier.fillMaxSize()
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
                            onGoToConfession = { navController.navigate(NavRoutes.CONFESSION) },
                            onGoToConfessionHistory = { navController.navigate(NavRoutes.CONFESSION_HISTORY) },
                            // 新增：事前一签入口
                            onGoToLuckDraw = { navController.navigate(NavRoutes.LUCK_DRAW) }
                        )
                    }

                    composable(NavRoutes.PROFILE) { ProfileScreen() }

                    composable(NavRoutes.TIME_CAPSULE) { TimeCapsuleScreen(navController = navController) }

                    composable(NavRoutes.CONFESSION) {
                        ConfessionScreen(onBack = { navController.popBackStack() })
                    }

                    // 新增：事前一签页面
                    composable(NavRoutes.LUCK_DRAW) {
                        LuckDrawScreen(
                            onBack = { navController.popBackStack() },
                            onSelectCategory = { category ->
                                navController.navigate("luck_draw_result/$category")
                            }
                        )
                    }

                    // 新增：事前一签结果页面
                    composable(NavRoutes.LUCK_DRAW_RESULT) { backStackEntry ->
                        val category = backStackEntry.arguments?.getString("category") ?: "学业"
                        LuckDrawResultScreen(
                            category = category,
                            onBack = { navController.popBackStack() },
                            onReDraw = { navController.navigate("luck_draw_result/$category") {
                                popUpTo(NavRoutes.LUCK_DRAW_RESULT) { inclusive = true }
                            } }
                        )
                    }
                }
            }

            // 名言区域：紧挨着底部导航栏上方
            if (showBottomBar) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text = currentQuote,
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }
    }
}