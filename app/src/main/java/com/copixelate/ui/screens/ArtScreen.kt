package com.copixelate.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.copixelate.art.ArtSpace
import com.copixelate.art.PixelGrid
import com.copixelate.art.Point
import com.copixelate.art.PointF
import com.copixelate.ui.util.PreviewSurface
import com.copixelate.viewmodel.ArtViewModel

@Composable
fun ArtScreen(viewModel: ArtViewModel) {

    ArtScreenContent(
        drawing = viewModel.drawing.collectAsState().value,
        palette = viewModel.palette.collectAsState().value,
        activeColor = viewModel.activeColor.collectAsState().value,
        brushPreview = viewModel.brushPreview.collectAsState().value,
        initialBrushSize = viewModel.brushSize.collectAsState().value,
        onTouchDrawing = { unitPosition -> viewModel.updateDrawing(unitPosition) },
        onTouchPalette = { unitPosition -> viewModel.updatePalette(unitPosition) },
        onBrushSizeUpdate = { size -> viewModel.updateBrush(size) }
    )

}

@Composable
fun ArtScreenContent(
    drawing: PixelGrid,
    palette: PixelGrid,
    activeColor: PixelGrid,
    brushPreview: PixelGrid,
    initialBrushSize: Int,
    onTouchDrawing: (unitPosition: PointF) -> Unit,
    onTouchPalette: (unitPosition: PointF) -> Unit,
    onBrushSizeUpdate: (Int) -> Unit

) {

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        Drawing(
            pixelGrid = drawing,
            onDraw = onTouchDrawing
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {

            BrushPreview(
                pixelGrid = brushPreview,
                modifier = Modifier.fillMaxHeight()
            )
            Palette(
                pixelGrid = palette,
                activeColor = activeColor,
                borderStroke = 10.dp,
                onUpdatePaletteActiveIndex = onTouchPalette,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            )

        } // End Row
        BrushSizeSlider(
            steps = SliderSteps(1, 16, initialBrushSize),
            onSizeChange = onBrushSizeUpdate,
            modifier = Modifier.padding(horizontal = 40.dp)
        )

    } // End Column
}

private fun IntSize.toPoint() = Point(width, height)
private fun Offset.toPointF() = PointF(x, y)

@Composable
private fun Drawing(
    pixelGrid: PixelGrid,
    onDraw: (unitPosition: PointF) -> Unit
) {

    var viewSize by remember { mutableStateOf(Point()) }

    BitmapImage(
        pixelGrid = pixelGrid,
        contentDescription = "Drawing",
        contentScale = ContentScale.FillWidth,
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned {
                viewSize = it.size.toPoint()
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    onDraw(change.position.toPointF() / viewSize)
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { position ->
                        onDraw(position.toPointF() / viewSize)
                    })
            })
}

@Composable
private fun Palette(
    pixelGrid: PixelGrid,
    activeColor: PixelGrid,
    borderStroke: Dp,
    onUpdatePaletteActiveIndex: (unitPosition: PointF) -> Unit,
    modifier: Modifier = Modifier
) {

    var viewSize by remember { mutableStateOf(Point()) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        BitmapImage(
            pixelGrid = activeColor,
            contentDescription = "Drawing palette border",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        BitmapImage(
            pixelGrid = pixelGrid,
            contentDescription = "Drawing palette",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
                .padding(borderStroke)
                .onGloballyPositioned {
                    viewSize = it.size.toPoint()
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { offset ->
                            onUpdatePaletteActiveIndex(offset.toPointF() / viewSize)
                        })
                })
    } // End Box
}

@Composable
private fun BrushPreview(
    pixelGrid: PixelGrid,
    contentScale: ContentScale = ContentScale.FillHeight,
    modifier: Modifier
) {
    BitmapImage(
        pixelGrid = pixelGrid,
        contentDescription = "Brush preview",
        contentScale = contentScale,
        modifier = modifier
    )
}

private data class SliderSteps(val min: Int, val max: Int, val default: Int) {
    val size = max - min
}

@Composable
private fun BrushSizeSlider(
    steps: SliderSteps,
    onSizeChange: (Int) -> Unit,
    modifier: Modifier
) {

    var currentStep by remember { mutableStateOf(steps.default) }

    Slider(
        value = (currentStep - steps.min) * 1f / steps.size,
        onValueChange = { newValue ->
            currentStep = (newValue * steps.size + steps.min).toInt()
            onSizeChange(currentStep)
        },
        modifier = modifier
    )
}

/**
 * A composable that lays out and draws a given [Bitmap] from a [PixelGrid] without filtering
 *
 * @param pixelGrid The [PixelGrid] to draw unfiltered
 * @param contentDescription text used by accessibility services to describe what this image
 */
@Composable
internal fun BitmapImage(
    pixelGrid: PixelGrid,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale
) {

    val bitmap = Bitmap.createBitmap(
        pixelGrid.pixels,
        pixelGrid.size.x,
        pixelGrid.size.y,
        Bitmap.Config.RGB_565
    )

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = contentDescription,
        filterQuality = FilterQuality.None,
        modifier = modifier,
        contentScale = contentScale
    )
}

@Preview
@Composable
fun ArtScreenPreview() {

    val phonyState = ArtSpace().state

    PreviewSurface {
        ArtScreenContent(
            drawing = phonyState.colorDrawing,
            palette = phonyState.palette,
            activeColor = phonyState.activeColor,
            brushPreview = phonyState.brushPreview,
            initialBrushSize = phonyState.brushSize,
            onTouchDrawing = {},
            onTouchPalette = {},
            onBrushSizeUpdate = {}
        )
    }

}
