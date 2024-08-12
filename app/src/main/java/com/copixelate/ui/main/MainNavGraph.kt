package com.copixelate.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.copixelate.ui.animation.AnimationCatalog.screenEnter
import com.copixelate.ui.animation.AnimationCatalog.screenExit
import com.copixelate.ui.nav.NavInfo
import com.copixelate.ui.screens.AuthScreen
import com.copixelate.ui.screens.SettingsScreen
import com.copixelate.ui.screens.art.ArtScreen
import com.copixelate.ui.screens.friend.AddFriendScreen
import com.copixelate.ui.screens.friend.FriendScreen
import com.copixelate.ui.screens.library.LibraryScreen
import com.copixelate.viewmodel.ActivityViewModel
import com.copixelate.viewmodel.ArtViewModel
import com.copixelate.viewmodel.LibraryViewModel
import com.copixelate.viewmodel.SettingsViewModel
import com.copixelate.viewmodel.UserViewModel

@Composable
fun SetupMainNavGraph(
    navController: NavHostController,
    activityViewModel: ActivityViewModel,
    userViewModel: UserViewModel,
    settingsViewModel: SettingsViewModel,
    artViewModel: ArtViewModel = viewModel(),
    libraryViewModel: LibraryViewModel = viewModel()
) {

    NavHost(
        navController = navController,
        startDestination = NavInfo.Art.route,
        enterTransition = { screenEnter },
        exitTransition = { screenExit },
        modifier = Modifier
            .fillMaxSize()
    ) {

        composable(route = NavInfo.Art.route) {
            ArtScreen(artViewModel = artViewModel)
        }

        composable(route = NavInfo.Library.route) {
            LibraryScreen(
                navController = navController,
                libraryViewModel = libraryViewModel,
                activityViewModel = activityViewModel
            )
        }

        composable(route = NavInfo.Login.route) {
            AuthScreen(navController, userViewModel)
        }

        navigation(
            route = NavInfo.Friend.route,
            startDestination = NavInfo.Friend.Start.route
        ) {
            composable(route = NavInfo.Friend.Start.route) {
                FriendScreen(navController = navController)
            }
            composable(route = NavInfo.Friend.Add.route) {
                AddFriendScreen(navController = navController)
            }
        }

        composable(route = NavInfo.Settings.route) {
            SettingsScreen(
                settingsViewModel = settingsViewModel,
                userViewModel = userViewModel
            )
        }

    }
}
