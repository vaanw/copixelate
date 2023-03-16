package com.copixelate.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.copixelate.nav.ScreenInfo
import com.copixelate.nav.SetupNavGraph
import com.copixelate.ui.theme.CopixelateTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(navController: NavHostController) {

    CopixelateTheme {

        Scaffold(
            bottomBar = { BottomBar(navController) }
        ) { offsetPadding ->
            //Account for bottom nav bar size
            Surface(Modifier.padding(offsetPadding)) {
                SetupNavGraph(navController = navController)
            }
        }

    }

}

@Composable
fun BottomBar(navController: NavHostController) {

    val navBarItems = listOf(
        ScreenInfo.Art,
        ScreenInfo.Overview
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        navBarItems.forEach { screenInfo ->
            NavigationBarItem(
                icon = {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = "TBD icon"
                    )
                },
                selected = currentDestination?.hierarchy?.any { it.route == screenInfo.route } == true,
                onClick = {
                    navController.navigate(screenInfo.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )// End NavigationBarItem
        }// End navBarItems.forEach
    }// End NavigationBar

}
