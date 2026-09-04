package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Input
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Gig
import com.example.ui.CampusViewModel
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import androidx.compose.material.icons.filled.Email

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GigScreen(
    viewModel: CampusViewModel,
    modifier: Modifier = Modifier,
    onStartChat: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val user by viewModel.currentUser.collectAsState()
    val gigs by viewModel.gigs.collectAsState()

    var showPostGigDialog by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    val categories = listOf("All", "Tutoring", "Lab Equipment", "Study Help", "Other")

    // Filter gigs by selected category
    val filteredGigs = gigs.filter { gig ->
        if (selectedCategoryFilter == "All") true
        else gig.category.equals(selectedCategoryFilter, ignoreCase = true)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
                            Text(
                                text = "Campus Gig Board",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp),
                                color = BentoTextMain
                            )
                        }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = BentoLavenderContainer),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "${user?.points ?: 0} CC",
                                color = BentoLavenderContent,
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BentoBackground,
                    titleContentColor = BentoTextMain
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showPostGigDialog = true },
                containerColor = BentoLavenderContent,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .testTag("post_gig_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Post Gig")
            }
        },
        containerColor = BentoBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Horizontal Category Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { chip ->
                    val isSelected = selectedCategoryFilter == chip
                    Card(
                        modifier = Modifier
                            .clickable { selectedCategoryFilter = chip }
                            .testTag("filter_chip_$chip")
                            .then(
                                if (!isSelected) Modifier.border(1.dp, BentoBorder.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                else Modifier
                            ),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) BentoLilacContainer else Color.White
                        )
                    ) {
                        Text(
                            text = chip,
                            color = if (isSelected) BentoLilacContent else BentoTextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else Modifier.let { FontWeight.Medium },
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Scrollable library items list
            if (filteredGigs.isEmpty()) {
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
                                .background(BentoLavenderContainer, shape = RoundedCornerShape(24.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Lightbulb,
                                contentDescription = "Empty Gigs",
                                tint = BentoLavenderContent,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Peer Gigs Active",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = BentoTextMain
                        )
                        Text(
                            text = "Everything is caught up! Be the first to ask for quick peer tutoring, a hand moving, or borrowing equipment.",
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
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredGigs) { gig ->
                        GigCardItem(
                            gig = gig,
                            currentUserEmail = user?.email ?: "",
                            onAccept = {
                                viewModel.acceptGig(gig)
                                Toast.makeText(context, "You accepted the gig! Reach out via contact details.", Toast.LENGTH_LONG).show()
                            },
                            onComplete = {
                                viewModel.completeGig(gig)
                                Toast.makeText(context, "Gig marked completed! Escrow points released to helper.", Toast.LENGTH_LONG).show()
                            },
                            onCancel = {
                                viewModel.cancelGig(gig)
                                Toast.makeText(context, "Gig cancelled and points refunded successfully.", Toast.LENGTH_SHORT).show()
                            },
                            onMessageClick = { onStartChat(gig.requesterEmail) }
                        )
                    }
                }
            }
        }

        // Post Gig Modal Dialog
        if (showPostGigDialog) {
            PostGigDialog(
                onDismiss = { showPostGigDialog = false },
                onPost = { title, category, description, points, contact ->
                    viewModel.postGig(title, category, description, points, contact) { success, msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                        if (success) showPostGigDialog = false
                    }
                }
            )
        }
    }
}

