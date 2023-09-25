package com.copixelate.ui.screens.art

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize


/**
 * A composable providing gestures to transform (pan and zoom) content.
 * The user can double-tap to revert all transformations.
 *
 * @param enabled Determines if the transform tool will respond to user input.
 * @param modifier The [Modifier] applied to the outermost component.
 * @param content The composable content to be transformed, typically a [Drawing].
 * The transformation is received as a [Modifier].
 */
@Composable
fun TransformTool(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit
) {

    var viewSize by remember { mutableStateOf(IntSize(0, 0)) }

    var revert by remember { mutableStateOf(false) }

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    if (revert) {
        var targetScale by remember { mutableFloatStateOf(scale) }
        val animatedScale: Float by animateFloatAsState(
            targetValue = targetScale,
            label = "TransformScaleAnimation"
        )
        targetScale = 1f

        var targetOffset by remember { mutableStateOf(offset) }
        val animatedOffset: Offset by animateOffsetAsState(
            targetValue = targetOffset,
            label = "TransformOffsetAnimation"
        )
        targetOffset = Offset.Zero

        scale = animatedScale
        offset = animatedOffset

        // End revert if complete
        if (scale == 1f
            && offset == Offset.Zero
        ) revert = false
    }

    Box(
        modifier = when (enabled) {
            false -> modifier
            true -> modifier
                .onGloballyPositioned { layoutCoordinates ->
                    viewSize = layoutCoordinates.size
                }
                // Pan and zoom gestures
                .pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        val inputOffset = centroid - viewSize / 2
                        val panCorrection = (offset - inputOffset) * (zoom - 1)
                        offset += pan + panCorrection
                        scale *= zoom
                    }
                }
                // Double-tap to revert transform
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { _ ->
                            revert = true
                        })
                }
        } // end when
    ) {

        content(
            Modifier
                // Transform content
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
        )

    } // End Box

} // End TransformTool

private operator fun Offset.minus(intSize: IntSize): Offset =
    Offset(
        x = x - intSize.width,
        y = y - intSize.height
    )
