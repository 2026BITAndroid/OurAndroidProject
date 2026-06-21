package com.YanandWang.ourandroidproject.ui.diary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.YanandWang.ourandroidproject.R


private val Emotion.bgColor: Long
    get() = when (this) {
        Emotion.HAPPY -> 0xFFFFF0D6 // 开心-浅橙
        Emotion.CALM -> 0xFFE8F5E9  // 平静-浅绿
        Emotion.SAD -> 0xFFE3F2FD   // 难过-浅蓝
        Emotion.ANGRY -> 0xFFFFEBEE // 生气-浅红
        Emotion.TIRED -> 0xFFEFEBE9 // 疲惫-浅棕
    }

private val Emotion.icon: ImageVector
    get() = when (this) {
        Emotion.HAPPY -> Icons.Filled.SentimentVerySatisfied
        Emotion.CALM -> Icons.Filled.SentimentSatisfied
        Emotion.SAD -> Icons.Filled.SentimentDissatisfied
        Emotion.ANGRY -> Icons.Filled.SentimentVeryDissatisfied
        Emotion.TIRED -> Icons.Filled.SelfImprovement
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { DiaryRepository(context) }
    val scope = rememberCoroutineScope()

    var diaries by remember { mutableStateOf<List<DiaryEntry>>(emptyList()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedEmotion by remember { mutableStateOf(Emotion.CALM) }
    var diaryContent by remember { mutableStateOf("") }

    // 加载日记列表
    LaunchedEffect(Unit) {
        repository.getAllDiaries().collectLatest {
            diaries = it
        }
    }

    // 外层Box：底层放背景图，上层放页面内容
    Box(modifier = Modifier.fillMaxSize()) {
        // 页面背景图
        Image(
            painter = painterResource(id = R.drawable.diary_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("情绪日记本") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White.copy(alpha = 0.85f)
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Text("+", fontSize = 24.sp)
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                if (diaries.isEmpty()) {
                    // 空状态：加半透明白底，保证背景图上文字清晰
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .background(
                                    color = Color.White.copy(alpha = 0.85f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "还没有日记哦",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "点击右下角按钮记录今天的心情",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                } else {
                    // 双列错落瀑布流
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(diaries) { diary ->
                            DiaryCard(
                                modifier = Modifier.padding(6.dp),
                                diary = diary,
                                onDelete = {
                                    scope.launch {
                                        repository.deleteDiary(diary.id)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // 添加日记对话框
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("记录今天的心情") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("选择你的情绪：")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Emotion.values().forEach { emotion ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        color = if (selectedEmotion == emotion)
                                            Color(emotion.color)
                                        else
                                            MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedEmotion = emotion }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = emotion.label,
                                    color = if (selectedEmotion == emotion)
                                        Color.White
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (selectedEmotion == emotion)
                                        FontWeight.Bold
                                    else
                                        FontWeight.Normal
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = diaryContent,
                        onValueChange = { diaryContent = it },
                        label = { Text("写下你的想法...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        maxLines = 8
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (diaryContent.isNotBlank()) {
                            scope.launch {
                                repository.addDiary(selectedEmotion.label, diaryContent)
                                diaryContent = ""
                                showAddDialog = false
                            }
                        }
                    }
                ) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun DiaryCard(
    diary: DiaryEntry,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val emotion = Emotion.values().find { it.label == diary.emotion } ?: Emotion.CALM

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(emotion.bgColor) // 情绪对应柔和背景色
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // 顶部：情绪小图标 + 日期 / 右侧删除按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // 情绪小图标
                    Icon(
                        imageVector = emotion.icon,
                        contentDescription = emotion.label,
                        tint = Color(emotion.color),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = diary.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "删除",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // 情绪标签
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .background(
                        color = Color(emotion.color).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text = emotion.label,
                    color = Color(emotion.color),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // 日记内容
            Text(
                text = diary.content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp),
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}