@Composable
fun GigCardItem(
    gig: Gig,
    currentUserEmail: String,
    onAccept: () -> Unit,
    onComplete: () -> Unit,
    onCancel: () -> Unit,
    onMessageClick: () -> Unit
) {
    val isMyGig = gig.requesterEmail == currentUserEmail
    val isHelperMe = gig.helperEmail == currentUserEmail

    val categoryIcon = when (gig.category.lowercase()) {
        "tutoring" -> Icons.Default.School
        "lab equipment" -> Icons.Default.ElectricalServices
        "study help" -> Icons.Default.Group
        else -> Icons.Default.Lightbulb
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("gig_card_${gig.id}"),
        cornerRadius = 24.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1.4f)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(BentoLilacContainer, shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(categoryIcon, contentDescription = gig.category, tint = BentoLilacContent, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = gig.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = BentoTextMain
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "By ${gig.requesterName} • ${gig.category}",
                                style = MaterialTheme.typography.bodySmall,
                                color = BentoTextSecondary
                            )
                            if (!isMyGig) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    Icons.Default.Email,
                                    contentDescription = "Message",
                                    tint = BentoLavenderContent,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { onMessageClick() }
                                )
                            }
                        }
                    }
                }

                // Reward Badge - Bento Style Pink
                Card(
                    colors = CardDefaults.cardColors(containerColor = BentoPinkContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "+${gig.rewardPoints} CC",
                        color = BentoPinkContent,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = gig.description,
                style = MaterialTheme.typography.bodyMedium,
                color = BentoTextMain.copy(alpha = 0.8f)
            )

            // Dynamic interaction panel based on status and roles
            Spacer(modifier = Modifier.height(16.dp))

            when (gig.status) {
                "OPEN" -> {
                    if (isMyGig) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Status: Awaiting Helper",
                                color = BentoTextSecondary,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = onCancel,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.testTag("cancel_gig_button_${gig.id}")
                            ) {
                                Text("Delete & Refund", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Button(
                            onClick = onAccept,
                            colors = ButtonDefaults.buttonColors(containerColor = BentoLavenderContainer, contentColor = BentoLavenderContent),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("accept_gig_button_${gig.id}")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Handshake, contentDescription = "Handshake", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Accept Help Request", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                "ACCEPTED" -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BentoBlueContainer),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Gig Accepted by ${gig.helperName}",
                                fontWeight = FontWeight.Bold,
                                color = BentoBlueContent,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Contact Information: ${gig.contactInfo}",
                                color = BentoTextMain,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (isHelperMe || isMyGig) {
                                    Button(
                                        onClick = onComplete,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E), contentColor = Color.White),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.testTag("complete_gig_button_${gig.id}")
                                    ) {
                                        Text("Mark Completed", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                "COMPLETED" -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BentoLilacContainer),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Completed", tint = BentoLilacContent)
                            Column {
                                Text(
                                    text = "Gig Completed Successfully",
                                    fontWeight = FontWeight.Bold,
                                    color = BentoLilacContent,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "Helper: ${gig.helperName}",
                                    color = BentoTextSecondary,
                                    style = MaterialTheme.typography.bodySmall
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
fun PostGigDialog(
    onDismiss: () -> Unit,
    onPost: (String, String, String, Int, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Tutoring") }
    var rewardPoints by remember { mutableStateOf("20") }
    var contactInfo by remember { mutableStateOf("") }

    val categories = listOf("Tutoring", "Lab Equipment", "Study Help", "Other")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .border(1.dp, BentoBorder, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Request Peer Assistance",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp),
                    color = BentoTextMain
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("What do you need help with?") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = BentoTextMain,
                        unfocusedTextColor = BentoTextMain,
                        focusedBorderColor = BentoLavenderContent,
                        unfocusedBorderColor = BentoBorder,
                        focusedLabelColor = BentoLavenderContent,
                        unfocusedLabelColor = BentoTextSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gig_dialog_title")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Assistance Category", style = MaterialTheme.typography.bodySmall, color = BentoTextSecondary)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = category == cat
                        Card(
                            modifier = Modifier
                                .clickable { category = cat }
                                .weight(1f)
                                .then(
                                    if (!isSelected) Modifier.border(1.dp, BentoBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    else Modifier
                                ),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) BentoLilacContainer else Color.White
                            )
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) BentoLilacContent else BentoTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = rewardPoints,
                        onValueChange = { rewardPoints = it },
                        label = { Text("CC Locked in Escrow") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = BentoTextMain,
                            unfocusedTextColor = BentoTextMain,
                            focusedBorderColor = BentoLavenderContent,
                            unfocusedBorderColor = BentoBorder,
                            focusedLabelColor = BentoLavenderContent,
                            unfocusedLabelColor = BentoTextSecondary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("gig_dialog_points")
                    )

                    OutlinedTextField(
                        value = contactInfo,
                        onValueChange = { contactInfo = it },
                        label = { Text("Contact Info (e.g. Discord, Room)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = BentoTextMain,
                            unfocusedTextColor = BentoTextMain,
                            focusedBorderColor = BentoLavenderContent,
                            unfocusedBorderColor = BentoBorder,
                            focusedLabelColor = BentoLavenderContent,
                            unfocusedLabelColor = BentoTextSecondary
                        ),
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("gig_dialog_contact")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Detailed request (e.g. date, location, specifics)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = BentoTextMain,
                        unfocusedTextColor = BentoTextMain,
                        focusedBorderColor = BentoLavenderContent,
                        unfocusedBorderColor = BentoBorder,
                        focusedLabelColor = BentoLavenderContent,
                        unfocusedLabelColor = BentoTextSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("gig_dialog_desc")
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = BentoTextSecondary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val rewardPointsInt = rewardPoints.toIntOrNull() ?: 20
                            if (title.isNotBlank() && description.isNotBlank() && contactInfo.isNotBlank()) {
                                onPost(title, category, description, rewardPointsInt, contactInfo)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BentoLavenderContent, contentColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("gig_dialog_submit")
                    ) {
                        Text("Post Help Request", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
