package com.copixelate.ui.animation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically

object AnimationCatalog {

    // Main Content
    val screenEnter = fadeIn(animationSpec = tween(700))
    val screenExit = fadeOut(animationSpec = tween(700))

    // Library Screen
    val libraryItemExit = shrinkVertically() + fadeOut()
    val libraryItemEnter = fadeIn()
}
