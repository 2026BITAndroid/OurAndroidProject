package com.YanandWang.ourandroidproject.ui.sleep

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.YanandWang.ourandroidproject.MainActivity
import com.YanandWang.ourandroidproject.R

class SleepMonitorService : Service() {
    private lateinit var sleepManager: SleepManager
    private lateinit var screenReceiver: ScreenStateReceiver
    private val CHANNEL_ID = "sleep_monitor_channel"
    private val NOTIFICATION_ID = 1001

    override fun onCreate() {
        super.onCreate()
        sleepManager = SleepManager(this)
        screenReceiver = ScreenStateReceiver.register(this, sleepManager)
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenReceiver)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "睡眠监测服务",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "持续监测屏幕状态以记录您的睡眠数据"
                setShowBadge(false)
                enableVibration(false)
                setSound(null, null)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    private fun buildNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )


        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        builder.setContentTitle("睡眠监测中")
        builder.setContentText("正在后台记录您的睡眠数据")
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContentIntent(pendingIntent)
        builder.setPriority(NotificationCompat.PRIORITY_LOW)
        builder.setOngoing(true)
        builder.setSilent(true)

        return builder.build()
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SleepMonitorService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, SleepMonitorService::class.java)
            context.stopService(intent)
        }
    }
}