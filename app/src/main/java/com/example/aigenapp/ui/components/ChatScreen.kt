package com.example.aigenapp.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp


import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.aigenapp.data.ChatMessage
import com.example.aigenapp.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier,
    onMicClick: () -> Unit,
    onPlayTTS: (String) -> Unit = {},
    isRecording: Boolean = false,
    isTranscribing: Boolean = false,
    inputText: String = "",
    onInputTextChange: (String) -> Unit = {},
    onMicResult: (String) -> Unit = {},
    onSendMessage: () -> Unit = {}
) {
    val messages by viewModel.messages.collectAsState()
    val isGeneratingImage by viewModel.isGeneratingImage.collectAsState()
    // inputText is now managed by the parent component
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll to the latest message when messages change
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            state = listState,
            reverseLayout = false
        ) {
            items(messages) { message ->
                ChatBubble(
                    message = message,
                    modifier = Modifier.padding(vertical = 4.dp),
                    isUser = message.isUser,
                    onPlayClick = { content -> onPlayTTS(content) }
                )
            }
        }

        // Input area
        // Input area moves up with the keyboard
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0x33FFFFFF))
                .padding(8.dp)
                .imePadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Animated circular mic spectrum visualizer
                if (isRecording) {
                    AnimatedMicVisualizer()
                }
                IconButton(onClick = onMicClick) {
                    Icon(
                        Icons.Rounded.Mic,
                        contentDescription = if (isRecording) "Stop recording" else "Voice input",
                        tint = if (isRecording) Color.Green else Color.Cyan
                    )
                }
            }

            TextField(
                value = inputText,
                onValueChange = { onInputTextChange(it) },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .background(Color(0xFF222244), RoundedCornerShape(24.dp)),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFF222244),
                    focusedContainerColor = Color(0xFF333366),
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White
                ),
                placeholder = {
                    Text("Type a message...", color = Color.Gray)
                }
            )

            IconButton(
                onClick = {
                    if (inputText.startsWith("/image")) {
                        viewModel.generateImage(inputText.removePrefix("/image").trim())
                    } else {
                        viewModel.sendMessage(inputText)
                    }
                    onSendMessage()
                    coroutineScope.launch {
                        if (messages.isNotEmpty()) {
                            listState.animateScrollToItem(messages.lastIndex)
                        }
                    }
                },
                enabled = inputText.isNotEmpty()
            ) {
                Icon(
                    if (inputText.startsWith("/image")) Icons.Rounded.Image else Icons.Rounded.Send,
                    contentDescription = "Send",
                    tint = if (inputText.isNotEmpty()) Color.Cyan else Color.Gray
                )
            }
        }

        if (isGeneratingImage) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp),
                color = Color.Cyan
            )
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (message.isUser) Color(0xFF2196F3) else Color(0xFF424242)
    val textColor = Color.White

    val formatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val timeString = remember(message.timestamp) { formatter.format(message.timestamp) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (message.isUser) 20.dp else 4.dp,
                        bottomEnd = if (message.isUser) 4.dp else 20.dp
                    )
                )
                .background(bubbleColor.copy(alpha = 0.7f))
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = message.content,
                    color = textColor,
                    fontSize = 16.sp
                )
                
                message.imageUrl?.let { url ->
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = url,
                        contentDescription = "Generated image",
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        
        Text(
            text = timeString,
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
    }
}
