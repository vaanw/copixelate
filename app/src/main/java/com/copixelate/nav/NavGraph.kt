package com.copixelate.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.copixelate.ui.screens.*
import com.copixelate.viewmodel.ArtViewModel
import com.copixelate.viewmodel.LibraryViewModel
import com.copixelate.viewmodel.NavViewModel
import com.copixelate.viewmodel.SettingsViewModel

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    navViewModel: NavViewModel,
    artViewModel: ArtViewModel,
    libraryViewModel: LibraryViewModel,
    settingsViewModel: SettingsViewModel,
) {

    NavHost(
        navController = navController,
        startDestination = NavInfo.Art.route
    ) {

        composable(route = NavInfo.Art.route) {
            ArtScreen(viewModel = artViewModel)
        }

        composable(route = NavInfo.Library.route) {
            LibraryScreen(
                navController = navController,
                libraryViewModel = libraryViewModel
            )
        }

        composable(route = NavInfo.Login.route) {
            AuthScreen(navController, navViewModel)
        }

        composable(route = NavInfo.Contacts.route) {
            ContactsScreen()
        }

        composable(route = NavInfo.Settings.route) {
            SettingsScreen(
                settingsViewModel = settingsViewModel,
                navViewModel = navViewModel
            )
        }

    }
}
