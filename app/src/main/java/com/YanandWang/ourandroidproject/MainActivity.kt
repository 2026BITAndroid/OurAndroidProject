package com.YanandWang.ourandroidproject

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import com.YanandWang.ourandroidproject.navigation.AppNavigation
import com.YanandWang.ourandroidproject.ui.sleep.SleepMonitorService

/**
 * 2026.3.14
 * 项目创建
 * 作者：闫俊卓 王希文
 * 希望一切顺利，做出一个好玩的软件
 *
 * 2026.3.18
 * 项目推进中…接代码不报错，接测试一遍过，希望每天都机魂大悦，orz
 *
 * 2026.6.5
 * 优化：睡眠功能改为前台服务实现，解决Android 14+后台被杀问题
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 检查并启动睡眠监测前台服务
        checkAndStartSleepService()

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }

    // 检查权限并启动睡眠服务
    private fun checkAndStartSleepService() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this) -> {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION)
            }
            else -> {
                SleepMonitorService.start(this)
            }
        }
    }

    // 处理权限请求结果
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                SleepMonitorService.start(this)
            }
        }
    }

    companion object {
        private const val REQUEST_OVERLAY_PERMISSION = 1001
    }
}