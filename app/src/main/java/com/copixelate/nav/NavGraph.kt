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
import com.copixelate.viewmodel.NavViewModel

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
    navViewModel: NavViewModel,
    artViewModel: ArtViewModel = viewModel()
) {

    NavHost(
        navController = navController,
        startDestination = NavInfo.Art.route
    ) {

        composable(route = NavInfo.Art.route) {
            ArtScreen(viewModel = artViewModel)
        }

        composable(route = NavInfo.Library.route) {

        }

        composable(route = NavInfo.Login.route) {
            when (Auth.state) {
                Auth.State.SIGNED_OUT -> AuthScreen(navController, navViewModel)
                Auth.State.SIGNED_IN -> {}
            }
        }

        composable(route = NavInfo.Buds.route) {
            OverviewScreen(navController)
        }

    }
}
