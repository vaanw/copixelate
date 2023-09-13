package com.copixelate

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.copixelate.data.proto.settingsDataStore
import com.copixelate.data.proto.uiStateDataStore
import com.copixelate.data.repo.ArtRepo
import com.copixelate.data.repo.SettingsRepo
import com.copixelate.data.repo.UiRepo
import com.copixelate.ui.MainContent
import com.copixelate.viewmodel.ActivityViewModel
import com.copixelate.viewmodel.ActivityViewModel.UiEvent
import com.copixelate.viewmodel.ArtViewModel
import com.copixelate.viewmodel.LibraryViewModel
import com.copixelate.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityViewModel: ActivityViewModel by viewModels()

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                handleUiEvents(activityViewModel.events)
            }
        }

        ArtRepo.init(this)
        UiRepo.init(applicationContext.uiStateDataStore)
        SettingsRepo.init(applicationContext.settingsDataStore)

        val artViewModel: ArtViewModel by viewModels()
        val libraryViewModel: LibraryViewModel by viewModels()
        val settingsViewModel: SettingsViewModel by viewModels()

        setContent {

            MainContent(
                navController = rememberNavController(),
                activityViewModel = activityViewModel,
                artViewModel = artViewModel,
                libraryViewModel = libraryViewModel,
                settingsViewModel = settingsViewModel
            )

        }

    }

    private suspend fun handleUiEvents(events: SharedFlow<UiEvent>) {
        events.collect { event ->
            when (event) {
                is UiEvent.ShareImage -> {
                    shareImage(event.uri)
                }
            }
        }
    }

    private fun shareImage(uri: Uri) {
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/png"
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            clipData = ClipData.newRawUri("", uri)
        }
        startActivity(Intent.createChooser(shareIntent, ""))
    }

}
