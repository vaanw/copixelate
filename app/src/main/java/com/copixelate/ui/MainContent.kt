package com.copixelate.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.copixelate.nav.NavInfo
import com.copixelate.nav.SetupNavGraph
import com.copixelate.ui.theme.CopixelateTheme
import com.copixelate.viewmodel.NavViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(navController: NavHostController) {

    val navViewModel: NavViewModel = viewModel()

    CopixelateTheme {
        Scaffold(
            bottomBar = {
                MainNavBar(
                    navController = navController,
                    isSignedIn = navViewModel.isSignedIn.collectAsState().value,
                )
            }
        ) { offsetPadding ->
            // Account for bottom nav bar size
            Surface(Modifier.padding(offsetPadding)) {
                SetupNavGraph(
                    navController = navController,
                    navViewModel = navViewModel
                )
            }
        }

    }

}

@Composable
fun MainNavBar(
    navController: NavHostController,
    isSignedIn: Boolean
) {

    NavBarBuilder(
        navController = navController,
        navInfos = when (isSignedIn) {
            true -> listOf(NavInfo.Art, NavInfo.Library, NavInfo.Buds, NavInfo.Settings)
            false -> listOf(NavInfo.Art, NavInfo.Library, NavInfo.Login, NavInfo.Settings)
        }
    )

}

@Composable
fun NavBarBuilder(
    navController: NavHostController,
    navInfos: List<NavInfo>
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavBarBuilder(
        navInfos = navInfos,
        onSelected = { route ->
            currentDestination?.hierarchy?.any { navDest ->
                navDest.route == route
            } == true
        },
        onClick = { route ->
            navController.navigate(route) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id)
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
            }
        }
    )

}

@Composable
fun NavBarBuilder(
    navInfos: List<NavInfo>,
    onSelected: (route: String) -> Boolean,
    onClick: (route: String) -> Unit,
) {

    NavigationBar {
        navInfos.forEach { navInfo ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = navInfo.icon,
                        contentDescription = stringResource(navInfo.contentDescriptionResId)
                    )
                },
                label = { Text(stringResource(navInfo.labelResId)) },
                selected = onSelected(navInfo.route),
                onClick = { onClick(navInfo.route) }
            ) // End NavigationBarItem
        } // End navBarItems.forEach
    } // End NavigationBar

}

@Preview
@Composable
fun NavBarPreview() {

    CopixelateTheme(darkTheme = true) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box {
                Surface(modifier = Modifier.align(Alignment.BottomCenter)) {

                    NavBarBuilder(
                        navInfos = listOf(NavInfo.Art, NavInfo.Library, NavInfo.Login),
                        onSelected = { false },
                        onClick = {}
                    )
                }

            } // End Box
        } // End Surface
    } // End CopixelateTheme

}
