package com.example.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R

/**
 * 🌟 SINGLE SOURCE OF TRUTH FOR THE APP LOGO 🌟
 * To change or swap the logo anywhere in the app, simply update [APP_LOGO_DRAWABLE_RES]
 * below to point to your new image or drawable resource ID!
 */
@DrawableRes
val APP_LOGO_DRAWABLE_RES = R.drawable.campusdeck_logo

/**
 * Reusable App Logo component used across all screens.
 */
@Composable
fun CampusLogo(
    modifier: Modifier = Modifier,
    contentDescription: String? = "Campusdeck Logo"
) {
    Image(
        painter = painterResource(id = APP_LOGO_DRAWABLE_RES),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}
