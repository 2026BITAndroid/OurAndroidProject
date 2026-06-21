package com.YanandWang.ourandroidproject.ui.sleep

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.YanandWang.ourandroidproject.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// 添加实验性API注解，抑制警告
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SleepScreen(navController: NavController) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val sleepManager = remember { SleepManager(ctx) }
    val sleepList = remember { mutableStateListOf<SleepRecord>() }
    val weekAvg = remember { mutableStateOf(0.0) }

    LaunchedEffect(Unit) {
        refreshData(sleepManager, sleepList, weekAvg)
        while (true) {
            delay(30000)
            refreshData(sleepManager, sleepList, weekAvg)
        }
    }

    // 外层背景容器
    Box(modifier = Modifier.fillMaxSize()) {
        // 背景图
        Image(
            painter = painterResource(id = R.drawable.sleep_bg),
            contentDescription = "睡眠页面背景",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.9f
        )
        // 半透明遮罩，保证内容清晰
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.2f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 顶部标题+手动刷新按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "睡了么 · 睡眠统计",
                    style = MaterialTheme.typography.headlineSmall
                )
                IconButton(onClick = {
                    scope.launch {
                        refreshData(sleepManager, sleepList, weekAvg)
                    }
                }) {
                    Icon(Icons.Default.Refresh, contentDescription = "刷新数据")
                }
            }

            Text(
                text = "近7天平均睡眠：${String.format("%.1f", weekAvg.value)} 小时",
                modifier = Modifier.padding(bottom = 16.dp, top = 8.dp),
                style = MaterialTheme.typography.titleMedium
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(sleepList.reversed()) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    scope.launch {
                                        // 长按删除单条记录
                                        val allRecords = sleepManager.getAllSleepRecords()
                                        allRecords.removeIf { it.sleepDate == item.sleepDate }
                                        sleepManager.saveList(allRecords)
                                        refreshData(sleepManager, sleepList, weekAvg)
                                    }
                                }
                            ),
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = " 日期：${item.sleepDate}",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = " 入睡：${item.sleepTime}",
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Text(
                                text = " 起床：${item.wakeTime}",
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            Text(
                                text = " 睡眠时长：${String.format("%.1f", item.sleepHour)} 小时",
                                modifier = Modifier.padding(top = 2.dp),
                                color = if (item.sleepHour >= 7.0) {
                                    androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                } else {
                                    androidx.compose.ui.graphics.Color(0xFFFF5722)
                                }
                            )
                            if (item.isOvernight) {
                                Text(
                                    text = " 熬夜了哦",
                                    modifier = Modifier.padding(top = 2.dp),
                                    color = androidx.compose.ui.graphics.Color(0xFFFF9800)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// 数据刷新工具函数
private suspend fun refreshData(
    sleepManager: SleepManager,
    sleepList: SnapshotStateList<SleepRecord>,
    weekAvg: MutableState<Double>
) {
    val newList = sleepManager.getAllSleepRecords()
    sleepList.clear()
    sleepList.addAll(newList)
    weekAvg.value = sleepManager.getAvgWeekSleep()
}