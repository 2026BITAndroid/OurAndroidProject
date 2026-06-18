package com.YanandWang.ourandroidproject.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

// 玩家小球半径
private const val PLAYER_RADIUS = 18f
// 敌方小球半径
private const val BALL_RADIUS = 12f
// 画布边界
private const val CANVAS_WIDTH = 600f
private const val CANVAS_HEIGHT = 800f

// 敌方小球数据类
data class EnemyBall(
    var pos: Offset,
    var speedX: Float,
    var speedY: Float,
    val color: Color
)

@Composable
fun DodgeBallGameScreen() {
    // 玩家初始坐标（屏幕中心）
    var playerPos by remember { mutableStateOf(Offset(CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2)) }
    // 敌方小球列表
    val enemyBalls = remember { mutableStateListOf<EnemyBall>() }
    // 存活分数（存活1帧+1分）
    var score by remember { mutableIntStateOf(0) }
    // 游戏结束标记
    var gameOver by remember { mutableStateOf(false) }
    // 游戏开关
    var isRunning by remember { mutableStateOf(true) }

    // 生成敌方小球循环
    LaunchedEffect(isRunning, gameOver) {
        if (!isRunning || gameOver) return@LaunchedEffect
        while (true) {
            delay(800)
            // 随机四边生成小球
            val side = Random.nextInt(4)
            val startPos = when (side) {
                0 -> Offset(Random.nextFloat() * CANVAS_WIDTH, -BALL_RADIUS) // 顶部
                1 -> Offset(CANVAS_WIDTH + BALL_RADIUS, Random.nextFloat() * CANVAS_HEIGHT) // 右侧
                2 -> Offset(Random.nextFloat() * CANVAS_WIDTH, CANVAS_HEIGHT + BALL_RADIUS) // 底部
                else -> Offset(-BALL_RADIUS, Random.nextFloat() * CANVAS_HEIGHT) // 左侧
            }
            // 随机朝向玩家方向的速度
            val targetX = playerPos.x - startPos.x
            val targetY = playerPos.y - startPos.y
            val speed = Random.nextFloat() * 1.5f + 0.8f
            val dx = targetX / sqrt(targetX.pow(2) + targetY.pow(2)) * speed
            val dy = targetY / sqrt(targetX.pow(2) + targetY.pow(2)) * speed
            // 修复Orange不存在，使用色值
            val randomColor = listOf(
                Color.Red,
                Color.Magenta,
                Color(0xFFFF9800),
                Color.Cyan
            ).random()
            enemyBalls.add(EnemyBall(startPos, dx, dy, randomColor))
        }
    }

    // 游戏主循环：移动小球+碰撞检测+加分
    LaunchedEffect(isRunning, gameOver) {
        if (!isRunning || gameOver) return@LaunchedEffect
        while (true) {
            delay(16)
            score += 1
            // 更新所有敌方小球位置
            enemyBalls.forEach { ball ->
                ball.pos = Offset(ball.pos.x + ball.speedX, ball.pos.y + ball.speedY)
            }
            // 移除超出画布的小球
            enemyBalls.removeAll { ball ->
                ball.pos.x < -50 || ball.pos.x > CANVAS_WIDTH + 50
                        || ball.pos.y < -50 || ball.pos.y > CANVAS_HEIGHT + 50
            }
            // 碰撞检测：两点距离小于两球半径之和即碰撞
            for (ball in enemyBalls) {
                val distance = sqrt(
                    (playerPos.x - ball.pos.x).pow(2) + (playerPos.y - ball.pos.y).pow(2)
                )
                if (distance < PLAYER_RADIUS + BALL_RADIUS) {
                    gameOver = true
                    break
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "当前存活分数：$score")
        Spacer(modifier = Modifier.height(8.dp))

        // 游戏画布 + 拖拽控制玩家
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((CANVAS_HEIGHT / 3).dp)
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        if (gameOver) return@detectDragGestures
                        // 限制玩家不超出画布
                        val newX = (playerPos.x + dragAmount.x).coerceIn(PLAYER_RADIUS, CANVAS_WIDTH - PLAYER_RADIUS)
                        val newY = (playerPos.y + dragAmount.y).coerceIn(PLAYER_RADIUS, CANVAS_HEIGHT - PLAYER_RADIUS)
                        playerPos = Offset(newX, newY)
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // 绘制玩家（绿色大圆）
                drawCircle(
                    color = Color.Green,
                    radius = PLAYER_RADIUS,
                    center = playerPos
                )
                // 绘制所有敌方小球
                enemyBalls.forEach { ball ->
                    drawCircle(
                        color = ball.color,
                        radius = BALL_RADIUS,
                        center = ball.pos
                    )
                }
            }

            // 游戏结束遮罩弹窗
            if (gameOver) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("游戏结束！", color = Color.Red)
                    Text("最终得分：$score")
                    Button(onClick = {
                        // 重置游戏
                        playerPos = Offset(CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2)
                        enemyBalls.clear()
                        score = 0
                        gameOver = false
                    }) {
                        Text("重新开局")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { isRunning = !isRunning }) {
            Text(if (isRunning) "暂停游戏" else "继续游戏")
        }
    }
}