package com.YanandWang.ourandroidproject.ui.habit

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import com.YanandWang.ourandroidproject.R
@Composable
fun HabitScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { HabitRepository(context) }
    val habits by repository.habitsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
    val scope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }
    var newHabitName by remember { mutableStateOf("") }
    val today = LocalDate.now()

    // 列表排序：今日未打卡置顶高亮，已打卡置灰靠后
    val sortedHabits = remember(habits, today) {
        habits.sortedBy { it.lastCheckInDate == today }
    }

    // 最外层 Box：背景图 + 内容层
    Box(modifier = Modifier.fillMaxSize()) {
        // 背景图片：铺满全屏，居中裁剪
        Image(
            painter = painterResource(id = R.drawable.habit_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 页面主体
        Scaffold(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
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
                        // 顶部 Header：环形进度 + 今日完成率
                        item {
                            val checkedCount = habits.count { it.lastCheckInDate == today }
                            val totalCount = habits.size
                            val progress = if (totalCount == 0) 0f else checkedCount.toFloat() / totalCount

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CircularProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.size(64.dp),
                                    strokeWidth = 6.dp
                                )
                                Column {
                                    Text(
                                        text = "今日打卡完成率",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "${(progress * 100).toInt()}% · $checkedCount / $totalCount",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        items(sortedHabits) { habit ->
                            HabitCard(
                                habit = habit,
                                onCheckIn = { habitToCheck ->
                                    scope.launch {
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
    // 获取本周周一到周日的日期列表
    val weekDates = remember {
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        (0..6).map { monday.plusDays(it.toLong()) }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCheckedInToday)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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

            // 周打卡进度条：7个方块代表周一到周日
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                weekDates.forEach { date ->
                    val isChecked = date in habit.checkInDates
                    val isToday = date == today

                    Surface(
                        modifier = Modifier.size(16.dp),
                        shape = RoundedCornerShape(2.dp),
                        color = if (isChecked) MaterialTheme.colorScheme.primary else Color.Transparent,
                        border = BorderStroke(
                            1.dp,
                            if (isToday) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline
                        )
                    ) {}
                }
            }
        }
    }
}