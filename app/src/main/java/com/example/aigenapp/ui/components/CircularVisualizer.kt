package com.example.aigenapp.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CircularVisualizer(
    sensorValues: List<Float>,
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(
        Color(0xFF00FF00),
        Color(0xFF00FFFF),
        Color(0xFFFF00FF),
        Color(0xFFFFFF00)
    )
) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(
        modifier = modifier.size(200.dp)
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val maxRadius = size.width.coerceAtMost(size.height) / 2

        sensorValues.forEachIndexed { index, value ->
            val normalizedValue = (value.coerceIn(-10f, 10f) + 10f) / 20f
            val radius = maxRadius * (0.4f + (index * 0.15f))
            
            drawCircle(
                color = colors[index % colors.size],
                center = center,
                radius = radius,
                style = Stroke(width = 2f)
            )

            // Draw data points
            for (i in 0..36) {
                val angle = Math.toRadians((i * 10 + rotation).toDouble())
                val x = center.x + radius * cos(angle).toFloat()
                val y = center.y + radius * sin(angle).toFloat()
                
                drawCircle(
                    color = colors[index % colors.size],
                    center = Offset(x, y),
                    radius = 2f * normalizedValue
                )
            }
        }
    }
}
