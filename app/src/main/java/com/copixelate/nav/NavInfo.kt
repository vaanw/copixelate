package com.copixelate.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.copixelate.R

object NavInfo {

    data class Screen(
        val route: String,
        val icon: ImageVector,
        val labelResId: Int,
        val contentDescriptionResId: Int
    )

    object Art {
        val Root = Screen(
            route = "nav-route-art",
            icon = Icons.Filled.Draw,
            labelResId = R.string.nav_label_art,
            contentDescriptionResId = R.string.nav_content_description_art
        )
    }

    object Library {
        val Root = Screen(
            route = "nav-route-library",
            icon = Icons.Filled.Collections,
            labelResId = R.string.nav_label_library,
            contentDescriptionResId = R.string.nav_content_description_library
        )
    }

    object Login {
        val Root = Screen(
            route = "nav-route-login",
            icon = Icons.Filled.Login,
            labelResId = R.string.nav_label_login,
            contentDescriptionResId = R.string.nav_content_description_login
        )
    }

    object Settings {
        val Root = Screen(
            route = "nav-route-settings",
            icon = Icons.Filled.Settings,
            labelResId = R.string.nav_label_settings,
            contentDescriptionResId = R.string.nav_content_description_settings
        )
    }

    object Contacts {
        val Root = Screen(
            route = "nav-route-contacts",
            icon = Icons.Filled.Contacts,
            labelResId = R.string.nav_label_contacts,
            contentDescriptionResId = R.string.nav_content_description_contacts
        )
        val Start = Screen(
            route = "nav-route-contacts-main",
            icon = Root.icon,
            labelResId = Root.labelResId,
            contentDescriptionResId = Root.contentDescriptionResId
        )
        val Add = Screen(
            route = "nav-route-contacts-add",
            icon = Root.icon,
            labelResId = R.string.nav_label_contacts_add,
            contentDescriptionResId = R.string.nav_content_description_contacts_add
        )
    }

}
