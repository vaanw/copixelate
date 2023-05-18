package com.copixelate.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Int.toDp(): Dp =
    let { value: Int ->
        LocalDensity.current.run {
            value.toDp()
        }
    }
