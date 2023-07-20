package com.copixelate.ui.util

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.copixelate.ui.theme.CopixelateTheme

@Composable
fun PreviewSurface(
    modifier: Modifier = Modifier,
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {

    CopixelateTheme(darkTheme = darkTheme) {
        Surface(modifier = modifier) {
            content()
        }
    }

}
