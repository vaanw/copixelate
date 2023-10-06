package com.copixelate.ui.util

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


/**
 * A [Composable] providing a [Surface] container which fills all available space,
 * used to maintain consistent screen transition animations.
 *
 * @param content The [Composable] screen content to be displayed.
 */
@Composable
fun ScreenSurface(
    content: @Composable () -> Unit
) {

    Surface(
        modifier = Modifier
            // Needed to maintain consistent screen transition animations
            .fillMaxSize()
    ) {
        content()
    }

}
