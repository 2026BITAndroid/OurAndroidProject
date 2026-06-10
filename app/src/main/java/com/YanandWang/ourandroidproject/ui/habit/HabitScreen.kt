package com.YanandWang.ourandroidproject.ui.habit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import java.time.LocalDate
import androidx.lifecycle.compose.collectAsStateWithLifecycle
@Composable
fun HabitScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { HabitRepository(context) }
    // 优化：使用collectAsStateWithLifecycle更符合Compose生命周期
    val habits by repository.habitsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val scope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }
    var newHabitName by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "添加习惯")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "习惯打卡",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (habits.isEmpty()) {
                Text(
                    text = "还没有添加任何习惯，点击右下角按钮开始吧！",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn {
                    items(habits) { habit ->
                        HabitCard(
                            habit = habit,
                            onCheckIn = { habitToCheck ->
                                scope.launch {
                                    // 修复：在顶层统一处理打卡逻辑，避免重复创建Repository
                                    val updatedHabit = repository.checkInHabit(habitToCheck)
                                    val newHabits = habits.map { h ->
                                        if (h.id == updatedHabit.id) updatedHabit else h
                                    }
                                    repository.saveHabits(newHabits)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // 添加习惯对话框
    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "添加新习惯", style = MaterialTheme.typography.titleLarge)

                    TextField(
                        value = newHabitName,
                        onValueChange = { newHabitName = it },
                        label = { Text("习惯名称") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "取消",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .clickable { showAddDialog = false }
                                .padding(8.dp)
                        )

                        Text(
                            text = "确定",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable {
                                    if (newHabitName.isNotBlank()) {
                                        val newHabit = Habit(
                                            id = (habits.maxOfOrNull { h -> h.id } ?: 0) + 1,
                                            name = newHabitName
                                        )
                                        scope.launch {
                                            repository.saveHabits(habits + newHabit)
                                            newHabitName = ""
                                            showAddDialog = false
                                        }
                                    }
                                }
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HabitCard(habit: Habit, onCheckIn: (Habit) -> Unit) {
    val today = LocalDate.now()
    val isCheckedInToday = habit.lastCheckInDate == today

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "累计: ${habit.totalDays}天 | 当前连续: ${habit.currentStreak}天 | 最高: ${habit.maxStreak}天",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Card(
                modifier = Modifier.clickable(enabled = !isCheckedInToday) {
                    onCheckIn(habit)
                },
                colors = CardDefaults.cardColors(
                    containerColor = if (isCheckedInToday)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "打卡",
                    modifier = Modifier.padding(12.dp),
                    tint = if (isCheckedInToday)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}