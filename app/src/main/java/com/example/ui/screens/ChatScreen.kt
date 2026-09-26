package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CampusViewModel
import com.example.ui.components.glassEffect
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: CampusViewModel, chatId: Int, onBack: () -> Unit) {
    val messages by viewModel.currentChatMessages.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val otherUser by viewModel.currentChatOtherUser.collectAsState()
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(chatId) {
        viewModel.loadChatMessages(chatId)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val displayName = otherUser?.name ?: "Student"
    val displayCollege = otherUser?.collegeName ?: "Campus Network"
    val displayMajor = otherUser?.major ?: "Student"

    Scaffold(
        containerColor = BentoBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        // User Avatar with Online Dot Badge
                        Box {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(BentoLilacContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    displayName.firstOrNull()?.toString()?.uppercase() ?: "?",
                                    color = BentoLilacContent,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF22C55E))
                                    .border(2.dp, Color.White, CircleShape)
                                    .align(Alignment.BottomEnd)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = displayName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                    color = BentoTextMain,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "$displayCollege • $displayMajor",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = BentoLavenderContent,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = BentoTextMain
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BentoBackground,
                    titleContentColor = BentoTextMain
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.ime),
                color = BentoBackground
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier
                            .weight(1f)
                            .glassEffect(cornerRadius = 24.dp),
                        placeholder = { Text("Message $displayName...", color = BentoTextSecondary.copy(alpha = 0.6f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = BentoLavenderContent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = BentoTextMain,
                            unfocusedTextColor = BentoTextMain
                        ),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    FloatingActionButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                viewModel.sendMessage(chatId, messageText.trim())
                                messageText = ""
                            }
                        },
                        containerColor = if (messageText.isNotBlank()) BentoLavenderContent else BentoLilacContainer,
                        contentColor = if (messageText.isNotBlank()) Color.White else BentoLilacContent,
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp),
                        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send Message",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (messages.isEmpty()) {
                // Rich Bento Empty State Card
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(BentoLilacContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                displayName.firstOrNull()?.toString()?.uppercase() ?: "?",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 36.sp,
                                color = BentoLilacContent
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Connected with $displayName",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = BentoTextMain,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "$displayName is a student at $displayCollege enrolled in $displayMajor. Break the ice and send a message!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = BentoTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Quick Icebreakers:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = BentoLavenderContent
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        val icebreakers = listOf(
                            "Hey $displayName! 👋",
                            "Are you studying on campus today?",
                            "Need help with $displayMajor notes?",
                            "Let's collaborate on a project!"
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            icebreakers.forEach { icebreaker ->
                                Surface(
                                    onClick = {
                                        viewModel.sendMessage(chatId, icebreaker)
                                    },
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color.White,
                                    border = BorderStroke(1.dp, BentoBorder)
                                ) {
                                    Text(
                                        text = icebreaker,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = BentoLavenderContent,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages) { msg ->
                        val isMe = msg.senderEmail == currentUser?.email
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem(),
                            horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                        ) {
                            if (!isMe) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(BentoLilacContainer)
                                        .align(Alignment.Bottom),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        displayName.firstOrNull()?.toString()?.uppercase() ?: "?",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = BentoLilacContent
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            Card(
                                shape = RoundedCornerShape(
                                    topStart = 20.dp,
                                    topEnd = 20.dp,
                                    bottomStart = if (isMe) 20.dp else 4.dp,
                                    bottomEnd = if (isMe) 4.dp else 20.dp
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isMe) BentoLavenderContent else Color.White
                                ),
                                elevation = CardDefaults.cardElevation(0.dp),
                                modifier = Modifier.widthIn(max = 280.dp)
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                    Text(
                                        text = msg.content,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = if (isMe) Color.White else BentoTextMain,
                                        lineHeight = 20.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = formatMsgTime(msg.timestamp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isMe) Color.White.copy(alpha = 0.7f) else BentoTextSecondary,
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatMsgTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
