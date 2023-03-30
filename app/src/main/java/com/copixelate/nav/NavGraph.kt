package com.copixelate.nav

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.copixelate.ui.screens.*
import com.copixelate.viewmodel.ArtViewModel
import com.copixelate.viewmodel.NavViewModel
import com.copixelate.viewmodel.SettingsViewModel

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    navViewModel: NavViewModel,
    artViewModel: ArtViewModel = viewModel(),
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
            LibraryScreen()
        }

        composable(route = NavInfo.Login.route) {
            AuthScreen(navController, navViewModel)
        }

        composable(route = NavInfo.Buds.route) {
            BudsScreen()
        }

        composable(route = NavInfo.Settings.route) {
            SettingsScreen(
                settingsViewModel = settingsViewModel,
                navViewModel = navViewModel
            )
        }

    }
}
