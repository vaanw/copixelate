package com.copixelate.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.copixelate.R

object NavInfo {

    object Art : TopScreen(
        route = "nav-route-art",
        icon = Icons.Filled.Draw,
        labelResId = R.string.nav_label_art,
        contentDescriptionResId = R.string.nav_content_description_art
    )

    object Library : TopScreen(
        route = "nav-route-library",
        icon = Icons.Filled.Collections,
        labelResId = R.string.nav_label_library,
        contentDescriptionResId = R.string.nav_content_description_library
    )

    object Login : TopScreen(
        route = "nav-route-login",
        icon = Icons.Filled.Login,
        labelResId = R.string.nav_label_login,
        contentDescriptionResId = R.string.nav_content_description_login
    )

    object Settings : TopScreen(
        route = "nav-route-settings",
        icon = Icons.Filled.Settings,
        labelResId = R.string.nav_label_settings,
        contentDescriptionResId = R.string.nav_content_description_settings
    )

    object Contacts : TopScreen(
        route = "nav-route-contacts",
        icon = Icons.Filled.Contacts,
        labelResId = R.string.nav_label_contacts,
        contentDescriptionResId = R.string.nav_content_description_contacts
    ) {
        object Start: SubScreen(
            route = "nav-route-contacts-start",
            contentDescriptionResId = Contacts.contentDescriptionResId
        )

        object Add: SubScreen(
            route = "nav-route-contacts-add",
            contentDescriptionResId = R.string.nav_content_description_contacts_add
        )
    }


    sealed class Screen(
        val route: String
    )

    sealed class SubScreen(
        route: String,
        val contentDescriptionResId: Int
    ) : Screen(route = route)

    sealed class TopScreen(
        route: String,
        val icon: ImageVector,
        val labelResId: Int,
        val contentDescriptionResId: Int
    ) : Screen(route = route)

}
