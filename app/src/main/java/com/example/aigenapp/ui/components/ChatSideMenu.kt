package com.example.aigenapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.aigenapp.data.ChatSession

@Composable
fun ChatSideMenu(
    sessions: List<ChatSession>,
    currentSessionId: Long,
    onSessionSelected: (Long) -> Unit,
    onCreateSession: () -> Unit,
    onDeleteSession: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(260.dp)
            .background(Color(0xFF181A20)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = "Chats",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
            sessions.forEach { session ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSessionSelected(session.id) }
                        .background(
                            if (session.id == currentSessionId) Color(0xFF23263A) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = session.topic,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { onDeleteSession(session.id) }) {
                        Icon(Icons.Rounded.Delete, contentDescription = "Delete chat", tint = Color.Red)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onCreateSession,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "New chat", tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("New Chat", color = Color.White)
            }
        }
    }
}
