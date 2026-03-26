package com.YanandWang.ourandroidproject.ui

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.YanandWang.ourandroidproject.R
/**
 * 此页面作为启动页面
 */
@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        Handler(Looper.getMainLooper()).postDelayed({
            onTimeout()
        }, 2000)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = R.drawable.new_splash_image_shortver,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}