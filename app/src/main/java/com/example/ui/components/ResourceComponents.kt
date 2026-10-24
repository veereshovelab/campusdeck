package com.example.ui.components

import androidx.compose.foundation.border
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ResourceMaterial
import com.example.ui.theme.*

@Composable
fun ResourceCardItem(
    resource: ResourceMaterial,
    isUnlocked: Boolean,
    isSelfUploaded: Boolean,
    onPurchase: () -> Unit,
    onDownloadSimulate: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("resource_card_${resource.id}"),
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
                Column(modifier = Modifier.weight(1.4f)) {
                    Text(
                        text = resource.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = BentoTextMain,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Prof. ${resource.professor} • ${resource.semester}",
                        style = MaterialTheme.typography.bodySmall,
                        color = BentoTextSecondary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.weight(1.1f)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BentoBlueContainer),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = resource.courseCode.uppercase(),
                            color = BentoBlueContent,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = BentoPinkContainer),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = resource.fileType.uppercase(),
                            color = BentoPinkContent,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = resource.description,
                style = MaterialTheme.typography.bodyMedium,
                color = BentoTextMain.copy(alpha = 0.8f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Shared by ${resource.uploaderName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = BentoTextSecondary
                    )
                    if (isSelfUploaded) {
                        Text(
                            text = "Your Listing",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = BentoLavenderContent
                        )
                    }
                }

                if (isUnlocked) {
                    Button(
                        onClick = onDownloadSimulate,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E), contentColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Download, contentDescription = "Download", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open PDF", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Button(
                        onClick = onPurchase,
                        colors = ButtonDefaults.buttonColors(containerColor = BentoLavenderContainer, contentColor = BentoLavenderContent),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("purchase_button_${resource.id}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            Icon(Icons.Default.LockOpen, contentDescription = "Unlock", tint = BentoLavenderContent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Unlock for ${resource.priceInPoints} CC", color = BentoLavenderContent, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UploadResourceDialog(
    onDismiss: () -> Unit,
    onUpload: (String, String, String, String, String, String, Int, Uri?) -> Unit,
    initialFileType: String = "Notes"
) {
    var title by remember { mutableStateOf("") }
    var courseCode by remember { mutableStateOf("") }
    var professor by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var fileType by remember { mutableStateOf(initialFileType) }
    var priceInPoints by remember { mutableStateOf("15") }
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) {
                fileUri = uri
                fileName = uri.lastPathSegment ?: "document.pdf"
            }
        }
    )

    val fileTypes = listOf("Assignment", "Notes", "Study Guide", "Cheat Sheet", "Exam Prep")

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
                    text = "Share Study Guide/Notes",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp),
                    color = BentoTextMain
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Material Title (e.g. CS201 Midterm Summary)") },
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
                        .testTag("dialog_title_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = courseCode,
                        onValueChange = { courseCode = it },
                        label = { Text("Course Code") },
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
                            .weight(1f)
                            .testTag("dialog_course_input")
                    )

                    OutlinedTextField(
                        value = professor,
                        onValueChange = { professor = it },
                        label = { Text("Professor") },
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
                            .weight(1f)
                            .testTag("dialog_professor_input")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = semester,
                        onValueChange = { semester = it },
                        label = { Text("Semester (e.g. Fall '26)") },
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
                            .weight(1.1f)
                            .testTag("dialog_semester_input")
                    )

                    OutlinedTextField(
                        value = priceInPoints,
                        onValueChange = { priceInPoints = it },
                        label = { Text("Price (CC)") },
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
                            .weight(0.9f)
                            .testTag("dialog_price_input")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Document Classification", style = MaterialTheme.typography.bodySmall, color = BentoTextSecondary)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    fileTypes.forEach { type ->
                        val isSelected = fileType == type
                        Card(
                            modifier = Modifier
                                .clickable { fileType = type }
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
                                text = type,
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
                
                Button(
                    onClick = { filePickerLauncher.launch(arrayOf("application/pdf")) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoLilacContainer, contentColor = BentoLilacContent)
                ) {
                    Text(if (fileName != null) "Selected: $fileName" else "Attach PDF File")
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Brief Description of Contents") },
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
                        .height(80.dp)
                        .testTag("dialog_desc_input")
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
                            val priceInt = priceInPoints.toIntOrNull() ?: 15
                            if (title.isNotBlank() && courseCode.isNotBlank() && fileUri != null) {
                                onUpload(title, courseCode, professor, semester, description, fileType, priceInt, fileUri)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BentoLavenderContent, contentColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("dialog_submit_button")
                    ) {
                        Text("Share to Library", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
