package com.copixelate.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.copixelate.data.Auth
import com.copixelate.viewmodel.NavViewModel

@Composable
fun SettingsScreen(navViewModel: NavViewModel) {

    Column {
        Text(text = "Hello, ${Auth.displayName}")

        Button(
            onClick = {
                Auth.signOut()
                navViewModel.setSignedOut()
            },
        ) {
            Text(text = "Logout")
        }
    }

}
