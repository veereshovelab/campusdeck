package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.ui.CampusViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInboxScreen(viewModel: CampusViewModel, onChatClick: (Int) -> Unit) {
    val dmsWithUsers by viewModel.directMessagesWithUsers.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    var showNewChatSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadDirectMessages()
    }

    Scaffold(
        containerColor = BentoBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewChatSheet = true },
                containerColor = BentoLavenderContent,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Chat")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                "Messages",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BentoTextMain,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (dmsWithUsers.isEmpty()) {
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
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = BentoLilacContent,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No Message Connections Yet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = BentoTextMain
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Tap the '+' button below to search by college or course and connect with peers!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = BentoTextSecondary,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(dmsWithUsers) { item ->
                        val dm = item.dm
                        val otherUser = item.otherUser
                        val otherUserEmail = if (dm.participant1Email == currentUser?.email) dm.participant2Email else dm.participant1Email
                        val displayName = otherUser?.name ?: otherUserEmail.substringBefore("@").replace(".", " ").capitalizeWords()

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                                .clickable { onChatClick(dm.id) },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Avatar circle with initials and online dot badge
                                Box {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(BentoLilacContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            displayName.firstOrNull()?.toString()?.uppercase() ?: "?",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 20.sp,
                                            color = BentoLilacContent
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF22C55E))
                                            .border(2.dp, Color.White, CircleShape)
                                            .align(Alignment.BottomEnd)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            displayName,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = BentoTextMain,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            formatChatTime(dm.lastMessageTimestamp),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = BentoTextSecondary
                                        )
                                    }

                                    Text(
                                        "${otherUser?.collegeName ?: "Campus"} • ${otherUser?.major ?: "Student"}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = BentoLavenderContent,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )

                                    Text(
                                        if (dm.lastMessage.isNotBlank()) dm.lastMessage else "Conversation started! Say hi.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (dm.lastMessage.isNotBlank()) BentoTextSecondary else BentoTextSecondary.copy(alpha = 0.6f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showNewChatSheet) {
        ModalBottomSheet(
            onDismissRequest = { showNewChatSheet = false }
        ) {
            NewChatSheetContent(viewModel, onChatClick = { chatId ->
                showNewChatSheet = false
                onChatClick(chatId)
            })
        }
    }
}

