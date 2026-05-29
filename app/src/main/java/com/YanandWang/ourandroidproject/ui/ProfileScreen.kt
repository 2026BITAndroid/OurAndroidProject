package com.YanandWang.ourandroidproject.ui

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas as AndroidCanvas
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import coil.compose.rememberAsyncImagePainter
import com.YanandWang.ourandroidproject.R
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
/**
 * 此页面为个人页面
 */
// SharedPreferences 常量
private const val PREFS_NAME = "user_prefs"
private const val KEY_AVATAR_PATH = "avatar_file_path"
private const val KEY_SIGNATURE_PATH = "signature_file_path"

// 签名显示区域尺寸（全局统一，手写板和显示区域完全一致）
private const val SIGNATURE_DISPLAY_HEIGHT_DP = 120

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ✅ 头像改为使用 Uri 状态，兼容 AvatarScreen 风格
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var signatureBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var showSignatureDialog by remember { mutableStateOf(false) }
    var showProjectDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }

    // 从本地存储加载头像和签名
    LaunchedEffect(Unit) {
        // 加载头像（从文件路径转换为 Uri）
        sharedPrefs.getString(KEY_AVATAR_PATH, null)?.let { filePath ->
            val file = File(filePath)
            if (file.exists()) {
                imageUri = Uri.fromFile(file)  // 转换为 Uri 供 Coil 加载
            }
        }
        // 加载签名
        sharedPrefs.getString(KEY_SIGNATURE_PATH, null)?.let { filePath ->
            val file = File(filePath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(filePath)
                signatureBitmap = bitmap.asImageBitmap()
            }
        }
    }

    // ✅ 头像永久保存：复制到应用内部存储，并更新 Uri 状态
    fun saveAvatar(uri: Uri) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val avatarFile = File(context.filesDir, "user_avatar.png")
            FileOutputStream(avatarFile).use { out ->
                inputStream?.copyTo(out)
            }
            sharedPrefs.edit {
                putString(KEY_AVATAR_PATH, avatarFile.absolutePath)
            }
            // 更新 Uri 状态，让界面刷新
            imageUri = Uri.fromFile(avatarFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 保存签名图片到应用内部存储
    fun saveSignatureBitmap(bitmap: Bitmap): String {
        val file = File(context.filesDir, "user_signature.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        sharedPrefs.edit {
            putString(KEY_SIGNATURE_PATH, file.absolutePath)
        }
        return file.absolutePath
    }

    // 本地备份吐槽内容
    fun saveFeedbackLocal(devName: String, content: String) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val feedbackDir = File(context.filesDir, "feedback")
        if (!feedbackDir.exists()) feedbackDir.mkdirs()

        val feedbackFile = File(feedbackDir, "feedback_${devName}_${timestamp}.txt")
        FileOutputStream(feedbackFile).use { out ->
            out.write("时间: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}\n".toByteArray())
            out.write("内容: $content\n".toByteArray())
        }
    }

    // 提交吐槽：EmailSender自己管理后台线程，不会被UI生命周期影响
    fun submitFeedback(devName: String, content: String) {
        // 先保存到本地
        saveFeedbackLocal(devName, content)
        // 调用EmailSender的静态方法发送邮件
        EmailSender.sendFeedbackEmailAsync(devName, content)
        // 立即关闭对话框
        showFeedbackDialog = false
    }

    // ---------- 头像选择逻辑（已替换为 AvatarScreen 风格）----------
    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { saveAvatar(it) }  // 选择后直接保存
    }

    val requestPermissions = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions[Manifest.permission.READ_MEDIA_IMAGES] == true
        } else {
            permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
        }
        if (granted) pickImage.launch("image/*")
    }

    fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 顶部头像区域（使用 Uri 方式加载）
        Spacer(modifier = Modifier.height(32.dp))
        Image(
            painter = rememberAsyncImagePainter(imageUri ?: R.drawable.ic_avatar_default),
            contentDescription = "用户头像",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .clickable {
                    if (checkPermission()) {
                        pickImage.launch("image/*")
                    } else {
                        val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
                        } else {
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                        requestPermissions.launch(perms)
                    }
                },
            contentScale = ContentScale.Crop
        )
        Text(
            text = "点击更换头像",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )

        // 手写签名显示区域
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(SIGNATURE_DISPLAY_HEIGHT_DP.dp)
                .clickable { showSignatureDialog = true },
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                signatureBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap,
                        contentDescription = "手写签名",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentScale = ContentScale.Fit
                    )
                } ?: Text(
                    text = "点击设置手写签名",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 功能列表
        Spacer(modifier = Modifier.height(32.dp))
        ProfileMenuItem(
            icon = Icons.Default.Info,
            title = "项目介绍",
            onClick = { showProjectDialog = true }
        )
        ProfileMenuItem(
            icon = Icons.Default.MailOutline,
            title = "吐槽反馈",
            onClick = { showFeedbackDialog = true }
        )

        // 手写签名编辑对话框（尺寸与显示区域完全一致）
        if (showSignatureDialog) {
            var currentPaths by remember { mutableStateOf(emptyList<List<Offset>>()) }

            AlertDialog(
                onDismissRequest = { showSignatureDialog = false },
                modifier = Modifier.fillMaxWidth(0.95f),
                title = {
                    TopAppBar(
                        title = { Text("手写签名") },
                        actions = {
                            IconButton(onClick = { currentPaths = emptyList() }) {
                                Icon(Icons.Default.Clear, contentDescription = "清除")
                            }
                            IconButton(
                                onClick = {
                                    if (currentPaths.isNotEmpty()) {
                                        val displayMetrics = context.resources.displayMetrics
                                        val screenWidthPx = displayMetrics.widthPixels
                                        val signatureHeightPx = (SIGNATURE_DISPLAY_HEIGHT_DP * displayMetrics.density).toInt()

                                        // 创建与显示区域尺寸完全相同的Bitmap
                                        val bitmap = Bitmap.createBitmap(
                                            screenWidthPx,
                                            signatureHeightPx,
                                            Bitmap.Config.ARGB_8888
                                        )
                                        val canvas = AndroidCanvas(bitmap)
                                        canvas.drawColor(android.graphics.Color.WHITE)
                                        val paint = android.graphics.Paint().apply {
                                            color = android.graphics.Color.BLACK
                                            strokeWidth = 8f
                                            isAntiAlias = true
                                            style = android.graphics.Paint.Style.STROKE
                                            strokeCap = android.graphics.Paint.Cap.ROUND
                                            strokeJoin = android.graphics.Paint.Join.ROUND
                                        }

                                        currentPaths.forEach { path ->
                                            if (path.size >= 2) {
                                                val pathObj = android.graphics.Path()
                                                pathObj.moveTo(path.first().x, path.first().y)
                                                path.drop(1).forEach { point ->
                                                    pathObj.lineTo(point.x, point.y)
                                                }
                                                canvas.drawPath(pathObj, paint)
                                            }
                                        }

                                        saveSignatureBitmap(bitmap)
                                        signatureBitmap = bitmap.asImageBitmap()
                                    }
                                    showSignatureDialog = false
                                }
                            ) {
                                Icon(Icons.Default.Done, contentDescription = "保存")
                            }
                        }
                    )
                },
                text = {
                    SignaturePad(
                        onSignatureChanged = { paths ->
                            currentPaths = paths
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(SIGNATURE_DISPLAY_HEIGHT_DP.dp)
                    )
                },
                confirmButton = {},
                dismissButton = {}
            )
        }

        // 项目介绍对话框
        if (showProjectDialog) {
            AlertDialog(
                onDismissRequest = { showProjectDialog = false },
                title = { Text("项目介绍") },
                text = {
                    Text(
                        "这是由Yan和Wang两位开发者共同开发的app——素造星盒。\n\n" +
                                "希望它能给此刻的你带来一点乐趣，给未来的你带去一些回忆！\n\n" +
                                "在这里，你可以写下你的心情、愿望、想说的话，设定一个时间，到了那天它就会准时送达~\n\n" +
                                "在这里，你可以通过几个小问题“吾日三省吾身”\n\n" +
                                "在这里，你可以监测自己的睡眠，更好地规划自己的时间安排~\n\n" +
                                "还有更多有趣的功能等待你发掘！\n\n" +
                                "感谢你的使用！"
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showProjectDialog = false }) {
                        Text("知道了")
                    }
                }
            )
        }

        // 应用内吐槽反馈对话框
        if (showFeedbackDialog) {
            var selectedDev by remember { mutableStateOf("Yan") }
            var feedbackContent by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showFeedbackDialog = false },
                title = { Text("吐槽反馈") },
                text = {
                    Column {
                        Text(
                            text = "选择要吐槽的开发者:",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TextButton(
                                onClick = { selectedDev = "Yan" },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    "开发者Yan 各种神秘bug的创造者 誓死保卫自己的头发",
                                    color = if (selectedDev == "Yan")
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }
                            TextButton(
                                onClick = { selectedDev = "Wang" },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    "开发者Wang 各种离谱功能设计均来自于现实需求",
                                    color = if (selectedDev == "Wang")
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        OutlinedTextField(
                            value = feedbackContent,
                            onValueChange = { feedbackContent = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            placeholder = { Text("写下你的吐槽...") },
                            maxLines = 5,
                            minLines = 3
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (feedbackContent.isNotBlank()) {
                                submitFeedback(selectedDev, feedbackContent)
                                feedbackContent = ""
                            }
                        },
                        enabled = feedbackContent.isNotBlank()
                    ) {
                        Text("提交")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showFeedbackDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

// 个人中心菜单项组件
@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}