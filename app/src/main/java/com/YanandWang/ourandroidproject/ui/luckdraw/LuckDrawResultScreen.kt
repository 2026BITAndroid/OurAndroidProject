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
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 40.sp
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
                "学累了就站起来走走，看看窗外的云",
                "今天的你超棒的，已经完成了很多事",
                "不用逼自己学太久，专注一小时胜过摸鱼半天",
                "遇到难题别死磕，先放一放，答案会自己来找你",
                "今天适合整理笔记，温故而知新",
                "给自己买杯奶茶再学习，效率会翻倍哦",
                "别和别人比进度，按自己的节奏来就好",
                "今天适合攻克一个小知识点，成就感满满",
                "记得多喝水，大脑缺水会变笨的",
                "学不进去的时候就去睡一觉，醒来会好很多",
                "今天适合和同学讨论问题，会有新启发",
                "允许自己偶尔摆烂，休息是为了走更远的路"
                // 在这里继续添加更多学业建议
            )
            pool.random()
        }
        "游戏" -> {
            val pool = listOf(
                "今天玩游戏别生气，开心最重要",
                "别熬夜打游戏，明天还要早起呢",
                "今天适合玩点轻松的小游戏放松一下",
                "玩累了就歇会儿，别让游戏变成负担",
                "享受游戏过程，输赢都是体验",
                "叫上朋友一起玩会吧"
                // 在这里继续添加更多游戏建议
            )
            pool.random()
        }
        "爱情" -> {
            val pool = listOf(
                "爱别人之前，先好好爱自己",
                "想念的话就发个消息吧，没什么不好意思的",
                "今天适合给喜欢的人点一杯奶茶",
                "感情里不用太懂事，偶尔撒娇也很可爱",
                "顺其自然就好，是你的总会来的",
                "今天适合和对方分享一件开心的小事",
                "别想太多，他/她其实很在乎你",
                "一个人也可以过得很精彩",
                "今天适合给对方一个拥抱，胜过千言万语",
                "有误会就说清楚，别憋在心里",
                "珍惜眼前人，不要等到失去才后悔",
                "今天适合给自己买束花，浪漫不分对象"
                // 在这里继续添加更多爱情建议
            )
            pool.random()
        }
        "工作" -> {
            val pool = listOf(
                "再忙也要记得按时吃饭",
                "今天的工作已经很棒了，别太苛责自己",
                "摸鱼也是工作的一部分，别太有负罪感",
                "遇到难搞的客户别生气，气坏身体不值得",
                "今天适合早点下班，去做自己喜欢的事",
                "记得每隔一小时站起来活动一下",
                "工作是做不完的，身体才是第一位的",
                "今天适合和同事喝杯咖啡，聊聊天",
                "别把工作情绪带回家，家里有温暖等你",
                "今天适合整理桌面，心情也会变清爽",
                "遇到困难别硬扛，寻求帮助不是软弱",
                "今天会有小惊喜等着你哦"
                // 在这里继续添加更多工作建议
            )
            pool.random()
        }
        "饮食" -> {
            val pool = listOf(
                "今天适合喝一杯甜甜的奶茶",
                "奖励自己一顿火锅吧，所有烦恼都能涮掉",
                "今天吃点清淡的，给肠胃放个假",
                "记得吃早餐，不然会胃疼的",
                "今天适合吃点甜品，心情会变好",
                "去尝尝那家新开的店吧，说不定有惊喜",
                "多喝水，别总喝饮料",
                "今天适合和朋友一起吃顿大餐",
                "吃点水果吧，补充维生素",
                "今天适合自己做饭，享受烹饪的乐趣",
                "偶尔吃顿垃圾食品也没关系，开心最重要",
                "晚上别吃太多，不然会睡不着的"
                // 在这里继续添加更多饮食建议
            )
            pool.random()
        }
        else -> "今天也是美好的一天，要开心哦"
    }
}