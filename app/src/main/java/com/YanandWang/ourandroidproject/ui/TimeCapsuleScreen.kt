package com.YanandWang.ourandroidproject.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import androidx.work.Data
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.ui.Alignment
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
/**
 * 此页面为时间胶囊实体
 */
// 时间胶囊实体
data class TimeCapsule(
    val id: String = UUID.randomUUID().toString(),
    val content: String = "",
    val passwordHash: String = "",
    val unlockTime: LocalDateTime = LocalDateTime.now(),
    val isUnlocked: Boolean = false
)

// 加密存储仓库
class TimeCapsuleRepository(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "time_capsule",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val gson = Gson()
        .newBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, object : com.google.gson.TypeAdapter<LocalDateTime>() {
            private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

            override fun write(out: com.google.gson.stream.JsonWriter, value: LocalDateTime?) {
                out.value(value?.format(formatter))
            }

            override fun read(`in`: com.google.gson.stream.JsonReader): LocalDateTime? {
                return LocalDateTime.parse(`in`.nextString(), formatter)
            }
        })
        .create()

    fun save(capsule: TimeCapsule) {
        val map = getAllCapsulesMap().toMutableMap()
        map[capsule.id] = capsule
        prefs.edit()
            .putString("list", gson.toJson(map))
            .apply()
    }

    fun getAll(): List<TimeCapsule> = getAllCapsulesMap().values.toList()
    fun getById(id: String): TimeCapsule? = getAllCapsulesMap()[id]

    private fun getAllCapsulesMap(): Map<String, TimeCapsule> {
        val json = prefs.getString("list", null) ?: return emptyMap()
        val type = object : TypeToken<Map<String, TimeCapsule>>() {}.type
        return gson.fromJson(json, type) ?: emptyMap()
    }
}

// 定时解锁任务
class UnlockWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val repo = TimeCapsuleRepository(applicationContext)
        val id = inputData.getString("id") ?: return Result.failure()
        val capsule = repo.getById(id) ?: return Result.failure()

        if (LocalDateTime.now().isAfter(capsule.unlockTime)) {
            repo.save(capsule.copy(isUnlocked = true))
        }
        return Result.success()
    }
}

// 调度工具
object CapsuleScheduler {
    fun schedule(context: Context, capsuleId: String, unlockTime: LocalDateTime) {
        val now = LocalDateTime.now()
        val delay = Duration.between(now, unlockTime)
        if (delay.isNegative) return

        val request = OneTimeWorkRequestBuilder<UnlockWorker>()
            .setInitialDelay(delay)
            .setInputData(Data.Builder().putString("id", capsuleId).build())
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }
}

// 加密工具
fun sha256(str: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hash = digest.digest(str.toByteArray(StandardCharsets.UTF_8))
    return hash.joinToString("") { "%02x".format(it) }
}

// -------------------------- 主页面 --------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeCapsuleScreen(navController: NavController) {
    val context = LocalContext.current
    val repo = remember { TimeCapsuleRepository(context) }
    var list by remember { mutableStateOf(repo.getAll()) }
    var showCreate by remember { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("时间胶囊") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Button(
                onClick = { showCreate = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("创建新胶囊")
            }

            Spacer(Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(list) { capsule ->
                    CapsuleItem(
                        capsule = capsule,
                        formatter = formatter,
                        onUnlockByPassword = { password ->
                            if (sha256(password) == capsule.passwordHash) {
                                repo.save(capsule.copy(isUnlocked = true))
                                list = repo.getAll()
                            }
                        }
                    )
                }
            }
        }
    }

    if (showCreate) {
        CreateCapsuleDialog(
            onDismiss = { showCreate = false },
            onSave = { content, password, time ->
                val newCapsule = TimeCapsule(
                    content = content,
                    passwordHash = sha256(password),
                    unlockTime = time
                )
                repo.save(newCapsule)
                CapsuleScheduler.schedule(context, newCapsule.id, time)
                list = repo.getAll()
                showCreate = false
            }
        )
    }
}

// 单个胶囊卡片
@Composable
fun CapsuleItem(
    capsule: TimeCapsule,
    formatter: DateTimeFormatter,
    onUnlockByPassword: (String) -> Unit
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("解锁时间：${capsule.unlockTime.format(formatter)}")
            Spacer(Modifier.height(8.dp))

            if (capsule.isUnlocked || LocalDateTime.now().isAfter(capsule.unlockTime)) {
                Text("内容：${capsule.content}", style = MaterialTheme.typography.bodyLarge)
            } else {
                Text("🔒 已封存", color = MaterialTheme.colorScheme.error)

                var password by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("输入密码解锁") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { onUnlockByPassword(password) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("立即解锁")
                }
            }
        }
    }
}

// 创建弹窗
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCapsuleDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, LocalDateTime) -> Unit
) {
    var content by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                if (content.isBlank() || password.isBlank()) return@Button
                val unlockTime = LocalDateTime.of(selectedDate, selectedTime)
                onSave(content, password, unlockTime)
            }) {
                Text("封存胶囊")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
        text = {
            Column(Modifier.fillMaxWidth()) {
                // 内容输入
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("写下你的话") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )

                Spacer(Modifier.height(8.dp))

                // 密码输入
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("设置解锁密码") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                // 日期选择按钮
                Button(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("选择解锁日期：$selectedDate")
                }

                Spacer(Modifier.height(8.dp))

                // 时间选择按钮
                Button(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("选择解锁时间：${selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"))}")
                }
            }
        }
    )

    // 日期选择器
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // 时间选择器
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedTime.hour,
            initialMinute = selectedTime.minute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("选择时间") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("取消")
                }
            }
        )
    }
}