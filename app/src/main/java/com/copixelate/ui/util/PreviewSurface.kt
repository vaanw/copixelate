package com.copixelate.ui.util

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.copixelate.ui.theme.CopixelateTheme

@Composable
fun PreviewSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    CopixelateTheme(darkTheme = true) {
        Surface(modifier = modifier) {
            content()
        }
    }

}
