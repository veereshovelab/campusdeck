package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.CampusViewModel
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: CampusViewModel,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val user by viewModel.currentUser.collectAsState()
    val resources by viewModel.resources.collectAsState()
    val gigs by viewModel.gigs.collectAsState()

    val userResources = resources.filter { it.uploaderEmail == user?.email }
    val userGigs = gigs.filter { it.requesterEmail == user?.email }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Student Profile",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp),
                        color = BentoTextMain
                    )
                },
                actions = {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.testTag("edit_profile_button")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = BentoTextSecondary)
                    }
                    IconButton(
                        onClick = {
                            viewModel.logout()
                            Toast.makeText(context, "Logged out from institutional network", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("logout_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color(0xFFEF4444))
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Student Profile Identity Banner Card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_identity_card"),
                cornerRadius = 28.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Initials or Picture Avatar
                    Box {
                        Box(
                            modifier = Modifier
                                .size(92.dp)
                                .clip(CircleShape)
                                .background(BentoLilacContainer)
                                .border(3.dp, BentoLavenderContent.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (user?.profilePictureUri != null) {
                                AsyncImage(
                                    model = user?.profilePictureUri,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = user?.name?.firstOrNull()?.toString()?.uppercase() ?: "?",
                                    color = BentoLilacContent,
                                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold, fontSize = 38.sp)
                                )
                            }
                        }

                        // Online Status Badge
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E))
                                .border(3.dp, Color.White, CircleShape)
                                .align(Alignment.BottomEnd)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = user?.name ?: "Student",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, fontSize = 22.sp),
                            color = BentoTextMain
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Verified Student",
                            tint = BentoLavenderContent,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // College & Major Tag Badge Pill
                    Surface(
                        modifier = Modifier.padding(top = 8.dp),
                        color = BentoLavenderContainer,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.School,
                                contentDescription = null,
                                tint = BentoLavenderContent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${user?.collegeName ?: "Campus"} • ${user?.major ?: "Student"}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = BentoLavenderContent
                            )
                        }
                    }

                    if (user?.bio?.isNotBlank() == true) {
                        Text(
                            text = "\"${user?.bio}\"",
                            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                            color = BentoTextSecondary,
                            modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Buttons Row inside Profile Hero
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onEditClick,
                            modifier = Modifier.weight(1f).height(46.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BentoLavenderContent,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Profile", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.logout()
                                Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f).height(46.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Log Out", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2x2 Bento Stat Cards Grid
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Stat 1: Campus Credits
                    Card(
                        modifier = Modifier.weight(1f).height(100.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = BentoPinkContainer),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Balance", style = MaterialTheme.typography.labelMedium, color = BentoPinkContent)
                                Icon(Icons.Default.Payments, contentDescription = null, tint = BentoPinkContent, modifier = Modifier.size(20.dp))
                            }
                            Text(
                                "${user?.points ?: 0} CC",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = BentoPinkContent
                            )
                        }
                    }

                    // Stat 2: Student ID
                    Card(
                        modifier = Modifier.weight(1f).height(100.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = BentoLilacContainer),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Student ID", style = MaterialTheme.typography.labelMedium, color = BentoLilacContent)
                                Icon(Icons.Default.Badge, contentDescription = null, tint = BentoLilacContent, modifier = Modifier.size(20.dp))
                            }
                            Text(
                                user?.studentId ?: "N/A",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = BentoLilacContent
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Stat 3: Shared Notes Count
                    Card(
                        modifier = Modifier.weight(1f).height(100.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = BentoLavenderContainer),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Shared Notes", style = MaterialTheme.typography.labelMedium, color = BentoLavenderContent)
                                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = BentoLavenderContent, modifier = Modifier.size(20.dp))
                            }
                            Text(
                                "${userResources.size} Files",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = BentoLavenderContent
                            )
                        }
                    }

                    // Stat 4: Active Gigs Count
                    Card(
                        modifier = Modifier.weight(1f).height(100.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = BentoBlueContainer),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Active Gigs", style = MaterialTheme.typography.labelMedium, color = BentoBlueContent)
                                Icon(Icons.Default.TaskAlt, contentDescription = null, tint = BentoBlueContent, modifier = Modifier.size(20.dp))
                            }
                            Text(
                                "${userGigs.size} Posts",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = BentoBlueContent
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // How Campus Credits Work Tutorial Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BentoBorder.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BentoBlueContainer.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = "Help", tint = BentoBlueContent, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Campus Credits (CC) Guide",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = BentoBlueContent
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• 📤 Upload course guides or notes. Each peer unlock earns you CC credits.\n" +
                                "• 💬 Start chat connections with peers across colleges for 10 CC.\n" +
                                "• 🤝 Request or offer tutoring & lab hardware gigs to transfer CC tokens securely.",
                        style = MaterialTheme.typography.bodySmall,
                        color = BentoTextMain,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 1: User's Shared Notes
            Text(
                text = "Your Shared Notes (${userResources.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp),
                color = BentoTextMain,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (userResources.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BentoBorder.copy(alpha = 0.3f), RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Text(
                        text = "You haven't uploaded any study materials yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BentoTextSecondary,
                        modifier = Modifier.padding(18.dp)
                    )
                }
            } else {
                userResources.forEach { resource ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .border(1.dp, BentoBorder.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(resource.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = BentoTextMain)
                                Text("${resource.courseCode.uppercase()} • Earns ${resource.priceInPoints} CC / unlock", style = MaterialTheme.typography.bodySmall, color = BentoTextSecondary)
                            }
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(BentoLavenderContainer, shape = RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Resource", tint = BentoLavenderContent, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 2: User's Active Gigs
            Text(
                text = "Your Active Gigs (${userGigs.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp),
                color = BentoTextMain,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (userGigs.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BentoBorder.copy(alpha = 0.3f), RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Text(
                        text = "You haven't requested any peer help yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BentoTextSecondary,
                        modifier = Modifier.padding(18.dp)
                    )
                }
            } else {
                userGigs.forEach { gig ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .border(1.dp, BentoBorder.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(gig.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = BentoTextMain)
                                Text("Reward: ${gig.rewardPoints} CC • Status: ${gig.status}", style = MaterialTheme.typography.bodySmall, color = BentoTextSecondary)
                            }
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(BentoPinkContainer, shape = RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.TaskAlt, contentDescription = "Gig", tint = BentoPinkContent, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
