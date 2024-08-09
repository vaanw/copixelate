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

    object Friend : TopScreen(
        route = "nav-route-friend",
        icon = IconCatalog.friend,
        labelResId = R.string.nav_label_friend,
        contentDescriptionResId = R.string.nav_content_description_friend
    ) {
        object Start: SubScreen(
            route = "nav-route-friend-start",
            contentDescriptionResId = Friend.contentDescriptionResId
        )

        object Add: SubScreen(
            route = "nav-route-friend-add",
            contentDescriptionResId = R.string.nav_content_description_friend_add
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
