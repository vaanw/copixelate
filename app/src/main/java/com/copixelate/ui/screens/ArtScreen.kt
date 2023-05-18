package com.copixelate.ui.screens

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.copixelate.art.*
import com.copixelate.ui.components.BitmapImage
import com.copixelate.ui.util.PreviewSurface
import com.copixelate.ui.util.toDp
import com.copixelate.viewmodel.ArtViewModel

@Composable
fun ArtScreen(viewModel: ArtViewModel) {

    ArtScreenContent(
        drawing = viewModel.drawing.collectAsState().value,
        palette = viewModel.palette.collectAsState().value,
        brushPreview = viewModel.brushPreview.collectAsState().value,
        initialBrushSize = viewModel.brushSize.collectAsState().value,
        onTouchDrawing = { unitPosition -> viewModel.updateDrawing(unitPosition) },
        onTapPalette = { paletteIndex -> viewModel.updatePaletteActiveIndex(paletteIndex) },
        onBrushSizeUpdate = { size -> viewModel.updateBrush(size) }
    )

}

@Composable
fun ArtScreenContent(
    drawing: PixelGrid,
    palette: PixelRow,
    brushPreview: PixelGrid,
    initialBrushSize: Int,
    onTouchDrawing: (unitPosition: PointF) -> Unit,
    onTapPalette: (paletteIndex: Int) -> Unit,
    onBrushSizeUpdate: (Int) -> Unit

) {

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        Drawing(
            drawing = drawing,
            onTouchDrawing = onTouchDrawing
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
                palette = palette,
                borderStroke = 10.dp,
                onTapPalette = onTapPalette,
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
    drawing: PixelGrid,
    onTouchDrawing: (unitPosition: PointF) -> Unit
) {

    var viewSize by remember { mutableStateOf(Point()) }

    BitmapImage(
        pixelGrid = drawing,
        contentDescription = "Drawing",
        contentScale = ContentScale.FillWidth,
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned {
                viewSize = it.size.toPoint()
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    onTouchDrawing(change.position.toPointF() / viewSize)
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { position ->
                        onTouchDrawing(position.toPointF() / viewSize)
                    })
            })
}

@Composable
private fun Palette(
    palette: PixelRow,
    borderStroke: Dp,
    onTapPalette: (paletteIndex: Int) -> Unit,
    modifier: Modifier = Modifier
) {

    var viewWidth by remember { mutableStateOf(0) }
    val paletteItemWidth = max(
        a = 50.dp,
        b = (viewWidth / palette.pixels.size).toDp()
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier

    ) {
        BitmapImage(
            color = palette.activeColor,
            contentDescription = "Drawing palette border",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .padding(borderStroke)
                .onGloballyPositioned { layout ->
                    viewWidth = layout.size.width
                }
        ) {
            itemsIndexed(items = palette.pixels.toList()) { index, value ->
                BitmapImage(
                    pixelGrid = PixelGrid(
                        pixels = intArrayOf(value),
                        size = Point(1)
                    ),
                    contentDescription = "Drawing palette border",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(paletteItemWidth)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { onTapPalette(index) })
                        }
                )
            }
        }

    } // End Box

} // End Palette

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


@Preview
@Composable
fun ArtScreenPreview() {

    val phonyState = ArtSpace().state

    PreviewSurface {
        ArtScreenContent(
            drawing = phonyState.colorDrawing,
            palette = phonyState.palette,
            brushPreview = phonyState.brushPreview,
            initialBrushSize = phonyState.brushSize,
            onTouchDrawing = {},
            onTapPalette = {},
            onBrushSizeUpdate = {}
        )
    }

}
