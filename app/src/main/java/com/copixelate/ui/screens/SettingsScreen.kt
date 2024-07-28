package com.copixelate.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.copixelate.ThemeSetting
import com.copixelate.data.model.AuthStatus
import com.copixelate.ui.animation.AnimationCatalog
import com.copixelate.ui.util.PreviewSurface
import com.copixelate.ui.util.ScreenSurface
import com.copixelate.viewmodel.SettingsViewModel
import com.copixelate.viewmodel.UserViewModel

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    userViewModel: UserViewModel
) {

    val themeSetting = settingsViewModel
        .themeSetting
        .collectAsState()
        .value

    val auth = userViewModel.auth.collectAsState().value

    ScreenSurface {
        SettingsScreenContent(
            themeSetting = themeSetting,
            onSelectTheme = { newTheme ->
                settingsViewModel.saveThemeSetting(newTheme)
            },
            displayName = auth.displayName,
            signedIn = auth.authStatus is AuthStatus.SignedIn,
            onClickLogout = {
                userViewModel.signOut()
            }
        )
    }

} // End SettingsScreen

@Composable
fun SettingsScreenContent(
    themeSetting: ThemeSetting,
    onSelectTheme: (ThemeSetting) -> Unit,
    displayName: String,
    signedIn: Boolean,
    onClickLogout: () -> Unit
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier
            .padding(24.dp)
    ) {

        Text(
            text = "Settings",
            style = MaterialTheme.typography.displaySmall,
        )

        // Appearance Section
        Text(
            text = "Appearance",
            style = MaterialTheme.typography.headlineSmall
        )

        ThemePicker(
            themeSetting = themeSetting,
            onSelectTheme = onSelectTheme
        )

        // Account Section
        AnimatedVisibility(
            visible = signedIn,
            enter = AnimationCatalog.settingsSectionEnter,
            exit = AnimationCatalog.settingsSectionExit
        ) {

            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Text(
                    text = "Account",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(text = "Display Name: $displayName")

                Button(
                    onClick = onClickLogout,
                ) {
                    Text(text = "Logout")
                }
            }

        }

    } // End Column

}

@Composable
fun ThemePicker(
    themeSetting: ThemeSetting,
    onSelectTheme: (ThemeSetting) -> Unit,
) {

    Column(Modifier.selectableGroup()) {

        Text(
            text = "Theme",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        SettingsRadioButton(
            text = "System Default",
            selected = themeSetting == ThemeSetting.DEFAULT,
            onClick = { onSelectTheme(ThemeSetting.DEFAULT) }
        )
        SettingsRadioButton(
            text = "Dark",
            selected = themeSetting == ThemeSetting.DARK,
            onClick = { onSelectTheme(ThemeSetting.DARK) }
        )
        SettingsRadioButton(
            text = "Light",
            selected = themeSetting == ThemeSetting.LIGHT,
            onClick = { onSelectTheme(ThemeSetting.LIGHT) }
        )

    }

}

@Composable
fun SettingsRadioButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    val interactionSource = remember { MutableInteractionSource() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            )
    ) {

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        RadioButton(
            selected = selected,
            onClick = onClick,
            interactionSource = interactionSource,
        )
    }

}

@Preview
@Composable
fun SettingsScreenPreview() {

    val themeSetting = ThemeSetting.DEFAULT
    PreviewSurface {
        ScreenSurface {
            SettingsScreenContent(
                themeSetting = themeSetting,
                onSelectTheme = {},
                displayName = "Preview-Name",
                signedIn = true,
                onClickLogout = {}
            )
        }
    }

}
