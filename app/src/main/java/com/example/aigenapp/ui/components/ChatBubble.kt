package com.example.aigenapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aigenapp.data.ChatMessage

@Composable
fun ChatBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier,
    isUser: Boolean = message.isUser,
    onPlayClick: ((String) -> Unit)? = null
) {
    val sciFiGreen = Color(0xFF00FF00)
    val fontSize = if (message.content.length > 100) 12.sp else 16.sp
    val bubbleColor = if (isUser) Color(0xFF222222) else Color(0xFF111111)
    val alignment = if (isUser) Alignment.End else Alignment.Start
    Row(
        modifier = modifier.padding(vertical = 4.dp, horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isUser) androidx.compose.foundation.layout.Arrangement.End else androidx.compose.foundation.layout.Arrangement.Start
    ) {
        if (!isUser && onPlayClick != null) {
            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = "Play message",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable { onPlayClick(message.content) },
                tint = sciFiGreen
            )
        }
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(bubbleColor)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            color = bubbleColor,
            shadowElevation = 4.dp
        ) {
            Text(
                text = message.content,
                color = sciFiGreen,
                fontFamily = FontFamily.Monospace,
                fontSize = fontSize,
                lineHeight = fontSize * 1.2
            )
        }
        if (isUser && onPlayClick != null) {
            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = "Play message",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable { onPlayClick(message.content) },
                tint = sciFiGreen
            )
        }
    }
}
