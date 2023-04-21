package com.copixelate.ui.util

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.copixelate.ui.theme.CopixelateTheme

@Composable
fun PreviewSurface(
    content: @Composable () -> Unit
) {

    CopixelateTheme(darkTheme = true) {
        Surface(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }

}
