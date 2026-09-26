package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CampusViewModel
import com.example.ui.components.CampusLogo
import com.example.ui.components.ResourceCardItem
import com.example.ui.components.UploadResourceDialog
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentScreen(
    viewModel: CampusViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val user by viewModel.currentUser.collectAsState()
    val resources by viewModel.resources.collectAsState()
    val unlockedIds by viewModel.unlockedResourceIds.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showUploadDialog by remember { mutableStateOf(value = false) }

    val assignmentResources = resources.filter { it.fileType == "Assignment" }

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
                                CampusLogo(
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Assignment Vault",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp),
                                color = BentoTextMain
                            )
                        }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = BentoPinkContainer),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "${user?.points ?: 0} CC",
                                color = BentoPinkContent,
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
                onClick = { showUploadDialog = true },
                containerColor = BentoPinkContent,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .testTag("upload_assignment_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Upload Assignment")
            }
        },
        containerColor = BentoBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search assignments...", color = BentoTextSecondary.copy(alpha = 0.6f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = BentoTextSecondary) },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = BentoTextMain,
                    unfocusedTextColor = BentoTextMain,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = BentoBorder,
                    unfocusedBorderColor = BentoBorder.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("assignment_search_bar")
            )

            if (assignmentResources.isEmpty()) {
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
                                .background(BentoPinkContainer, shape = RoundedCornerShape(24.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Assignment,
                                contentDescription = "Empty Vault",
                                tint = BentoPinkContent,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Assignments Shared Yet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = BentoTextMain
                        )
                        Text(
                            text = "Be the first to share an assignment guide or solved problem set to earn CC credits!",
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
                    items(assignmentResources) { resource ->
                        ResourceCardItem(
                            resource = resource,
                            isUnlocked = (unlockedIds.contains(resource.id) || resource.uploaderEmail == user?.email),
                            isSelfUploaded = resource.uploaderEmail == user?.email,
                            onPurchase = {
                                viewModel.purchaseResource(resource) { _, msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                }
                            },
                        ) {
                            Toast.makeText(context, "Opening Assignment: ${resource.title}...", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        if (showUploadDialog) {
            UploadResourceDialog(
                onDismiss = { showUploadDialog = false },
                onUpload = { title, course, prof, sem, desc, _, price ->
                    viewModel.addResource(title, course, prof, sem, desc, "Assignment", price)
                    Toast.makeText(context, "Assignment uploaded to the vault!", Toast.LENGTH_SHORT).show()
                    showUploadDialog = false
                },
            )
        }
    }
}
