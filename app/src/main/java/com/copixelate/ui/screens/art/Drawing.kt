package com.copixelate.ui.screens.art

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.copixelate.art.PixelGrid
import com.copixelate.art.Point
import com.copixelate.art.PointF
import com.copixelate.ui.common.BitmapImage

enum class TouchStatus {
    STARTED,
    ONGOING,
    ENDED
}

@Composable
internal fun Drawing(
    state: PixelGrid,
    transformState: TransformState,
    editable: Boolean,
    onTouchDrawing: (unitPosition: PointF, status: TouchStatus) -> Unit,
    modifier: Modifier = Modifier
) {

    var totalViewSize by remember { mutableStateOf(IntSize.Zero) }
    var bitmapViewSize by remember { mutableStateOf(IntSize.Zero) }

    fun handleGesture(position: Offset, status: TouchStatus) {
        val correctedPosition = calculateCorrectedPosition(
            position = position,
            totalViewSize = totalViewSize,
            adjustedViewSize = bitmapViewSize,
            transformState = transformState
        )
        val unitPosition =
            correctedPosition.toPointF() / bitmapViewSize.toPoint()
        onTouchDrawing(unitPosition, status)
    }

    @Composable
    fun GestureModifier() = when (editable) {
        false -> Modifier

        true -> Modifier
            .pointerInput(Unit) {
                // Drawing gesture
                awaitPointerEventScope {
                    while (true) {
                        val change = awaitPointerEvent().changes.first()
                        val touchStatus = when {
                            change.pressed && !change.previousPressed -> TouchStatus.STARTED
                            change.pressed -> TouchStatus.ONGOING
                            else -> TouchStatus.ENDED
                        }
                        handleGesture(change.position, touchStatus)
                    }
                }
            } // End pointerInput
    } // End when / GestureModifier

    // Main container
    Box(
        modifier = modifier
            .then(GestureModifier())
            .fillMaxSize()
            .onSizeChanged { size ->
                totalViewSize = size
            }
    ) {

        BitmapImage(
            pixelGrid = state,
            contentDescription = "Drawing",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .aspectRatio(state.aspectRatio)
                .align(Alignment.Center)
                .graphicsLayer(
                    scaleX = transformState.scale,
                    scaleY = transformState.scale,
                    translationX = transformState.offset.x,
                    translationY = transformState.offset.y,
                )
                .onSizeChanged { size ->
                    bitmapViewSize = size
                }
        ) // End BitmapImage

    } // End Box

} // End Drawing

private fun calculateCorrectedPosition(
    position: Offset,
    totalViewSize: IntSize,
    adjustedViewSize: IntSize,
    transformState: TransformState
): Offset {

    // Correct for aspect ratio
    //
    val aspectRatioCorrection = Offset(
        x = (totalViewSize.width - adjustedViewSize.width) / 2f,
        y = (totalViewSize.height - adjustedViewSize.height) / 2f
    )
    // Invert aspect ratio
    val withoutAspectRatio = position - aspectRatioCorrection

    // Correct for transformation
    //
    val scalingOrigin = Offset(
        x = adjustedViewSize.width / 2f,
        y = adjustedViewSize.height / 2f
    )
    // Invert translation
    val withoutTranslation = withoutAspectRatio - transformState.offset
    // Invert scaling
    return ((withoutTranslation - scalingOrigin) / transformState.scale) + scalingOrigin

} // End calculateCorrectedPosition

private fun IntSize.toPoint() = Point(width, height)
private fun Offset.toPointF() = PointF(x, y)
