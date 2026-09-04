package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FeedPost
import com.example.data.model.FriendSuggestion
import com.example.data.model.HomeFeedItem
import com.example.ui.CampusViewModel
import com.example.ui.components.FriendSuggestionCarousel
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: CampusViewModel,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onStartChat: (String) -> Unit = {}
) {
    val user by viewModel.currentUser.collectAsState()
    val posts by viewModel.feedPosts.collectAsState()

    var postContent by remember { mutableStateOf("") }
    var isPostingBoxExpanded by remember { mutableStateOf(false) }

    val mockSuggestions = remember {
        mutableStateListOf(
            FriendSuggestion("1", "Alex Rivers", "3 mutual friends", "A"),
            FriendSuggestion("2", "Sam Knight", "Nearby", "S"),
            FriendSuggestion("3", "Jordan Lee", "In your major", "J"),
            FriendSuggestion("4", "Casey Brown", "Mutual group", "C")
        )
    }

    val feedItems = remember(posts, mockSuggestions.size) {
        val items = mutableListOf<HomeFeedItem>()
        posts.forEachIndexed { index, post ->
            items.add(HomeFeedItem.Post(post))
            if (index == 1 && mockSuggestions.isNotEmpty()) {
                items.add(HomeFeedItem.Suggestions(mockSuggestions.toList()))
            }
        }
        if (posts.size <= 1 && mockSuggestions.isNotEmpty()) {
            items.add(HomeFeedItem.Suggestions(mockSuggestions.toList()))
        }
        items
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF020617), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.campusdeck_logo),
                                contentDescription = "Logo",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(
                                text = "Campusdeck",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp),
                                color = BentoTextMain
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color(0xFF22C55E), shape = CircleShape)
                                )
                                Text(
                                    text = (user?.collegeName ?: "Institutional Network").uppercase(),
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                                    color = BentoTextSecondary
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Profile initials avatar badge matching bento layout header
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(BentoLavenderContainer)
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { onProfileClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user?.name?.firstOrNull()?.toString()?.uppercase() ?: "?",
                            color = BentoLavenderContent,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BentoBackground,
                    titleContentColor = BentoTextMain
                )
            )
        },
        containerColor = BentoBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Write Post Section - Glass Styled
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                cornerRadius = 24.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (!isPostingBoxExpanded) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(BentoLavenderContent)
                                    .clickable { onProfileClick() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user?.name?.firstOrNull()?.toString()?.uppercase() ?: "?",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = { isPostingBoxExpanded = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                            ) {
                                Text(
                                    text = "Share an update or ask a question...",
                                    color = BentoTextSecondary,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Start,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = postContent,
                            onValueChange = { postContent = it },
                            placeholder = { Text("What's happening on campus?", color = BentoTextSecondary.copy(alpha = 0.6f)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .testTag("home_post_input"),
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = BentoLavenderContent,
                                unfocusedTextColor = BentoLavenderContent,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = BentoLavenderContent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    isPostingBoxExpanded = false
                                    postContent = ""
                                }
                            ) {
                                Text("Cancel", color = BentoLavenderContent, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (postContent.isNotBlank()) {
                                        viewModel.createPost(postContent)
                                        postContent = ""
                                        isPostingBoxExpanded = false
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BentoLavenderContent,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.testTag("submit_post_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Post", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Scrollable list of posts
            if (posts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(BentoLilacContainer, shape = RoundedCornerShape(24.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Campaign,
                                contentDescription = "Empty Bulletin",
                                tint = BentoLilacContent,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Bulletin Posts Yet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = BentoTextMain
                        )
                        Text(
                            text = "Be the first to share an update, organize an event, or greet your peers!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = BentoTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(feedItems) { item ->
                        when (item) {
                            is HomeFeedItem.Post -> {
                                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                    PostItem(
                                        post = item.post,
                                        currentUserEmail = user?.email ?: "",
                                        onLikeToggle = { viewModel.toggleLikePost(item.post) },
                                        onMessageClick = { onStartChat(item.post.authorEmail) }
                                    )
                                }
                            }
                            is HomeFeedItem.Suggestions -> {
                                FriendSuggestionCarousel(
                                    suggestions = item.suggestions,
                                    onConnect = { suggestion ->
                                        // Connect logic
                                        mockSuggestions.remove(suggestion)
                                    },
                                    onDismissSuggestion = { suggestion ->
                                        mockSuggestions.remove(suggestion)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostItem(
    post: FeedPost,
    currentUserEmail: String,
    onLikeToggle: () -> Unit,
    onMessageClick: () -> Unit
) {
    val hasLiked = post.likedByEmails.split(",").map { it.trim() }.contains(currentUserEmail)

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("post_item_${post.id}"),
        cornerRadius = 24.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Author Initials Avatar with Lilac Theme
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(BentoLilacContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.authorName.firstOrNull()?.toString()?.uppercase() ?: "?",
                        color = BentoLilacContent,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.authorName,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = BentoTextMain
                    )
                    Text(
                        text = "${post.authorEmail} • ${formatDate(post.timestamp)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = BentoTextSecondary
                    )
                }

                // Aesthetic category tag
                Box(
                    modifier = Modifier
                        .background(BentoPinkContainer, shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "CAMPUS",
                        color = BentoPinkContent,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyLarge,
                color = BentoTextMain,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = onLikeToggle,
                    modifier = Modifier.testTag("like_post_button_${post.id}")
                ) {
                    Icon(
                        imageVector = if (hasLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like Button",
                        tint = if (hasLiked) Color(0xFFEF4444) else BentoTextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = "${post.likesCount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (hasLiked) BentoLavenderContent else BentoTextSecondary,
                    fontWeight = if (hasLiked) FontWeight.Bold else FontWeight.Normal
                )
                
                if (post.authorEmail != currentUserEmail) {
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = onMessageClick) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Message",
                            tint = BentoLavenderContent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}


fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
