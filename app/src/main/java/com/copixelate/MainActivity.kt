package com.copixelate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.copixelate.ui.MainContent
import com.copixelate.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            navController = rememberNavController()

            val settingsViewModel: SettingsViewModel by viewModels { SettingsViewModel.Factory }

            MainContent(
                navController = navController,
                settingsViewModel = settingsViewModel
            )

        }

    }

}
