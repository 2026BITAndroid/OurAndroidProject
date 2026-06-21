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

 */
fun getRandomResult(category: String): String {
    return when (category) {
        "学业" -> {
            val fortunePool = listOf(
                "文曲星微亮",
                "书桌有灵光",
                "笔尖带好运",
                "窗外云卷舒",
                "书页藏答案",
                "思路渐清晰",
                "晨光正好",
                "墨香绕指尖",
                "心静自然明",
                "贵人将相助",
                "灵感在午后",
                "星光不负赶路人"
            )
            val advicePool = listOf(
                "宜起身远眺，放松双眼",
                "宜专注一小时，胜过摸鱼半天",
                "宜整理笔记，温故而知新",
                "宜与同学讨论，碰撞新想法",
                "宜攻克一个小知识点",
                "宜给自己买杯奶茶充能",
                "忌死磕难题，学会暂时放下",
                "忌和别人比进度，按自己节奏来",
                "忌熬夜刷题，保证充足睡眠",
                "忌久坐不动，记得多喝水",
                "忌过度焦虑，允许自己偶尔摆烂",
                "忌三心二意，一次只做一件事"
            )
            "${fortunePool.random()}，${advicePool.random()}"
        }
        "游戏" -> {
            val fortunePool = listOf(
                "手柄沾喜气",
                "网络无卡顿",
                "队友超靠谱",
                "欧气值拉满",
                "手感正火热",
                "逆风也翻盘",
                "宝箱有惊喜",
                "今晚运气佳",
                "技能全命中",
                "快乐值超标",
                "连胜在招手",
                "烦恼全清零"
            )
            val advicePool = listOf(
                "宜叫上朋友开黑",
                "宜玩点轻松的小游戏放松",
                "宜享受过程，看淡输赢",
                "宜玩累了就歇会儿",
                "宜试试新英雄新玩法",
                "宜截图保存精彩瞬间",
                "忌熬夜打游戏",
                "忌因输赢生气上头",
                "忌让游戏变成负担",
                "忌喷队友，保持良好心态",
                "忌沉迷游戏，耽误正事",
                "忌边充电边玩太久手机"
            )
            "${fortunePool.random()}，${advicePool.random()}"
        }
        "爱情" -> {
            val fortunePool = listOf(
                "桃花悄悄开",
                "晚风传心意",
                "心跳会加速",
                "缘分天注定",
                "爱意藏眼底",
                "月老牵红线",
                "今日宜浪漫",
                "思念有回响",
                "相遇皆温柔",
                "双向奔赴时",
                "星光为证",
                "爱意随风起"
            )
            val advicePool = listOf(
                "宜给喜欢的人发个消息",
                "宜分享一件开心的小事",
                "宜给对方一个拥抱",
                "宜给自己买束花",
                "宜和对方一起看场电影",
                "宜好好爱自己",
                "忌胡思乱想，过度猜忌",
                "忌把话憋在心里",
                "忌在感情里太懂事",
                "忌为了别人失去自己",
                "忌回头看，珍惜眼前人",
                "忌急于求成，顺其自然就好"
            )
            "${fortunePool.random()}，${advicePool.random()}"
        }
        "工作" -> {
            val fortunePool = listOf(
                "工位有好运",
                "邮件秒回复",
                "老板不找事",
                "摸鱼不被抓",
                "难题迎刃解",
                "同事超给力",
                "今日宜摸鱼",
                "效率翻倍日",
                "下班不加班",
                "有小惊喜",
                "财运悄悄来",
                "诸事皆顺意"
            )
            val advicePool = listOf(
                "宜按时吃饭，好好照顾自己",
                "宜每隔一小时起身活动",
                "宜整理桌面，清爽心情",
                "宜和同事喝杯咖啡聊聊天",
                "宜早点下班，去做喜欢的事",
                "宜寻求帮助，别硬扛",
                "忌把工作情绪带回家",
                "忌过度苛责自己",
                "忌为了工作牺牲健康",
                "忌和难搞的客户置气",
                "忌拖延症，今日事今日毕",
                "忌久坐不动，保护颈椎腰椎"
            )
            "${fortunePool.random()}，${advicePool.random()}"
        }
        "饮食" -> {
            val fortunePool = listOf(
                "味蕾超灵敏",
                "肠胃很争气",
                "美食在招手",
                "今日有口福",
                "干饭魂觉醒",
                "甜品能治愈",
                "烟火气最抚人心",
                "食欲大开",
                "好运吃出来",
                "人间值得",
                "美味不打烊",
                "快乐干饭日"
            )
            val advicePool = listOf(
                "宜喝一杯甜甜的奶茶",
                "宜奖励自己一顿火锅",
                "宜吃点清淡的，给肠胃放假",
                "宜吃点水果，补充维生素",
                "宜自己做饭，享受烹饪乐趣",
                "宜和朋友一起吃顿大餐",
                "忌不吃早餐",
                "忌晚上吃太多",
                "忌总喝饮料，记得多喝水",
                "忌暴饮暴食",
                "忌吃太辣太冰，刺激肠胃",
                "忌有负罪感，偶尔放纵没关系"
            )
            "${fortunePool.random()}，${advicePool.random()}"
        }
        else -> {
            val fortunePool = listOf(
                "今天天气很好",
                "风很温柔",
                "阳光正好",
                "云很可爱",
                "万事皆宜",
                "好运将至",
                "平安喜乐",
                "顺遂无忧",
                "心想事成",
                "未来可期"
            )
            val advicePool = listOf(
                "宜开心快乐",
                "宜做自己喜欢的事",
                "宜出门走走",
                "宜微笑",
                "宜好好爱自己",
                "忌不开心"
            )
            "${fortunePool.random()}，${advicePool.random()}"
        }
    }

}