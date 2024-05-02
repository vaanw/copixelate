package com.copixelate.ui.nav

import androidx.compose.ui.graphics.vector.ImageVector
import com.copixelate.R
import com.copixelate.ui.icon.IconCatalog

object NavInfo {

    object Art : TopScreen(
        route = "nav-route-art",
        icon = IconCatalog.draw,
        labelResId = R.string.nav_label_art,
        contentDescriptionResId = R.string.nav_content_description_art
    )

    object Library : TopScreen(
        route = "nav-route-library",
        icon = IconCatalog.collections,
        labelResId = R.string.nav_label_library,
        contentDescriptionResId = R.string.nav_content_description_library
    )

    object Login : TopScreen(
        route = "nav-route-login",
        icon = IconCatalog.login,
        labelResId = R.string.nav_label_login,
        contentDescriptionResId = R.string.nav_content_description_login
    )

    object Settings : TopScreen(
        route = "nav-route-settings",
        icon = IconCatalog.settings,
        labelResId = R.string.nav_label_settings,
        contentDescriptionResId = R.string.nav_content_description_settings
    )

    object Contacts : TopScreen(
        route = "nav-route-contacts",
        icon = IconCatalog.contacts,
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
