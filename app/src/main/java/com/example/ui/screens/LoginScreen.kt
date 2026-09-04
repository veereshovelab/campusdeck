package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CampusViewModel
import com.example.ui.theme.*

@Composable
fun LoginScreen(
    viewModel: CampusViewModel,
    modifier: Modifier = Modifier
) {
    val isRegistering by viewModel.isRegistering.collectAsState()
    val errorMsg by viewModel.loginError.collectAsState()

    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var collegeName by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var major by remember { mutableStateOf("") }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = BentoBackground
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Ambient grid background glow using pastel palette
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(BentoLavenderContainer.copy(alpha = 0.5f), Color.Transparent),
                        center = Offset(size.width * 0.2f, size.height * 0.2f),
                        radius = size.width * 0.8f
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(BentoLilacContainer.copy(alpha = 0.4f), Color.Transparent),
                        center = Offset(size.width * 0.8f, size.height * 0.7f),
                        radius = size.width * 0.7f
                    )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // The Campus Gate Silhouette Custom Vector Canvas - Bento style
                CampusGateSilhouette(modifier = Modifier.size(150.dp))

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Campusdeck",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-1.5).sp
                    ),
                    color = BentoTextMain,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "The Localized Digital Campus Hub",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = BentoTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BentoBorder.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                        .testTag("auth_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isRegistering) "Create Student Account" else "Unlock Campusdeck Gates",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp),
                            color = BentoTextMain,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )

                        Text(
                            text = if (isRegistering) "Register with your university credential to access your local network." else "Enter your campus-issued credentials to enter.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = BentoTextSecondary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp, bottom = 20.dp),
                            textAlign = TextAlign.Start
                        )

                        // Error Alert Banner using beautiful Bento Pink Alert style
                        AnimatedVisibility(
                            visible = errorMsg != null,
                            enter = fadeIn() + slideInVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            errorMsg?.let { msg ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = BentoPinkContainer),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp)
                                ) {
                                    Text(
                                        text = msg,
                                        color = BentoPinkContent,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }

                        // Input fields with custom Bento Outline Text Fields
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("College Email (ends in .edu)") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = BentoLavenderContent) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = if (isRegistering) ImeAction.Next else ImeAction.Done
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = BentoTextMain,
                                unfocusedTextColor = BentoTextMain,
                                focusedBorderColor = BentoLavenderContent,
                                unfocusedBorderColor = BentoBorder,
                                focusedLabelColor = BentoLavenderContent,
                                unfocusedLabelColor = BentoTextSecondary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("email_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (isRegistering) {
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Full Name") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name", tint = BentoLavenderContent) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = BentoTextMain,
                                    unfocusedTextColor = BentoTextMain,
                                    focusedBorderColor = BentoLavenderContent,
                                    unfocusedBorderColor = BentoBorder,
                                    focusedLabelColor = BentoLavenderContent,
                                    unfocusedLabelColor = BentoTextSecondary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("name_input")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = collegeName,
                                onValueChange = { collegeName = it },
                                label = { Text("University / College Name") },
                                leadingIcon = { Icon(Icons.Default.Business, contentDescription = "College", tint = BentoLavenderContent) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = BentoTextMain,
                                    unfocusedTextColor = BentoTextMain,
                                    focusedBorderColor = BentoLavenderContent,
                                    unfocusedBorderColor = BentoBorder,
                                    focusedLabelColor = BentoLavenderContent,
                                    unfocusedLabelColor = BentoTextSecondary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("college_input")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = studentId,
                                    onValueChange = { studentId = it },
                                    label = { Text("Student ID") },
                                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = "Student ID", tint = BentoLavenderContent) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = BentoTextMain,
                                        unfocusedTextColor = BentoTextMain,
                                        focusedBorderColor = BentoLavenderContent,
                                        unfocusedBorderColor = BentoBorder,
                                        focusedLabelColor = BentoLavenderContent,
                                        unfocusedLabelColor = BentoTextSecondary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("student_id_input")
                                )

                                OutlinedTextField(
                                    value = major,
                                    onValueChange = { major = it },
                                    label = { Text("Major") },
                                    leadingIcon = { Icon(Icons.Default.School, contentDescription = "Major", tint = BentoLavenderContent) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = BentoTextMain,
                                        unfocusedTextColor = BentoTextMain,
                                        focusedBorderColor = BentoLavenderContent,
                                        unfocusedBorderColor = BentoBorder,
                                        focusedLabelColor = BentoLavenderContent,
                                        unfocusedLabelColor = BentoTextSecondary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .testTag("major_input")
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        Button(
                            onClick = {
                                if (isRegistering) {
                                    viewModel.register(email, name, collegeName, studentId, major)
                                } else {
                                    viewModel.login(email)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("submit_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BentoLavenderContent,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = if (isRegistering) "Sign Up & Claim Welcome Bonus" else "Verify Domain & Enter",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isRegistering) "Already registered?" else "New student?",
                                color = BentoTextSecondary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            TextButton(
                                onClick = { viewModel.setRegistering(!isRegistering) },
                                modifier = Modifier.testTag("toggle_auth_mode")
                            ) {
                                Text(
                                    text = if (isRegistering) "Sign In instead" else "Create account",
                                    color = BentoLavenderContent,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Feature Highlights Footer - Bento Grid Style Layout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(BentoLilacContainer, shape = RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = "Secure", tint = BentoLilacContent, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("No Spammers", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = BentoTextSecondary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(BentoPinkContainer, shape = RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.School, contentDescription = "Peer Collaboration", tint = BentoPinkContent, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Peer To Peer", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = BentoTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CampusGateSilhouette(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Outer Arch
        val outerArchPath = Path().apply {
            moveTo(w * 0.15f, h * 0.95f)
            lineTo(w * 0.15f, h * 0.45f)
            cubicTo(
                w * 0.15f, h * 0.1f,
                w * 0.85f, h * 0.1f,
                w * 0.85f, h * 0.45f
            )
            lineTo(w * 0.85f, h * 0.95f)
        }

        drawPath(
            path = outerArchPath,
            color = BentoLavenderContent.copy(alpha = 0.3f),
            style = Stroke(width = 6.dp.toPx())
        )

        // Inner Arch Detail
        val innerArchPath = Path().apply {
            moveTo(w * 0.25f, h * 0.95f)
            lineTo(w * 0.25f, h * 0.52f)
            cubicTo(
                w * 0.25f, h * 0.25f,
                w * 0.75f, h * 0.25f,
                w * 0.75f, h * 0.52f
            )
            lineTo(w * 0.75f, h * 0.95f)
        }

        drawPath(
            path = innerArchPath,
            color = BentoLilacContent.copy(alpha = 0.4f),
            style = Stroke(width = 3.dp.toPx())
        )

        // Main gate pillars
        drawRoundRect(
            color = BentoLavenderContent,
            topLeft = Offset(w * 0.12f, h * 0.85f),
            size = Size(w * 0.08f, h * 0.12f),
            cornerRadius = CornerRadius(2.dp.toPx())
        )
        drawRoundRect(
            color = BentoLavenderContent,
            topLeft = Offset(w * 0.8f, h * 0.85f),
            size = Size(w * 0.08f, h * 0.12f),
            cornerRadius = CornerRadius(2.dp.toPx())
        )

        // Golden Keystone Crest at the top center of the arch
        drawCircle(
            color = BentoLilacContent,
            center = Offset(w * 0.5f, h * 0.27f),
            radius = 10.dp.toPx()
        )

        // Glowing center
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(BentoLilacContent.copy(alpha = 0.3f), Color.Transparent),
                center = Offset(w * 0.5f, h * 0.6f),
                radius = w * 0.25f
            ),
            center = Offset(w * 0.5f, h * 0.6f),
            radius = w * 0.25f
        )
    }
}
