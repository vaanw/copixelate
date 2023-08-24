package com.copixelate.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.copixelate.ui.nav.NavInfo
import com.copixelate.ui.screens.AuthScreen
import com.copixelate.ui.screens.SettingsScreen
import com.copixelate.ui.screens.art.ArtScreen
import com.copixelate.ui.screens.contacts.AddContactsScreen
import com.copixelate.ui.screens.contacts.ContactsScreen
import com.copixelate.ui.screens.library.LibraryScreen
import com.copixelate.viewmodel.ArtViewModel
import com.copixelate.viewmodel.LibraryViewModel
import com.copixelate.viewmodel.NavViewModel
import com.copixelate.viewmodel.SettingsViewModel

@Composable
fun SetupMainNavGraph(
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
            ArtScreen(artViewModel = artViewModel)
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

        navigation(
            route = NavInfo.Contacts.route,
            startDestination = NavInfo.Contacts.Start.route
        ) {
            composable(route = NavInfo.Contacts.Start.route) {
                ContactsScreen(navController = navController)
            }
            composable(route = NavInfo.Contacts.Add.route) {
                AddContactsScreen(navController = navController)
            }
        }


        composable(route = NavInfo.Settings.route) {
            SettingsScreen(
                settingsViewModel = settingsViewModel,
                navViewModel = navViewModel
            )
        }

    }
}
