package com.YanandWang.ourandroidproject.ui.confession

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfessionScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("ConfessionHistory", Context.MODE_PRIVATE)

    // 从题库随机抽3题（不重复）
    val selectedQuestions = remember { ConfessionBank.questions.shuffled().take(3) }
    var currentIndex by remember { mutableStateOf(0) }
    var answers = remember { mutableListOf<String>() }
    var isFinished by remember { mutableStateOf(false) }

    // 控制是否显示历史记录
    var showHistory by remember { mutableStateOf(false) }
    val records = remember {
        sharedPrefs.getString("confession_records", "暂无忏悔记录") ?: "暂无忏悔记录"
    }

    // 木鱼动画（文字版）
    var knockScale by remember { mutableStateOf(1f) }
    val scale by animateFloatAsState(
        targetValue = knockScale,
        animationSpec = tween(durationMillis = 100),
        finishedListener = { knockScale = 1f }
    )

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("忏悔录") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Text("←", fontSize = 20.sp)
                }
            },
            // 右上角：历史/返回按钮
            actions = {
                TextButton(onClick = { showHistory = !showHistory }) {
                    Text(if (showHistory) "返回" else "历史", color = Color.Black)
                }
            }
        )
    }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F0E6))
        ) {
            if (showHistory) {
                // 历史记录界面
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    item {
                        Text(
                            text = "忏悔录历史记录",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = records,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }
            } else {
                if (!isFinished) {
                    // 答题界面
                    val currentQuestion = selectedQuestions[currentIndex]
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        item {
                            Text(
                                text = "问题 ${currentIndex + 1}/3",
                                fontSize = 18.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = currentQuestion.question,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                            currentQuestion.options.forEach { option ->
                                Button(
                                    onClick = {
                                        answers.add(option)
                                        if (currentIndex < 2) {
                                            currentIndex++
                                        } else {
                                            saveConfessionRecord(sharedPrefs, selectedQuestions, answers)
                                            isFinished = true
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5A2B))
                                ) {
                                    Text(text = option, fontSize = 16.sp, color = Color.White)
                                }
                            }
                        }
                    }
                } else {
                    // 答题完成 + 木鱼文字版
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "今日忏悔结束~",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8B5A2B)
                        )
                        Spacer(modifier = Modifier.height(48.dp))

                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .scale(scale)
                                .clickable { knockScale = 0.9f },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🪵", fontSize = 100.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "点击屏幕敲击木鱼",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

// 保存忏悔记录到本地
private fun saveConfessionRecord(
    sharedPrefs: SharedPreferences,
    questions: List<ConfessionQuestion>,
    answers: List<String>
) {
    val editor = sharedPrefs.edit()
    val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    val record = buildString {
        append("时间: $time\n")
        questions.forEachIndexed { index, q ->
            append("问题${index + 1}: ${q.question}\n")
            append("回答: ${answers[index]}\n\n")
        }
    }
    val oldRecords = sharedPrefs.getString("confession_records", "") ?: ""
    val newRecords = "$record\n---\n$oldRecords"
    editor.putString("confession_records", newRecords)
    editor.apply()
}