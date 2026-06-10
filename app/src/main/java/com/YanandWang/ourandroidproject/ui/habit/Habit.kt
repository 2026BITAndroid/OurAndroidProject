package com.YanandWang.ourandroidproject.ui.habit

import java.time.LocalDate

data class Habit(
    val id: Int,
    val name: String,
    val totalDays: Int = 0,
    val currentStreak: Int = 0,
    val maxStreak: Int = 0,
    val lastCheckInDate: LocalDate? = null
)