package com.YanandWang.ourandroidproject.ui.luckdraw

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random
import com.YanandWang.ourandroidproject.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuckDrawResultScreen(
    category: String,
    onBack: () -> Unit,
    onReDraw: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var result by remember { mutableStateOf("") }

    // 页面加载时随机抽取结果
    LaunchedEffect(Unit) {
        // 模拟抽签动画效果
        delay(800)
        result = getRandomResult(category)
        isLoading = false
    }

    // 根据类别获取对应的图片资源和主题色
    val (backgroundImage, categoryColor) = when (category) {
        "学业" -> Pair(R.drawable.bg_study, Color(0xFF2196F3))
        "游戏" -> Pair(R.drawable.bg_game, Color(0xFF4CAF50))
        "爱情" -> Pair(R.drawable.bg_love, Color(0xFFE91E63))
        "工作" -> Pair(R.drawable.bg_work, Color(0xFFFF9800))
        "饮食" -> Pair(R.drawable.bg_food, Color(0xFF9C27B0))
        else -> Pair(R.drawable.bg_default, Color(0xFF2196F3))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$category 一签") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                // 加载动画
                Text(
                    text = "正在为你抽取...",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = categoryColor
                )
            } else {
                // 结果卡片
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // 背景图片
                        Image(
                            painter = painterResource(id = backgroundImage),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(24.dp)),
                            contentScale = ContentScale.Crop
                        )

                        // 半透明遮罩，让文字更清晰
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(categoryColor.copy(alpha = 0.7f))
                                .clip(RoundedCornerShape(24.dp))
                        )

                        // 文字内容
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "今日${category}建议",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Text(
                                text = result,
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                // 再抽一次按钮
                Button(
                    onClick = onReDraw,
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("再抽一次", fontSize = 18.sp)
                }
            }
        }
    }
}

/**
 * 根据类别随机获取结果
 * 👉 在这里添加你的签池内容！
 */
fun getRandomResult(category: String): String {
    return when (category) {
        "学业" -> {
            val pool = listOf(
                "今天适合学习数学",
                "今天适合背单词",
                "今天适合写代码",
                "今天适合复习昨天的内容",
                "今天适合预习新知识"
                // 在这里继续添加更多学业建议
            )
            pool.random()
        }
        "游戏" -> {
            val pool = listOf(
                "今天适合玩辅助位",
                "今天适合玩打野位",
                "今天适合单排",
                "今天适合和朋友开黑",
                "今天不适合打排位"
                // 在这里继续添加更多游戏建议
            )
            pool.random()
        }
        "爱情" -> {
            val pool = listOf(
                "今天适合主动发消息",
                "今天适合约对方吃饭",
                "今天适合送小礼物",
                "今天适合安静陪伴",
                "今天适合表达心意"
                // 在这里继续添加更多爱情建议
            )
            pool.random()
        }
        "工作" -> {
            val pool = listOf(
                "今天适合处理邮件",
                "今天适合写报告",
                "今天适合开会讨论",
                "今天适合整理文件",
                "今天适合摸鱼休息"
                // 在这里继续添加更多工作建议
            )
            pool.random()
        }
        "饮食" -> {
            val pool = listOf(
                "今天吃川菜",
                "今天吃火锅",
                "今天吃烧烤",
                "今天吃日料",
                "今天吃家常菜"
                // 在这里继续添加更多饮食建议
            )
            pool.random()
        }
        else -> "今天一切顺利"
    }
}