package com.copixelate.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.copixelate.ThemeType
import com.copixelate.data.Auth
import com.copixelate.ui.theme.CopixelateTheme
import com.copixelate.viewmodel.NavViewModel
import com.copixelate.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    navViewModel: NavViewModel
) {

    val themeType = settingsViewModel
        .settings
        .collectAsState(initial = ThemeType.DEFAULT)
        .value

    SettingsScreenContent(
        themeType = themeType,
        onSelectTheme = { newTheme ->
            settingsViewModel.setTheme(newTheme)
        },
        displayName = Auth.displayName,
        onClickLogout = {
            Auth.signOut()
            navViewModel.setSignedOut()
        }
    )
}

@Composable
fun SettingsScreenContent(
    themeType: ThemeType,
    onSelectTheme: (ThemeType) -> Unit,
    displayName: String,
    onClickLogout: () -> Unit
) {

    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        Text(
            text = "Settings",
            style = MaterialTheme.typography.displaySmall,
        )

        Text(text = "Display Name: $displayName")

        Button(
            onClick = onClickLogout,
        ) {
            Text(text = "Logout")
        }

        ThemePicker(
            themeType = themeType,
            onSelectTheme = onSelectTheme
        )

    }

}

@Composable
fun ThemePicker(
    themeType: ThemeType,
    onSelectTheme: (ThemeType) -> Unit,
) {

    Column(Modifier.selectableGroup()) {

        Text(
            text = "Theme",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        SettingsRadioButton(
            text = "System Default",
            selected = themeType == ThemeType.DEFAULT,
            onClick = { onSelectTheme(ThemeType.DEFAULT) }
        )
        SettingsRadioButton(
            text = "Dark",
            selected = themeType == ThemeType.DARK,
            onClick = { onSelectTheme(ThemeType.DARK) }
        )
        SettingsRadioButton(
            text = "Light",
            selected = themeType == ThemeType.LIGHT,
            onClick = { onSelectTheme(ThemeType.LIGHT) }
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

    CopixelateTheme(darkTheme = true) {
        Surface(modifier = Modifier.fillMaxSize()) {
            SettingsScreenContent(
                displayName = "xXCoolUserXx",
                onClickLogout = {},
                onSelectTheme = {},
                themeType = ThemeType.DARK
            )
        }
    }

}
