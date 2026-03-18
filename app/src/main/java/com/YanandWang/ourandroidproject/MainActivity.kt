package com.YanandWang.ourandroidproject

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent

/**
 * 2026.3.14
 * 项目创建
 * 作者：闫俊卓 王希文
 * 希望一切顺利，做出一个好玩的软件
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        var btnGoToSelectAvatar = findViewById<Button>(R.id.btnGoToSelectAvatar)
        btnGoToSelectAvatar.setOnClickListener {
            val intent = Intent(this, AvatarActivity::class.java)
            startActivity(intent)
        }

    }
}