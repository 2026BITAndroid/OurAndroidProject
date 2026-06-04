package com.YanandWang.ourandroidproject.ui.sleep

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.YanandWang.ourandroidproject.ui.sleep.SleepSpHelper
import java.time.LocalDateTime

object SleepTimeCache {
    var lastOff: LocalDateTime? = null
    var firstOn: LocalDateTime? = null
}

class ScreenBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        // 安全?.取action，修复空指针编译报错
        val action = intent?.action ?: return
        val sp = SleepSpHelper(context)
        val now = LocalDateTime.now()
        val splitTime = now.toLocalTime().isBefore(java.time.LocalTime.of(4,0))

        when(action){
            Intent.ACTION_SCREEN_OFF -> {
                if(splitTime){
                    SleepTimeCache.lastOff = now
                    sp.saveLastScreenOff(now)
                }
            }
            Intent.ACTION_SCREEN_ON -> {
                if(!splitTime && SleepTimeCache.firstOn == null){
                    SleepTimeCache.firstOn = now
                    sp.saveFirstScreenOn(now)
                }
            }
        }
    }
}