package com.YanandWang.ourandroidproject.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput

/**
 * 手写签名板组件
 * @param onSignatureChanged 当签名内容变化时回调，返回所有绘制路径
 */
@Composable
fun SignaturePad(
    onSignatureChanged: (List<List<Offset>>) -> Unit,
    modifier: Modifier = Modifier,
    strokeColor: Color = Color.Black,
    strokeWidth: Float = 8f
) {
    // 存储所有绘制的路径（每条路径是一个点的列表）
    var paths by remember { mutableStateOf(emptyList<List<Offset>>()) }
    // 当前正在绘制的路径
    var currentPath by remember { mutableStateOf(emptyList<Offset>()) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        // 开始新路径
                        currentPath = listOf(offset)
                    },
                    onDrag = { change, _ ->
                        // 追加点到当前路径
                        currentPath = currentPath + change.position
                    },
                    onDragEnd = {
                        // 结束当前路径，添加到总路径列表
                        paths = paths + listOf(currentPath)
                        currentPath = emptyList()
                        onSignatureChanged(paths)
                    }
                )
            }
    ) {
        // 绘制所有已完成的路径
        paths.forEach { path ->
            if (path.size >= 2) {
                drawPath(
                    path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(path.first().x, path.first().y)
                        path.drop(1).forEach { point ->
                            lineTo(point.x, point.y)
                        }
                    },
                    color = strokeColor,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }

        // 绘制当前正在绘制的路径
        if (currentPath.size >= 2) {
            drawPath(
                path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(currentPath.first().x, currentPath.first().y)
                    currentPath.drop(1).forEach { point ->
                        lineTo(point.x, point.y)
                    }
                },
                color = strokeColor,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}