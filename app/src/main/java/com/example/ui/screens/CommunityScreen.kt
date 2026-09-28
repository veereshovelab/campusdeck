package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CommunityComment
import com.example.data.model.CommunityPost
import com.example.ui.CampusViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    viewModel: CampusViewModel,
    modifier: Modifier = Modifier
) {
    val posts by viewModel.communityPosts.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val context = LocalContext.current

    var selectedCategory by remember { mutableStateOf("All") }
    var showCreatePostSheet by remember { mutableStateOf(false) }
    var selectedPostForComments by remember { mutableStateOf<CommunityPost?>(null) }

    val categories = listOf("All", "Campus Issue", "Discussion", "Advice", "Confessions")

    val filteredPosts = remember(posts, selectedCategory) {
        if (selectedCategory == "All") {
            posts
        } else {
            posts.filter { it.categoryTag.equals(selectedCategory, ignoreCase = true) }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BentoBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreatePostSheet = true },
                containerColor = BentoLavenderContent,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Post")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Post Topic", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Campus Community 🥷",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp),
                        color = BentoTextMain
                    )
                    Text(
                        text = "Anonymous campus issues, advice, & student forums",
                        style = MaterialTheme.typography.bodySmall,
                        color = BentoTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category Filter Chips Carousel
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category, fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Medium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BentoLavenderContainer,
                            selectedLabelColor = BentoLavenderContent
                        )
                    )
                }
            }

            if (filteredPosts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(BentoLilacContainer, shape = RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Security,
                                contentDescription = null,
                                tint = BentoLilacContent,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "No Community Discussions Yet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = BentoTextMain
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Be the first student to start an anonymous discussion or speak up about a campus issue!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = BentoTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredPosts) { post ->
                        CommunityPostCard(
                            post = post,
                            currentUserEmail = currentUser?.email ?: "",
                            onVote = { isUpvote -> viewModel.voteCommunityPost(post, isUpvote) },
                            onCommentClick = {
                                selectedPostForComments = post
                                viewModel.loadCommentsForPost(post.id)
                            }
                        )
                    }
                }
            }
        }
    }

    // Create New Community Post Bottom Sheet
    if (showCreatePostSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCreatePostSheet = false }
        ) {
            CreateCommunityPostSheet(
                onSubmit = { title, content, tag, isAnon ->
                    viewModel.createCommunityPost(title, content, tag, isAnon)
                    showCreatePostSheet = false
                    Toast.makeText(context, "Published to Campus Community!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    // Post Comments Bottom Sheet
    if (selectedPostForComments != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedPostForComments = null }
        ) {
            CommunityCommentsSheet(
                viewModel = viewModel,
                post = selectedPostForComments!!,
                currentUserEmail = currentUser?.email ?: ""
            )
        }
    }
}

@Composable
fun CommunityPostCard(
    post: CommunityPost,
    currentUserEmail: String,
    onVote: (Boolean) -> Unit,
    onCommentClick: () -> Unit
) {
    val hasUpvoted = post.upvotedByEmails.split(",").map { it.trim() }.contains(currentUserEmail)
    val hasDownvoted = post.downvotedByEmails.split(",").map { it.trim() }.contains(currentUserEmail)
    val netVotes = post.upvotesCount - post.downvotesCount

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCommentClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Upvote / Downvote Reddit Style Control Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .background(BentoLilacContainer.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp))
                    .padding(vertical = 6.dp, horizontal = 4.dp)
            ) {
                IconButton(
                    onClick = { onVote(true) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = "Upvote",
                        tint = if (hasUpvoted) Color(0xFFEF4444) else BentoTextSecondary
                    )
                }

                Text(
                    text = "$netVotes",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = if (hasUpvoted) Color(0xFFEF4444) else if (hasDownvoted) Color(0xFF3B82F6) else BentoTextMain
                )

                IconButton(
                    onClick = { onVote(false) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Downvote",
                        tint = if (hasDownvoted) Color(0xFF3B82F6) else BentoTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Main Post Content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.authorName,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = if (post.isAnonymous) BentoLavenderContent else BentoTextMain
                        )
                        Text(
                            text = " • ${formatCommunityTime(post.timestamp)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = BentoTextSecondary
                        )
                    }

                    Surface(
                        color = BentoPinkContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = post.categoryTag.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 9.sp),
                            color = BentoPinkContent,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = BentoTextMain,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = post.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = BentoTextSecondary,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onCommentClick() }
                ) {
                    Icon(
                        Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comments",
                        tint = BentoLavenderContent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.commentsCount} Comments",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = BentoLavenderContent
                    )
                }
            }
        }
    }
}

@Composable
fun CreateCommunityPostSheet(
    onSubmit: (String, String, String, Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var categoryTag by remember { mutableStateOf("Campus Issue") }
    var isAnonymous by remember { mutableStateOf(true) }

    val tags = listOf("Campus Issue", "Discussion", "Advice", "Confessions")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Start Community Discussion",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = BentoTextMain
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Category Tag Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            tags.forEach { tag ->
                FilterChip(
                    selected = categoryTag == tag,
                    onClick = { categoryTag = tag },
                    label = { Text(tag, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Topic Title") },
            placeholder = { Text("e.g. Wi-Fi issues in Library, Midterm tips...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Post Details / Question") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(12.dp),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BentoLilacContainer.copy(alpha = 0.5f), shape = RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Security, contentDescription = null, tint = BentoLavenderContent)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Post Anonymously 🥷", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = BentoTextMain)
                    Text("Your identity will be hidden from everyone", style = MaterialTheme.typography.labelSmall, color = BentoTextSecondary)
                }
            }
            Switch(
                checked = isAnonymous,
                onCheckedChange = { isAnonymous = it },
                colors = SwitchDefaults.colors(checkedThumbColor = BentoLavenderContent, checkedTrackColor = BentoLavenderContainer)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                if (title.isNotBlank() && content.isNotBlank()) {
                    onSubmit(title.trim(), content.trim(), categoryTag, isAnonymous)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BentoLavenderContent, contentColor = Color.White)
        ) {
            Text("Publish to Community", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
fun CommunityCommentsSheet(
    viewModel: CampusViewModel,
    post: CommunityPost,
    currentUserEmail: String
) {
    val comments by viewModel.activeComments.collectAsState()
    var newCommentText by remember { mutableStateOf("") }
    var isCommentAnonymous by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = post.title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = BentoTextMain
        )
        Text(
            text = "Posted by ${post.authorName} • ${post.commentsCount} Comments",
            style = MaterialTheme.typography.bodySmall,
            color = BentoTextSecondary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (comments.isEmpty()) {
                item {
                    Text(
                        text = "No comments yet. Be the first to share an answer or opinion!",
                        color = BentoTextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(comments) { comment ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = comment.authorName,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (comment.isAnonymous) BentoLavenderContent else BentoTextMain
                                )
                                Text(
                                    text = formatCommunityTime(comment.timestamp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BentoTextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = comment.content,
                                style = MaterialTheme.typography.bodyMedium,
                                color = BentoTextMain
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Comment Input Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newCommentText,
                onValueChange = { newCommentText = it },
                placeholder = { Text("Write a comment...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            FloatingActionButton(
                onClick = {
                    if (newCommentText.isNotBlank()) {
                        viewModel.addCommunityComment(post.id, newCommentText.trim(), isCommentAnonymous)
                        newCommentText = ""
                    }
                },
                containerColor = BentoLavenderContent,
                contentColor = Color.White,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send Comment", modifier = Modifier.size(20.dp))
            }
        }
    }
}

private fun formatCommunityTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
