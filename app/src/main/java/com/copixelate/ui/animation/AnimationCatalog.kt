package com.copixelate.ui.animation

import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.ui.Alignment

object AnimationCatalog {

    // Main Content
    val screenEnter = fadeIn(animationSpec = tween(700))
    val screenExit = fadeOut(animationSpec = tween(700))

    // Library Screen
    val libraryItemExit = shrinkVertically() + fadeOut()
    val libraryItemEnter = fadeIn()

    // Drawing Screen
    val toolbarEnter = expandHorizontally(expandFrom = Alignment.End) + fadeIn()
    val toolbarExit = shrinkHorizontally(shrinkTowards = Alignment.End) + fadeOut()
}
