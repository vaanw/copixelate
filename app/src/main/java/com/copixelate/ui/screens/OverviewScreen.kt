package com.copixelate.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.copixelate.data.Auth
import com.copixelate.nav.refresh

@Composable
fun OverviewScreen(navController: NavController) {

    OverviewScreenContent(
        clickMessageThread = { }, //navController.navigate(ScreenInfo.???.route) },
        onLogoutClick = {
            Auth.signOut()
            navController.refresh()
        },
        username = Auth.displayName
    )
}

@Composable
fun OverviewScreenContent(
    clickMessageThread: () -> Unit,
    onLogoutClick: () -> Unit,
    username: String?
) {
    Column {
        Text(text = "Hello, $username")

        // Button for advancing to the message thread for the specified user.
        Button(
            onClick = { clickMessageThread() },
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(text = "Message 'Username'")
        }

        // Button for logging the user out.
        Button(
            onClick = { onLogoutClick() },
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(text = "Logout")
        }
    }
}
