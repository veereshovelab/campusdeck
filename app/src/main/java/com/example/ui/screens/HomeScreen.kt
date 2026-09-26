package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.FeedPost
import com.example.data.model.FriendSuggestion
import com.example.data.model.HomeFeedItem
import com.example.ui.CampusViewModel
import com.example.ui.components.AnimatedLikeButton
import com.example.ui.components.CampusLogo
import com.example.ui.components.FriendSuggestionCarousel
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CampusEvent(
    val title: String,
    val date: String,
    val description: String,
    val tag: String
)

data class FeaturedCollegeBanner(
    val name: String,
    val location: String,
    val imageUrl: String,
    val events: List<CampusEvent>
)

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
    var selectedCollegeForEvents by remember { mutableStateOf<FeaturedCollegeBanner?>(null) }

    val featuredColleges = remember {
        listOf(
            FeaturedCollegeBanner(
                name = "KLE Technological University, Hubballi",
                location = "Hubballi, Karnataka",
                imageUrl = "https://www.kletech.ac.in/admission/images/about-img.webp",
                events = listOf(
                    CampusEvent("Pleiades '26 Annual Fest", "Oct 24-26, 2026", "ProNite & Hackathon registration now open! Main Quadrangle & Open Air Theatre.", "FEST"),
                    CampusEvent("ACM CodeStorm '26 Hackathon", "Nov 2, 2026", "24-hour algorithmic coding sprint and product dev challenge. 500 CC prize pool!", "CODING"),
                    CampusEvent("EEC TechSymposium & Robotics Expo", "Nov 15, 2026", "Showcase of cutting-edge robotics, IoT hardware, and embedded systems in Aud B.", "EXPO"),
                    CampusEvent("KLE Champions Trophy '26", "Nov 20-22, 2026", "Inter-department football, basketball, and badminton sports meet.", "SPORTS")
                )
            ),
            FeaturedCollegeBanner(
                name = "SDM College of Engineering & Tech",
                location = "Dharwad, Karnataka",
                imageUrl = "https://images.unsplash.com/photo-1541829070764-84a7d30dd3f3?q=80&w=1000",
                events = listOf(
                    CampusEvent("Synergy '26 Inter-College Meet", "Nov 5-6, 2026", "Paper Presentations, Coding Relays, and E-Sports Gaming in Main Auditorium.", "MEET"),
                    CampusEvent("Data Science & AI HackDay", "Nov 18, 2026", "Hands-on workshop on LLMs, PyTorch, and Computer Vision by Dept of IT.", "WORKSHOP"),
                    CampusEvent("SDM Kala Utsav '26 Arts Exhibition", "Dec 1, 2026", "Fine arts exhibition, photography contest, and live acoustic music performances.", "ARTS")
                )
            )
        )
    }

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
                            CampusLogo(
                                modifier = Modifier.size(26.dp)
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
                    .padding(horizontal = 16.dp, vertical = 10.dp),
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

            // Featured Colleges & Events Carousel Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Campus Hubs & Events",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp),
                        color = BentoTextMain
                    )
                    Text(
                        text = "Tap to Explore",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = BentoLavenderContent
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(featuredColleges) { college ->
                        CollegeBannerCard(
                            college = college,
                            onClick = { selectedCollegeForEvents = college }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                                .size(72.dp)
                                .background(BentoLilacContainer, shape = RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Campaign,
                                contentDescription = "Empty Bulletin",
                                tint = BentoLilacContent,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
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
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
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

    if (selectedCollegeForEvents != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedCollegeForEvents = null },
            sheetState = rememberModalBottomSheetState()
        ) {
            CollegeEventsSheetContent(
                college = selectedCollegeForEvents!!
            )
        }
    }
}

@Composable
fun CollegeBannerCard(college: FeaturedCollegeBanner, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(340.dp)
            .height(260.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
            ) {
                AsyncImage(
                    model = college.imageUrl,
                    contentDescription = college.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = college.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = college.location,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // Events List inside College
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Event,
                            contentDescription = "Events",
                            tint = BentoLavenderContent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "UPCOMING EVENTS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 10.sp),
                            color = BentoLavenderContent
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "View All",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                            color = BentoTextSecondary
                        )
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = BentoTextSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                college.events.take(2).forEach { event ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BentoLilacContainer.copy(alpha = 0.4f), shape = RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = BentoTextMain,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = event.description,
                                style = MaterialTheme.typography.labelSmall,
                                color = BentoTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Surface(
                            color = BentoPinkContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = event.date,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                color = BentoPinkContent,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CollegeEventsSheetContent(college: FeaturedCollegeBanner) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        // High-res Cover Header Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            AsyncImage(
                model = college.imageUrl,
                contentDescription = college.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Surface(
                    color = BentoLavenderContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "VERIFIED CAMPUS HUB",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = BentoLavenderContent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = college.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color.White
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = college.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Complete Campus Events (${college.events.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = BentoTextMain
                )
                Text(
                    text = "Live Updates",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = BentoLavenderContent
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            college.events.forEach { event ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = BentoPinkContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = event.tag,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = BentoPinkContent,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Event,
                                    contentDescription = null,
                                    tint = BentoLavenderContent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = event.date,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = BentoLavenderContent
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = BentoTextMain
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = event.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = BentoTextSecondary,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                Toast.makeText(context, "Registered for '${event.title}'! Added to schedule.", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BentoLavenderContainer,
                                contentColor = BentoLavenderContent
                            )
                        ) {
                            Text("RSVP & Add to Schedule", fontWeight = FontWeight.Bold)
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
                AnimatedLikeButton(
                    hasLiked = hasLiked,
                    onLikeToggle = onLikeToggle,
                    modifier = Modifier.testTag("like_post_button_${post.id}")
                )
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
