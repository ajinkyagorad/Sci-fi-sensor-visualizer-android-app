package com.example.aigenapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun CompactVisualizer(
    values: List<Float>,
    history: List<List<Float>>,
    modifier: Modifier = Modifier,
    color: Color = Color.Green
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = color.copy(alpha = 0.5f)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.3f))
            .padding(8.dp)
    ) {
        Canvas(
            modifier = Modifier.size(60.dp)
        ) {
            val width = size.width
            val height = size.height
            val path = Path()

            if (history.isNotEmpty()) {
                val minValue = history.minOf { it[0] }
                val maxValue = history.maxOf { it[0] }
                val range = (maxValue - minValue).coerceAtLeast(0.1f)
                
                val points = history.mapIndexed { index, point ->
                    Offset(
                        x = (index.toFloat() / (history.size - 1)) * width,
                        y = height - ((point[0] - minValue) / range) * height * 0.8f - height * 0.1f
                    )
                }

                if (points.size > 1) {
                    path.moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        path.lineTo(points[i].x, points[i].y)
                    }

                    // Draw glow effect
                    drawPath(
                        path = path,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                color.copy(alpha = 0.7f),
                                color.copy(alpha = 0.3f)
                            )
                        ),
                        style = Stroke(width = 4.dp.toPx())
                    )

                    // Draw main line
                    drawPath(
                        path = path,
                        color = color,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }
    }
}
