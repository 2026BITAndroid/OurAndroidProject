package com.YanandWang.ourandroidproject.ui.sleep

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** 监听屏幕亮/灭广播 */
class ScreenStateReceiver(private val sleepManager: SleepManager) : BroadcastReceiver() {

    companion object {
        // 注册广播（代码动态注册，清单里不能写）
        fun register(ctx: Context, manager: SleepManager): ScreenStateReceiver {
            val receiver = ScreenStateReceiver(manager)
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_OFF)
                addAction(Intent.ACTION_SCREEN_ON)
            }
            ctx.registerReceiver(receiver, filter)
            return receiver
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent ?: return
        when(intent.action) {
            // 息屏 → 记录睡觉
            Intent.ACTION_SCREEN_OFF -> {
                CoroutineScope(Dispatchers.IO).launch {
                    sleepManager.recordSleepOff()
                }
            }
            // 亮屏 → 记录起床
            Intent.ACTION_SCREEN_ON -> {
                CoroutineScope(Dispatchers.IO).launch {
                    sleepManager.recordScreenOn()
                }
            }
        }
    }
}