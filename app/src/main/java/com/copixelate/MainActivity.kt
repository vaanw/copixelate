package com.copixelate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.copixelate.data.proto.settingsDataStore
import com.copixelate.data.proto.uiStateDataStore
import com.copixelate.data.repo.ArtRepo
import com.copixelate.data.repo.SettingsRepo
import com.copixelate.data.repo.UiRepo
import com.copixelate.ui.MainContent
import com.copixelate.viewmodel.ArtViewModel
import com.copixelate.viewmodel.LibraryViewModel
import com.copixelate.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ArtRepo.init(this)
        UiRepo.init(applicationContext.uiStateDataStore)
        SettingsRepo.init(applicationContext.settingsDataStore)

        val artViewModel: ArtViewModel by viewModels()
        val libraryViewModel: LibraryViewModel by viewModels()
        val settingsViewModel: SettingsViewModel by viewModels()

        setContent {

            MainContent(
                navController = rememberNavController(),
                artViewModel = artViewModel,
                libraryViewModel = libraryViewModel,
                settingsViewModel = settingsViewModel
            )

        }

    }

}
