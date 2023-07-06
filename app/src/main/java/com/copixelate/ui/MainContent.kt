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
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.copixelate.ThemeSetting
import com.copixelate.FeatureFlags
import com.copixelate.nav.NavInfo
import com.copixelate.nav.SetupNavGraph
import com.copixelate.nav.compareTopLevelRoute
import com.copixelate.nav.navigateTopLevel
import com.copixelate.ui.theme.CopixelateTheme
import com.copixelate.ui.util.PreviewSurface
import com.copixelate.viewmodel.ArtViewModel
import com.copixelate.viewmodel.LibraryViewModel
import com.copixelate.viewmodel.NavViewModel
import com.copixelate.viewmodel.SettingsViewModel

@Composable
fun MainContent(
    navController: NavHostController,
    artViewModel: ArtViewModel,
    libraryViewModel: LibraryViewModel,
    settingsViewModel: SettingsViewModel
) {

    val isCoopAvailable = FeatureFlags.IS_COOP_AVAILABLE

    val navViewModel: NavViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val themeSetting = settingsViewModel.themeSetting.collectAsState().value


    MainTheme(themeSetting = themeSetting) {
        Scaffold(
            bottomBar = {

                MainNavBar(
                    isCoopAvailable = isCoopAvailable,
                    isSignedIn = navViewModel.isSignedIn.collectAsState().value,
                    isSelected = { route ->
                        navBackStackEntry.compareTopLevelRoute(route = route)
                    },
                    onClick = { route ->
                        navController.navigateTopLevel(route = route)
                    },
                ) // End MainNavBar

            } // End bottomBar
        ) { offsetPadding ->
            Surface(Modifier.padding(offsetPadding)) {

                SetupNavGraph(
                    navController = navController,
                    navViewModel = navViewModel,
                    artViewModel = artViewModel,
                    libraryViewModel = libraryViewModel,
                    settingsViewModel = settingsViewModel,
                )

            }
        } // End Scaffold
    } // End CopixelateTheme

}

@Composable
fun MainTheme(
    themeSetting: ThemeSetting,
    content: @Composable () -> Unit
) {

    Crossfade(targetState = themeSetting) { newSetting ->
        when (newSetting) {
            ThemeSetting.DARK -> CopixelateTheme(content = content, darkTheme = true)
            ThemeSetting.LIGHT -> CopixelateTheme(content = content, darkTheme = false)
            ThemeSetting.DEFAULT,
            ThemeSetting.UNRECOGNIZED -> CopixelateTheme(content = content)
        }
    }

}

@Composable
fun MainNavBar(
    isCoopAvailable: Boolean,
    isSignedIn: Boolean,
    isSelected: (route: String) -> Boolean,
    onClick: (route: String) -> Unit,
) {

    val baseMenu = mutableListOf(
        NavInfo.Art.Root,
        NavInfo.Library.Root,
        NavInfo.Settings.Root
    )

    val navInfos =
        when (isCoopAvailable) {
            false -> baseMenu

            true -> baseMenu
                .apply {
                    when (isSignedIn) {
                        false -> add(index = 2, element = NavInfo.Login.Root)
                        true -> add(index = 2, element = NavInfo.Contacts.Root)
                    }
                }
        }

    NavBarBuilder(
        navInfos = navInfos,
        isSelected = isSelected,
        onClick = onClick
    )

}

@Composable
fun NavBarBuilder(
    navInfos: List<NavInfo.Screen>,
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

    PreviewSurface {
        Box {
            Surface(modifier = Modifier.align(Alignment.BottomCenter)) {

                MainNavBar(
                    isCoopAvailable = false,
                    isSignedIn = true,
                    isSelected = { route ->
                        NavInfo.Art.Root.route == route
                    },
                    onClick = {}
                )
            }

        } // End Box
    } // End PreviewSurface

}
