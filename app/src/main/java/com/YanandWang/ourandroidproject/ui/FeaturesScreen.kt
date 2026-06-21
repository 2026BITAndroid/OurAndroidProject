package com.YanandWang.ourandroidproject.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.YanandWang.ourandroidproject.navigation.NavRoutes
import com.YanandWang.ourandroidproject.R
import androidx.compose.ui.unit.sp

/**
 * 此页面作为功能选择页面
 * 风铃式竖排吊落布局
 */
@Composable
fun FeaturesScreen(
    navController: NavController,
    onGoToTimeCapsule: () -> Unit,
    onGoToConfession: () -> Unit,
    onGoToConfessionHistory: () -> Unit,
    onGoToLuckDraw: () -> Unit
) {
    val ropeHeights = listOf(140.dp, 100.dp, 170.dp, 120.dp, 150.dp, 110.dp, 130.dp)
    val ropeColor = Color(0xFF8B5A2B)

    Box(modifier = Modifier.fillMaxSize()) {
        // 背景图片
        Image(
            painter = painterResource(id = R.drawable.bg_features),
            contentDescription = "页面背景",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.9f
        )

        // 风铃主体：7项平分宽度，全部从顶部吊落
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top
        ) {
            // 1. 时间胶囊
            WindBellItem(
                modifier = Modifier.weight(1f),
                ropeHeight = ropeHeights[0],
                ropeColor = ropeColor,
                onClick = onGoToTimeCapsule,
                text = "时间胶囊",
                buttonColors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                imageRes = R.drawable.bg_time_capsule,
                textColor = Color.White,
                brightness = 0.7f,
                imageAlpha = 0.9f
            )

            // 2. 忏悔录
            WindBellItem(
                modifier = Modifier.weight(1f),
                ropeHeight = ropeHeights[1],
                ropeColor = ropeColor,
                onClick = onGoToConfession,
                text = "忏悔录",
                buttonColors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                textColor = Color.White,
                imageRes = R.drawable.confession_bg,
                brightness = 0.7f,
                imageAlpha = 0.9f
            )

            // 3. 事前一签
            WindBellItem(
                modifier = Modifier.weight(1f),
                ropeHeight = ropeHeights[2],
                ropeColor = ropeColor,
                onClick = onGoToLuckDraw,
                text = "事前一签",
                buttonColors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                textColor = Color.White,
                imageRes = R.drawable.bg_love,
                brightness = 0.7f,
                imageAlpha = 0.9f
            )

            // 4. 睡了么
            WindBellItem(
                modifier = Modifier.weight(1f),
                ropeHeight = ropeHeights[3],
                ropeColor = ropeColor,
                onClick = { navController.navigate(NavRoutes.SleepPage) },
                text = "睡了么",
                buttonColors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                imageRes = R.drawable.sleep_bg,
                textColor = Color.White,
                brightness = 0.7f,
                imageAlpha = 0.9f
            )

            // 5. 习惯打卡
            WindBellItem(
                modifier = Modifier.weight(1f),
                ropeHeight = ropeHeights[4],
                ropeColor = ropeColor,
                onClick = { navController.navigate(NavRoutes.HABIT) },
                text = "习惯打卡",
                buttonColors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                imageRes = R.drawable.habit_bg,
                textColor = Color.White,
                brightness = 0.7f,
                imageAlpha = 0.9f
            )

            // 6. 情绪日记
            WindBellItem(
                modifier = Modifier.weight(1f),
                ropeHeight = ropeHeights[5],
                ropeColor = ropeColor,
                onClick = { navController.navigate(NavRoutes.DIARY) },
                text = "情绪日记",
                buttonColors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                textColor = Color.White,
                imageRes = R.drawable.diary_bg,
                brightness = 0.7f,
                imageAlpha = 0.9f
            )

            // 7. 摸鱼小游戏
            WindBellItem(
                modifier = Modifier.weight(1f),
                ropeHeight = ropeHeights[6],
                ropeColor = ropeColor,
                onClick = { navController.navigate("dodgeBall") },
                text = "摸鱼小游戏",
                buttonColors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                imageRes = R.drawable.game_bg,
                textColor = Color.White,
                brightness = 0.7f,
                imageAlpha = 0.9f
            )
        }
    }
}

/**
 * 单个风铃组件：上方吊绳 + 下方竖排窄按钮（带背景图，按钮原生尺寸完全保留）
 */
@Composable
private fun WindBellItem(
    modifier: Modifier = Modifier,
    ropeHeight: androidx.compose.ui.unit.Dp,
    ropeColor: Color,
    onClick: () -> Unit,
    text: String,
    buttonColors: androidx.compose.material3.ButtonColors,
    textColor: Color = Color.White,
    // 按钮背景图片资源ID
    imageRes: Int,
    imageAlpha: Float = 0.8f,
    brightness: Float = 1f
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 吊绳
        Box(
            modifier = Modifier
                .width(1.2.dp)
                .height(ropeHeight)
                .background(ropeColor)
        )

        // 外层容器
        Box(modifier = Modifier.padding(top = 4.dp)) {
            // 背景图
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop,
                alpha = imageAlpha,
                // 明度调节滤镜：通过颜色矩阵缩放RGB通道
                colorFilter = ColorFilter.colorMatrix(
                    ColorMatrix().apply {
                        setToScale(brightness, brightness, brightness, 1f)
                    }
                )
            )

            Button(
                onClick = onClick,
                modifier = Modifier
                    .defaultMinSize(1.dp, 1.dp)
                    .widthIn(min = 48.dp),
                colors = buttonColors,
                // 恢复原始内边距，文字位置与原版完全一致
                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 8.dp)
            ) {
                VerticalText(text = text, color = textColor)
            }
        }
    }
}

/**
 * 竖排文字：字符纵向排列
 */
@Composable
private fun VerticalText(
    text: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        text.forEach { char ->
            Text(
                text = char.toString(),
                color = color,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                ),
                fontSize = 15.sp
            )
        }
    }
}