package com.YanandWang.ourandroidproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.YanandWang.ourandroidproject.navigation.AppNavigation
import androidx.compose.foundation.layout.fillMaxSize
/**
 * 2026.3.14
 * 项目创建
 * 作者：闫俊卓 王希文
 * 希望一切顺利，做出一个好玩的软件
 *
 * 2026.3.18
 * 项目推进中…接代码不报错，接测试一遍过，希望每天都机魂大悦，orz
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}