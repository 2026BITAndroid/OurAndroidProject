package com.YanandWang.ourandroidproject.ui.sleep

import android.content.Context
import android.content.SharedPreferences
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SleepSpHelper(ctx: Context) {
    private val sp: SharedPreferences = ctx.getSharedPreferences("sleep_cache", Context.MODE_PRIVATE)
    private val fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun saveLastScreenOff(time: LocalDateTime?) {
        sp.edit().putString("off", time?.format(fmt)).apply()
    }

    fun saveFirstScreenOn(time: LocalDateTime?) {
        sp.edit().putString("on", time?.format(fmt)).apply()
    }

    fun getLastScreenOff(): LocalDateTime? {
        val str = sp.getString("off", null) ?: return null
        return LocalDateTime.parse(str, fmt)
    }

    fun getFirstScreenOn(): LocalDateTime? {
        val str = sp.getString("on", null) ?: return null
        return LocalDateTime.parse(str, fmt)
    }

    fun clearCache() {
        sp.edit().clear().apply()
    }
}