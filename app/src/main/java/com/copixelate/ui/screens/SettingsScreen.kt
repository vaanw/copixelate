package com.copixelate.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import com.copixelate.data.Auth
import com.copixelate.ui.util.PreviewSurface
import com.copixelate.viewmodel.NavViewModel
import com.copixelate.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    navViewModel: NavViewModel
) {

    val themeSetting = settingsViewModel
        .themeSetting
        .collectAsState()
        .value

    SettingsScreenContent(
        themeSetting = themeSetting,
        onSelectTheme = { newTheme ->
            settingsViewModel.saveThemeSetting(newTheme)
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
    themeSetting: ThemeSetting,
    onSelectTheme: (ThemeSetting) -> Unit,
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
            themeSetting = themeSetting,
            onSelectTheme = onSelectTheme
        )

    }

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
        SettingsScreenContent(
            themeSetting = themeSetting,
            onSelectTheme = {},
            displayName = "Preview-Name",
            onClickLogout = {}
        )
    }
}