@Composable
fun NewChatSheetContent(viewModel: CampusViewModel, onChatClick: (Int) -> Unit) {
    var searchMode by remember { mutableStateOf("College") }

    val allColleges by viewModel.allColleges.collectAsState()
    val allMajors by viewModel.allMajors.collectAsState()

    var selectedCollege by remember { mutableStateOf<String?>(null) }
    var selectedMajor by remember { mutableStateOf<String?>(null) }
    var nameQuery by remember { mutableStateOf("") }

    val usersInCollege by viewModel.usersInSelectedCollege.collectAsState()
    val usersInMajor by viewModel.usersInSelectedMajor.collectAsState()
    val usersByName by viewModel.usersByName.collectAsState()

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var filterQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "New Chat Request",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BentoTextMain
            )
            Surface(
                color = BentoLavenderContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Cost: 10 CC",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = BentoLavenderContent,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        errorMessage?.let { msg ->
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoPinkContainer),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                Text(
                    msg,
                    color = BentoPinkContent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = searchMode == "College",
                onClick = {
                    searchMode = "College"
                    selectedCollege = null
                    filterQuery = ""
                },
                label = { Text("By College") },
                leadingIcon = { Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            FilterChip(
                selected = searchMode == "Course",
                onClick = {
                    searchMode = "Course"
                    selectedMajor = null
                    filterQuery = ""
                },
                label = { Text("By Course/Major") },
                leadingIcon = { Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            FilterChip(
                selected = searchMode == "Name",
                onClick = {
                    searchMode = "Name"
                    filterQuery = ""
                },
                label = { Text("By Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }

        when (searchMode) {
            "College" -> {
                if (selectedCollege == null) {
                    Text(
                        "Select a College to view students:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = BentoTextSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = filterQuery,
                        onValueChange = { filterQuery = it },
                        placeholder = { Text("Search colleges...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    val filteredColleges = allColleges.filter { it.contains(filterQuery, ignoreCase = true) }

                    if (filteredColleges.isEmpty()) {
                        Text("No colleges found in database.", color = BentoTextSecondary, modifier = Modifier.padding(16.dp))
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(filteredColleges) { college ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedCollege = college
                                            viewModel.selectCollegeForChat(college)
                                        },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = BentoLilacContainer.copy(alpha = 0.4f))
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Business, contentDescription = null, tint = BentoLavenderContent)
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(college, fontWeight = FontWeight.Bold, color = BentoTextMain)
                                        }
                                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = BentoTextSecondary)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        IconButton(onClick = { selectedCollege = null }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text("Students at", style = MaterialTheme.typography.bodySmall, color = BentoTextSecondary)
                            Text(selectedCollege!!, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BentoTextMain)
                        }
                    }

                    if (usersInCollege.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("No other students registered at this college yet.", color = BentoTextSecondary)
                        }
                    } else {
                        LazyColumn {
                            items(usersInCollege) { user ->
                                UserListItem(user = user, onClick = {
                                    viewModel.startNewChat(user.email) { success, msg, chatId ->
                                        if (success && chatId != null) {
                                            onChatClick(chatId)
                                        } else {
                                            errorMessage = msg
                                        }
                                    }
                                })
                            }
                        }
                    }
                }
            }

            "Course" -> {
                if (selectedMajor == null) {
                    Text(
                        "Select a Course/Major to view students across colleges:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = BentoTextSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = filterQuery,
                        onValueChange = { filterQuery = it },
                        placeholder = { Text("Search courses/majors...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    val filteredMajors = allMajors.filter { it.contains(filterQuery, ignoreCase = true) }

                    if (filteredMajors.isEmpty()) {
                        Text("No courses/majors found in database.", color = BentoTextSecondary, modifier = Modifier.padding(16.dp))
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(filteredMajors) { major ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedMajor = major
                                            viewModel.selectMajorForChat(major)
                                        },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = BentoLilacContainer.copy(alpha = 0.4f))
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.School, contentDescription = null, tint = BentoLilacContent)
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(major, fontWeight = FontWeight.Bold, color = BentoTextMain)
                                        }
                                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = BentoTextSecondary)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        IconButton(onClick = { selectedMajor = null }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text("Students enrolled in", style = MaterialTheme.typography.bodySmall, color = BentoTextSecondary)
                            Text(selectedMajor!!, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BentoTextMain)
                        }
                    }

                    if (usersInMajor.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("No students found in this course/major.", color = BentoTextSecondary)
                        }
                    } else {
                        LazyColumn {
                            items(usersInMajor) { user ->
                                UserListItem(user = user, onClick = {
                                    viewModel.startNewChat(user.email) { success, msg, chatId ->
                                        if (success && chatId != null) {
                                            onChatClick(chatId)
                                        } else {
                                            errorMessage = msg
                                        }
                                    }
                                })
                            }
                        }
                    }
                }
            }

            "Name" -> {
                OutlinedTextField(
                    value = nameQuery,
                    onValueChange = {
                        nameQuery = it
                        viewModel.searchUsersByName(it)
                    },
                    label = { Text("Search by student name...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                if (nameQuery.isBlank()) {
                    Text("Type a name above to search students.", color = BentoTextSecondary, modifier = Modifier.padding(16.dp))
                } else if (usersByName.isEmpty()) {
                    Text("No students found matching '$nameQuery'.", color = BentoTextSecondary, modifier = Modifier.padding(16.dp))
                } else {
                    LazyColumn {
                        items(usersByName) { user ->
                            UserListItem(user = user, onClick = {
                                viewModel.startNewChat(user.email) { success, msg, chatId ->
                                    if (success && chatId != null) {
                                        onChatClick(chatId)
                                    } else {
                                        errorMessage = msg
                                    }
                                }
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserListItem(user: User, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(user.name, fontWeight = FontWeight.Bold, color = BentoTextMain) },
        supportingContent = { Text("${user.collegeName} • ${user.major}", color = BentoLavenderContent, fontWeight = FontWeight.SemiBold) },
        leadingContent = {
            Surface(
                shape = CircleShape,
                color = BentoLilacContainer,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        user.name.firstOrNull()?.toString()?.uppercase() ?: "?",
                        fontWeight = FontWeight.ExtraBold,
                        color = BentoLilacContent
                    )
                }
            }
        },
        modifier = Modifier
            .clickable { onClick() }
    )
    HorizontalDivider()
}

private fun formatChatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}
