package com.copixelate

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.copixelate.ui.MainContent
import com.copixelate.ui.util.createShareImageIntent
import com.copixelate.viewmodel.ActivityViewModel
import com.copixelate.viewmodel.ActivityViewModel.UiEvent
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

        setContent {
            MainContent(
                navController = rememberNavController(),
                activityViewModel = activityViewModel,
            )
        }

    } // end OnCreate

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
        startActivity(
            createShareImageIntent(uri)
        )
    }

}
