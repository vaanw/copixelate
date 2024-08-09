package com.copixelate.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.copixelate.FeatureFlags
import com.copixelate.ThemeSetting
import com.copixelate.data.model.AuthStatus
import com.copixelate.ui.nav.NavInfo
import com.copixelate.ui.nav.compareTopLevelRoute
import com.copixelate.ui.nav.navigateTopLevel
import com.copixelate.ui.theme.CopixelateTheme
import com.copixelate.ui.util.PreviewSurface
import com.copixelate.viewmodel.ActivityViewModel
import com.copixelate.viewmodel.SettingsViewModel
import com.copixelate.viewmodel.UserViewModel

@Composable
fun MainContent(
    navController: NavHostController,
    activityViewModel: ActivityViewModel,
    userViewModel: UserViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {

    val isCoopAvailable = FeatureFlags.IS_COOP_AVAILABLE

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val isSignedIn = userViewModel.auth.collectAsState().value.authStatus is AuthStatus.SignedIn

    val themeSetting = settingsViewModel.themeSetting.collectAsState().value


    MainTheme(themeSetting = themeSetting) {
        Scaffold(
            bottomBar = {

                MainNavBar(
                    isCoopAvailable = isCoopAvailable,
                    isSignedIn = isSignedIn,
                    isSelected = { navInfo ->
                        navBackStackEntry.compareTopLevelRoute(navInfo = navInfo)
                    },
                    onClick = { navInfo ->
                        navController.navigateTopLevel(navInfo = navInfo)
                    },
                ) // End MainNavBar

            } // End bottomBar
        ) { offsetPadding ->
            Surface(Modifier.padding(offsetPadding)) {

                SetupMainNavGraph(
                    navController = navController,
                    activityViewModel = activityViewModel,
                    userViewModel = userViewModel,
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
    isSelected: (screen: NavInfo.Screen) -> Boolean,
    onClick: (screen: NavInfo.Screen) -> Unit,
) {

    val baseMenu = mutableListOf(
        NavInfo.Art,
        NavInfo.Library,
        NavInfo.Settings
    )

    val navInfos =
        when (isCoopAvailable) {
            false -> baseMenu

            true -> baseMenu
                .apply {
                    when (isSignedIn) {
                        false -> add(index = 2, element = NavInfo.Login)
                        true -> add(index = 2, element = NavInfo.Friend)
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
    navInfos: List<NavInfo.TopScreen>,
    isSelected: (navInfo: NavInfo.Screen) -> Boolean,
    onClick: (navInfo: NavInfo.Screen) -> Unit,
) {

    Surface(
        color = NavigationBarDefaults.containerColor,
        tonalElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {

        NavigationBar(
            containerColor = Color.Transparent,
            modifier = Modifier
                .height(48.dp)
                .padding(horizontal = 16.dp)
        ) {
            navInfos.forEach { navInfo ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = navInfo.icon,
                            contentDescription = stringResource(navInfo.contentDescriptionResId)
                        )
                    },
                    selected = isSelected(navInfo),
                    onClick = { onClick(navInfo) }
                ) // End NavigationBarItem
            } // End navBarItems.forEach
        } // End NavigationBar

    }


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
                    isSelected = { navInfo ->
                        NavInfo.Art.route == navInfo.route
                    },
                    onClick = {}
                )
            }

        } // End Box
    } // End PreviewSurface

}
