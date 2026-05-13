package com.YanandWang.ourandroidproject.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Features : BottomNavItem(
        route = NavRoutes.FEATURES,
        title = "功能选择",
        icon = Icons.Default.Home
    )

    object Profile : BottomNavItem(
        route = NavRoutes.PROFILE,
        title = "我",
        icon = Icons.Default.Person
    )
}