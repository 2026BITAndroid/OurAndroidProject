package com.YanandWang.ourandroidproject.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment

/**
 * 此页面作为功能选择页面
 */
@Composable
fun FeaturesScreen(
    onGoToTimeCapsule: () -> Unit,
    onGoToConfession: () -> Unit,
    onGoToConfessionHistory: () -> Unit,
    onGoToLuckDraw: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 时间胶囊
        Button(
            onClick = onGoToTimeCapsule,
            modifier = Modifier.padding(8.dp)
        ) {
            Text("时间胶囊")
        }

        // 忏悔录
        Button(
            onClick = onGoToConfession,
            modifier = Modifier.padding(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5A2B))
        ) {
            Text("忏悔录", color = Color.White)
        }

        // 事前一签
        Button(
            onClick = onGoToLuckDraw,
            modifier = Modifier.padding(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
        ) {
            Text("事前一签", color = Color.White)
        }
    }
}