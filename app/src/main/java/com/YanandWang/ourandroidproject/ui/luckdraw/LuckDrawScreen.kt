package com.YanandWang.ourandroidproject.ui.luckdraw

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.YanandWang.ourandroidproject.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuckDrawScreen(
    onBack: () -> Unit,
    onSelectCategory: (String) -> Unit
) {
    // 抽签类别列表（保留原来的颜色配置，用于按钮颜色）
    val categories = listOf(
        "学业" to Color(0xFF2196F3),
        "游戏" to Color(0xFF4CAF50),
        "爱情" to Color(0xFFE91E63),
        "工作" to Color(0xFFFF9800),
        "饮食" to Color(0xFF9C27B0)
    )

    // 滚轮状态
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = 1000) // 初始位置在中间，实现无限循环
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    // 计算当前选中的索引
    val currentIndex by remember {
        derivedStateOf {
            (listState.firstVisibleItemIndex + 2) % categories.size
        }
    }

    // 选中的类别和颜色
    val selectedCategory = categories[currentIndex].first
    val selectedColor = categories[currentIndex].second

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("事前一签") },
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
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 顶部提示文字
            Text(
                text = "滑动选择抽签方向",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 32.dp)
            )

            // 核心：无限循环滚轮选择器
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // 中间选中指示器
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(140.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {}

                // 滚轮列表
                LazyColumn(
                    state = listState,
                    flingBehavior = snapFlingBehavior,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(count = Int.MAX_VALUE) { index ->
                        val actualIndex = index % categories.size
                        val (category, color) = categories[actualIndex]

                        // 计算当前item距离中心的偏移量
                        val itemOffset = listState.layoutInfo.visibleItemsInfo
                            .find { it.index == index }
                            ?.let {
                                val centerY = listState.layoutInfo.viewportEndOffset / 2
                                val itemCenterY = it.offset + it.size / 2
                                (itemCenterY - centerY).toFloat()
                            } ?: 0f

                        // 根据偏移量计算缩放比例和透明度
                        val maxOffset = 200f // 影响滚轮的"高度"
                        val scale = 1f - (kotlin.math.abs(itemOffset) / maxOffset) * 0.3f
                        val alpha = 1f - (kotlin.math.abs(itemOffset) / maxOffset) * 0.5f

                        // 滚轮项
                        WheelItem(
                            category = category,
                            color = color,
                            scale = scale.coerceIn(0.7f, 1.2f), // 限制缩放范围
                            alpha = alpha.coerceIn(0.4f, 1f),    // 限制透明度范围
                            isSelected = actualIndex == currentIndex
                        )
                    }
                }
            }

            // 底部确认按钮
            Button(
                onClick = { onSelectCategory(selectedCategory) },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(bottom = 48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = selectedColor
                )
            ) {
                Text(
                    text = "开始抽签",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun WheelItem(
    category: String,
    color: Color,
    scale: Float,
    alpha: Float,
    isSelected: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(120.dp)
            .scale(scale)
            .alpha(alpha)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isSelected) 8.dp else 2.dp
            ),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent) // 背景改为透明
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // 背景图片 - 请替换这里的图片资源ID
                // 你可以为每个类别设置不同的图片，或者使用统一的背景图
                val imageResId = when (category) {
                    "学业" -> R.drawable.bg_study // 学业背景图
                    "游戏" -> R.drawable.bg_game  // 游戏背景图
                    "爱情" -> R.drawable.bg_love  // 爱情背景图
                    "工作" -> R.drawable.bg_work  // 工作背景图
                    "饮食" -> R.drawable.bg_food  // 饮食背景图
                    else -> R.drawable.bg_default // 默认背景图
                }

                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)), // 保持圆角
                    contentScale = ContentScale.Crop // 裁剪填充，保持图片比例
                )

                // 可选：添加半透明遮罩，让文字更清晰
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color.copy(alpha = 0.3f)) // 使用原颜色作为半透明遮罩
                        .clip(RoundedCornerShape(16.dp))
                )

                Text(
                    text = category,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}