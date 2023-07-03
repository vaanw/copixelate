package com.copixelate.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.copixelate.ui.screens.*
import com.copixelate.ui.screens.contacts.AddContactsScreen
import com.copixelate.ui.screens.contacts.ContactsScreen
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
        startDestination = NavInfo.Art.Root.route
    ) {

        composable(route = NavInfo.Art.Root.route) {
            ArtScreen(viewModel = artViewModel)
        }

        composable(route = NavInfo.Library.Root.route) {
            LibraryScreen(
                navController = navController,
                libraryViewModel = libraryViewModel
            )
        }

        composable(route = NavInfo.Login.Root.route) {
            AuthScreen(navController, navViewModel)
        }

        navigation(
            route = NavInfo.Contacts.Root.route,
            startDestination = NavInfo.Contacts.Start.route
        ) {
            composable(route = NavInfo.Contacts.Start.route) {
                ContactsScreen(navController = navController)
            }
            composable(route = NavInfo.Contacts.Add.route) {
                AddContactsScreen(navController = navController)
            }
        }


        composable(route = NavInfo.Settings.Root.route) {
            SettingsScreen(
                settingsViewModel = settingsViewModel,
                navViewModel = navViewModel
            )
        }

    }
}
