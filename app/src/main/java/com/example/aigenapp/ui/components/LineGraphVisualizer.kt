package com.example.aigenapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun LineGraphVisualizer(
    values: List<Float>,
    history: List<List<Float>>,
    modifier: Modifier = Modifier,
    color: Color = Color.Green,
    alpha: Float = 0.5f
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        val width = size.width
        val height = size.height
        val path = Path()

        // Draw the current values
        if (history.isNotEmpty()) {
            val points = history.mapIndexed { index, point ->
                Offset(
                    x = (index.toFloat() / (history.size - 1)) * width,
                    y = height - (point[0] + 10f) / 20f * height // Normalize from -10 to 10 range
                )
            }

            // Draw line
            if (points.size > 1) {
                path.moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    path.lineTo(points[i].x, points[i].y)
                }
                drawPath(
                    path = path,
                    color = color.copy(alpha = alpha),
                    style = Stroke(
                        width = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )
            }
        }
    }
}
