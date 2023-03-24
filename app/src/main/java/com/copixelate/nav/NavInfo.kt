package com.copixelate.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.copixelate.R

enum class NavInfo(
    val route: String,
    val icon: ImageVector,
    val labelResId: Int,
    val contentDescriptionResId: Int,
) {
    Art(
        route = "nav-route-art",
        icon = Icons.Filled.Draw,
        labelResId = R.string.nav_label_art,
        contentDescriptionResId = R.string.nav_content_description_art
    ),
    Library(
        route = "nav-route-library",
        icon = Icons.Filled.Collections,
        labelResId = R.string.nav_label_library,
        contentDescriptionResId = R.string.nav_content_description_library
    ),
    Login(
        route = "nav-route-login",
        icon = Icons.Filled.Login,
        labelResId = R.string.nav_label_login,
        contentDescriptionResId = R.string.nav_content_description_login
    ),
    Buds(
        route = "nav-route-buds",
        icon = Icons.Filled.Contacts,
        labelResId = R.string.nav_label_buds,
        contentDescriptionResId = R.string.nav_content_description_buds
    ),
    Settings(
        route = "nav-route-settings",
        icon = Icons.Filled.Settings,
        labelResId = R.string.nav_label_settings,
        contentDescriptionResId = R.string.nav_content_description_settings
    )
}
