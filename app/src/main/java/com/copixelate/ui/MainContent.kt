package com.copixelate.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.copixelate.ThemeType
import com.copixelate.nav.NavInfo
import com.copixelate.nav.SetupNavGraph
import com.copixelate.ui.theme.CopixelateTheme
import com.copixelate.viewmodel.NavViewModel
import com.copixelate.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel
) {

    val navViewModel: NavViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val themeType = settingsViewModel
        .themeTypeFlow
        .collectAsState(initial = runBlocking {
            settingsViewModel.themeTypeFlow.first()
        })
        .value

    MainTheme(themeType = themeType) {
        Scaffold(
            bottomBar = {

                MainNavBar(
                    isSignedIn = navViewModel.isSignedIn.collectAsState().value,
                    isSelected = { route ->
                        currentDestination?.hierarchy?.any { navDest ->
                            navDest.route == route
                        } == true
                    },
                    onClick = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    },
                ) // End MainNavBar

            } // End bottomBar
        ) { offsetPadding ->

            Surface(Modifier.padding(offsetPadding)) {
                SetupNavGraph(
                    navController = navController,
                    navViewModel = navViewModel,
                    settingsViewModel = settingsViewModel,
                )
            }

        } // End Scaffold
    } // End CopixelateTheme

}

@Composable
fun MainTheme(
    themeType: ThemeType,
    content: @Composable () -> Unit
) {

    Crossfade(targetState = themeType) { newTheme ->
        when (newTheme) {
            ThemeType.DARK -> CopixelateTheme(content = content, darkTheme = true)
            ThemeType.LIGHT -> CopixelateTheme(content = content, darkTheme = false)
            ThemeType.DEFAULT,
            ThemeType.UNRECOGNIZED -> CopixelateTheme(content = content)
        }
    }

}

@Composable
fun MainNavBar(
    isSignedIn: Boolean,
    isSelected: (route: String) -> Boolean,
    onClick: (route: String) -> Unit,
) {

    NavBarBuilder(
        navInfos = when (isSignedIn) {
            true -> listOf(NavInfo.Art, NavInfo.Library, NavInfo.Buds, NavInfo.Settings)
            false -> listOf(NavInfo.Art, NavInfo.Library, NavInfo.Login, NavInfo.Settings)
        },
        isSelected = isSelected,
        onClick = onClick
    )

}

@Composable
fun NavBarBuilder(
    navInfos: List<NavInfo>,
    isSelected: (route: String) -> Boolean,
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
                selected = isSelected(navInfo.route),
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

                    MainNavBar(
                        isSignedIn = true,
                        isSelected = { route ->
                            NavInfo.Art.route == route
                        },
                        onClick = {}
                    )
                }

            } // End Box
        } // End Surface
    } // End CopixelateTheme

}
