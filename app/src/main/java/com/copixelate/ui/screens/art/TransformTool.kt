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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize

private const val DEFAULT_SCALE = 1f
private val DEFAULT_OFFSET = Offset.Zero

data class TransformState(
    var scale: Float = DEFAULT_SCALE,
    var offset: Offset = DEFAULT_OFFSET
)

/**
 * A composable providing gestures to transform (pan and zoom) content,
 * and double-tap to revert all transformations.
 * Transformation state is persisted locally to improve performance.
 *
 * @param initialState [TransformState] used upon first composition.
 * @param onStateChange Callback providing the latest [TransformState].
 * @param enabled Determines if the transform tool will respond to user input.
 * @param modifier [Modifier] applied to the outermost component.
 * @param content Composable content to be transformed, typically a [Drawing].
 * The transformation is received as a [Modifier].
 */
@Composable
fun TransformTool(
    initialState: TransformState,
    onStateChange: (TransformState) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable (TransformState) -> Unit
) {

    var state by remember { mutableStateOf(initialState) }

    fun updateState(scale: Float, offset: Offset) {
        state = state.copy(
            scale = scale,
            offset = offset
        )
        onStateChange(state)
    }

    var revert by remember { mutableStateOf(false) }

    @Composable
    fun AnimateTransformationReversion() {
        var targetScale by remember { mutableFloatStateOf(state.scale) }
        val animatedScale: Float by animateFloatAsState(
            targetValue = targetScale,
            label = "TransformScaleAnimation"
        )
        targetScale = DEFAULT_SCALE

        var targetOffset by remember { mutableStateOf(state.offset) }
        val animatedOffset: Offset by animateOffsetAsState(
            targetValue = targetOffset,
            label = "TransformOffsetAnimation"
        )
        targetOffset = DEFAULT_OFFSET

        updateState(animatedScale, animatedOffset)

        // End revert if complete
        if (state.scale == DEFAULT_SCALE
            && state.offset == DEFAULT_OFFSET
        ) revert = false
    }

    if (revert) AnimateTransformationReversion()

    @Composable
    fun ContentTransformationModifier(): Modifier = when (enabled) {
        false -> modifier
        true -> {
            var viewSize by remember { mutableStateOf(IntSize(0, 0)) }
            modifier
                .onSizeChanged { size ->
                    viewSize = size
                }
                // Pan and zoom gestures
                .pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        val inputOffset = centroid - viewSize / 2
                        val panCorrection = (state.offset - inputOffset) * (zoom - 1)
                        updateState(
                            scale = state.scale * zoom,
                            offset = state.offset + pan + panCorrection
                        )
                    }
                }
                // Double-tap to revert transform
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { _ ->
                            revert = true
                        })
                }
        }
    } // end when / ContentTransformationModifier

    Box(
        modifier = ContentTransformationModifier()
    ) {
        content(state)
    }

} // End TransformTool

private operator fun Offset.minus(intSize: IntSize): Offset =
    Offset(
        x = x - intSize.width,
        y = y - intSize.height
    )
