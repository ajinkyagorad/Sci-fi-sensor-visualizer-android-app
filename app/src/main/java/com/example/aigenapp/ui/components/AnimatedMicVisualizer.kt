package com.example.aigenapp.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedMicVisualizer(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "mic-visualizer")
    val amplitude by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "amplitude"
    )
    val bars = 24
    val radius = 24f
    val barLength = 14f
    val barWidth = 4f
    Canvas(modifier = modifier.size(56.dp)) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        for (i in 0 until bars) {
            val angle = (2 * PI * i / bars).toFloat()
            val startX = centerX + radius * cos(angle)
            val startY = centerY + radius * sin(angle)
            val endX = centerX + (radius + barLength * amplitude) * cos(angle)
            val endY = centerY + (radius + barLength * amplitude) * sin(angle)
            drawLine(
                color = Color(0xFF00FF00),
                start = androidx.compose.ui.geometry.Offset(startX, startY),
                end = androidx.compose.ui.geometry.Offset(endX, endY),
                strokeWidth = barWidth,
                cap = StrokeCap.Round
            )
        }
    }
}
