package com.YanandWang.ourandroidproject.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random
import com.YanandWang.ourandroidproject.R


private const val PLAYER_RADIUS = 18f
private const val BALL_RADIUS = 12f
private const val CANVAS_WIDTH = 600f
private const val CANVAS_HEIGHT = 800f

data class EnemyBall(
    var pos: Offset,
    var speedX: Float,
    var speedY: Float,
    val color: Color
)

@Composable
fun DodgeBallGameScreen() {
    var playerPos by remember { mutableStateOf(Offset(CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2)) }
    val enemyBalls = remember { mutableStateListOf<EnemyBall>() }
    var score by remember { mutableIntStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }
    var isRunning by remember { mutableStateOf(true) }

    var canvasSize by remember { mutableStateOf(Size.Zero) }

    // 加载背景图片
    val backgroundImage: ImageBitmap = ImageBitmap.imageResource(id = R.drawable.game_bg)

    // 生成敌方小球
    LaunchedEffect(isRunning, gameOver) {
        if (!isRunning || gameOver) return@LaunchedEffect
        while (true) {
            delay(800)
            val side = Random.nextInt(4)
            val startPos = when (side) {
                0 -> Offset(Random.nextFloat() * CANVAS_WIDTH, -BALL_RADIUS)
                1 -> Offset(CANVAS_WIDTH + BALL_RADIUS, Random.nextFloat() * CANVAS_HEIGHT)
                2 -> Offset(Random.nextFloat() * CANVAS_WIDTH, CANVAS_HEIGHT + BALL_RADIUS)
                else -> Offset(-BALL_RADIUS, Random.nextFloat() * CANVAS_HEIGHT)
            }
            val targetX = playerPos.x - startPos.x
            val targetY = playerPos.y - startPos.y
            val speed = Random.nextFloat() * 1.5f + 0.8f
            val dist = sqrt(targetX.pow(2) + targetY.pow(2))
            val dx = targetX / dist * speed
            val dy = targetY / dist * speed
            val randomColor = listOf(
                Color.Red,
                Color.Magenta,
                Color(0xFFFF9800),
                Color.Cyan
            ).random()
            enemyBalls.add(EnemyBall(startPos, dx, dy, randomColor))
        }
    }

    // 游戏主循环
    LaunchedEffect(isRunning, gameOver) {
        if (!isRunning || gameOver) return@LaunchedEffect
        while (true) {
            delay(16)
            score += 1
            enemyBalls.forEach { ball ->
                ball.pos = Offset(ball.pos.x + ball.speedX, ball.pos.y + ball.speedY)
            }
            enemyBalls.removeAll { ball ->
                ball.pos.x < -50 || ball.pos.x > CANVAS_WIDTH + 50 ||
                        ball.pos.y < -50 || ball.pos.y > CANVAS_HEIGHT + 50
            }
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
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "当前存活分数：$score")
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        if (gameOver) return@detectDragGestures
                        val scaleX = CANVAS_WIDTH / canvasSize.width
                        val scaleY = CANVAS_HEIGHT / canvasSize.height
                        val logicDragX = dragAmount.x * scaleX
                        val logicDragY = dragAmount.y * scaleY
                        val newX = (playerPos.x + logicDragX).coerceIn(
                            PLAYER_RADIUS, CANVAS_WIDTH - PLAYER_RADIUS
                        )
                        val newY = (playerPos.y + logicDragY).coerceIn(
                            PLAYER_RADIUS, CANVAS_HEIGHT - PLAYER_RADIUS
                        )
                        playerPos = Offset(newX, newY)
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                canvasSize = size

                // 绘制背景图片
                drawImage(
                    image = backgroundImage,
                    dstSize = androidx.compose.ui.unit.IntSize(size.width.toInt(), size.height.toInt())
                )

                val scaleX = size.width / CANVAS_WIDTH
                val scaleY = size.height / CANVAS_HEIGHT

                drawCircle(
                    color = Color.Green,
                    radius = PLAYER_RADIUS * scaleX,
                    center = Offset(playerPos.x * scaleX, playerPos.y * scaleY)
                )
                enemyBalls.forEach { ball ->
                    drawCircle(
                        color = ball.color,
                        radius = BALL_RADIUS * scaleX,
                        center = Offset(ball.pos.x * scaleX, ball.pos.y * scaleY)
                    )
                }
            }

            if (gameOver) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("游戏结束！", color = Color.Red)
                    Text("最终得分：$score")
                    Button(onClick = {
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