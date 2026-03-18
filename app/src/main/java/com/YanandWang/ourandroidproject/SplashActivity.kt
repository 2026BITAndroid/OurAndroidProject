package com.YanandWang.ourandroidproject
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.OnBackPressedCallback // 必须导入

/**
 * 此页面作为启动页面
 */
class SplashActivity : AppCompatActivity() {
    private val delayTime: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, delayTime)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }
}