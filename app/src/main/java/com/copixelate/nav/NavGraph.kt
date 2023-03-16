package com.copixelate.nav

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.copixelate.data.Auth
import com.copixelate.ui.screens.ArtScreen
import com.copixelate.ui.screens.AuthScreen
import com.copixelate.ui.screens.OverviewScreen
import com.copixelate.viewmodel.ArtViewModel

fun NavController.refresh() {
    currentDestination?.route?.let { route ->
        navigate(route) {
            popBackStack()
        }
    }
}

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    artViewModel: ArtViewModel = viewModel()
) {

    NavHost(
        navController = navController,
        startDestination = ScreenInfo.Art.route
    ) {

        composable(route = ScreenInfo.Art.route) {
            ArtScreen(viewModel = artViewModel)
        }

        composable(route = ScreenInfo.Overview.route) {
            when (Auth.state) {
                Auth.State.SIGNED_OUT -> AuthScreen(navController)
                Auth.State.SIGNED_IN -> OverviewScreen(navController)
            }
        }

    }
}
