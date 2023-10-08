package com.copixelate.ui.screens.art

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition.Companion.None
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.copixelate.R
import com.copixelate.art.PixelGrid
import com.copixelate.art.Point
import com.copixelate.art.PointF
import com.copixelate.data.model.PaletteModel
import com.copixelate.data.model.SpaceModel
import com.copixelate.data.model.toArtSpace
import com.copixelate.data.model.toModel
import com.copixelate.ui.common.BitmapImage
import com.copixelate.ui.theme.disable
import com.copixelate.ui.util.PreviewSurface
import com.copixelate.ui.util.ScreenSurface
import com.copixelate.ui.util.generateDefaultArt
import com.copixelate.ui.util.toDp
import com.copixelate.viewmodel.ArtViewModel

@Composable
fun ArtScreen(artViewModel: ArtViewModel) {

    ScreenSurface {
        AnimatedVisibility(
            visible = artViewModel.contentReady.collectAsState().value,
            enter = fadeIn(), exit = None,
        ) {
            ArtScreenContent(
                drawing = artViewModel.drawing.collectAsState().value,
                palette = artViewModel.palette.collectAsState().value,
                brushPreview = artViewModel.brushPreview.collectAsState().value,
                initialBrushSize = artViewModel.brushSize.collectAsState().value,
                transformState = artViewModel.transformState,
                transformEnabled = artViewModel.transformEnabled.collectAsState().value,
                onTouchDrawing = { unitPosition -> artViewModel.updateDrawing(unitPosition) },
                onTapPalette = { paletteIndex -> artViewModel.updatePaletteActiveIndex(paletteIndex) },
                onEditColor = { color -> artViewModel.updatePaletteActiveColor(color) },
                onBrushSizeUpdate = { size -> artViewModel.updateBrush(size) },
                onTransform = { transformState -> artViewModel.updateTransformState(transformState) },
                onTransformEnableChange = { enabled -> artViewModel.updateTransformEnabled(enabled) }
            )
        } // End AnimatedVisibility
    } // End ScreenSurface

}

@Composable
fun ArtScreenContent(
    drawing: PixelGrid,
    palette: PaletteModel,
    brushPreview: PixelGrid,
    initialBrushSize: Int,
    transformState: TransformState,
    transformEnabled: Boolean,
    onTouchDrawing: (unitPosition: PointF) -> Unit,
    onTapPalette: (paletteIndex: Int) -> Unit,
    onEditColor: (color: Int) -> Unit,
    onBrushSizeUpdate: (Int) -> Unit,
    onTransform: (transformState: TransformState) -> Unit,
    onTransformEnableChange: (enabled: Boolean) -> Unit
) {

    Column {

        // Drawing area
        Box(
            modifier = Modifier
                // Fill available column height
                .weight(1f)
                // Prevents drawing being shown outside container due to transform
                .clip(shape = RectangleShape)
        ) {

            TransformTool(
                initialState = transformState,
                onStateChange = onTransform,
                enabled = transformEnabled
            ) { transformState ->
                Drawing(
                    state = drawing,
                    transformState = transformState,
                    editable = !transformEnabled,
                    onTouchDrawing = onTouchDrawing
                )
            }

            DrawingToolBar(
                transformable = transformEnabled,
                onUpdateTransformable = onTransformEnableChange,
                modifier = Modifier
                    .align(Alignment.TopEnd)
            )

        }

        PalettePanel(
            palette = palette,
            brushPreview = brushPreview,
            initialBrushSize = initialBrushSize,
            onTapPalette = onTapPalette,
            onEditColor = onEditColor,
            onBrushSizeUpdate = onBrushSizeUpdate,
        )

    } // End Column
} // ArtScreenContent

@Composable
fun PalettePanel(
    palette: PaletteModel,
    brushPreview: PixelGrid,
    initialBrushSize: Int,
    onTapPalette: (paletteIndex: Int) -> Unit,
    onEditColor: (color: Int) -> Unit,
    onBrushSizeUpdate: (Int) -> Unit
) {

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        var expanded by remember { mutableStateOf(false) }

        var previousColor by remember { mutableIntStateOf(palette.activeColor) }
        var colorComponents by remember { mutableStateOf(palette.activeColor.toHSL()) }

        Box {
            // Palette + preview
            Column(
                modifier = Modifier
                    .padding(bottom = 32.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                ) {

                    BrushPreview(
                        pixelGrid = brushPreview,
                        modifier = Modifier.fillMaxHeight()
                    )
                    Palette(
                        palette = palette,
                        borderStroke = 8.dp,
                        onTapPalette = { index ->
                            onTapPalette(index)
                            previousColor = palette.pixels[index]
                            colorComponents = previousColor.toHSL()
                        },
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                    )

                } // End Row

                BrushSizeSlider(
                    steps = SliderSteps(1, 16, initialBrushSize),
                    onSizeChange = onBrushSizeUpdate,
                    modifier = Modifier
                        .padding(horizontal = 40.dp)
                )

            } // End Column

            IconToggleButton(
                checked = expanded,
                onCheckedChange = { newValue -> expanded = newValue },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) {
                when (expanded) {
                    false -> Icon(
                        imageVector = Icons.Outlined.Palette,
                        contentDescription = "Localized description"
                    )

                    true -> Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Localized description"
                    )
                }

            }

        } // End Box

        AnimatedVisibility(visible = expanded) {

            ColorEditor(
                colorComponents = colorComponents,
                previousColor = previousColor,
                onUpdateComponents = { newComponents ->
                    colorComponents = newComponents
                    onEditColor(colorComponents.toColor())
                },
                onRevert = {
                    colorComponents = previousColor.toHSL()
                    onEditColor(colorComponents.toColor())
                }
            )

        }

    } // End Column

} // End PalettePanel

@Composable
private fun DrawingToolBar(
    transformable: Boolean,
    onUpdateTransformable: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    Row(modifier = modifier) {

        val bgColor = MaterialTheme.colorScheme.background

        // Enable transformable-mode (pan & zoom)
        IconToggleButton(
            checked = transformable,
            onCheckedChange = { newValue: Boolean ->
                onUpdateTransformable(newValue)
            },
            colors = IconButtonDefaults.iconToggleButtonColors(
                containerColor = bgColor,
                checkedContainerColor = bgColor,
                contentColor = LocalContentColor.current.disable()
            )
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.drag_pan),
                contentDescription = "Enable pan and zoom"
            )
        } // End IconToggleButton

    } // End Row
} // End DrawingToolBar

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

@Composable
private fun Drawing(
    state: PixelGrid,
    transformState: TransformState,
    editable: Boolean,
    onTouchDrawing: (unitPosition: PointF) -> Unit,
    modifier: Modifier = Modifier
) {

    var totalViewSize by remember { mutableStateOf(IntSize.Zero) }
    var bitmapViewSize by remember { mutableStateOf(IntSize.Zero) }

    fun handleGesture(position: Offset) {
        val correctedPosition = calculateCorrectedPosition(
            position = position,
            totalViewSize = totalViewSize,
            adjustedViewSize = bitmapViewSize,
            transformState = transformState
        )
        val unitPosition =
            correctedPosition.toPointF() / bitmapViewSize.toPoint()
        onTouchDrawing(unitPosition)
    }

    @Composable
    fun GestureModifier() = when (editable) {
        false -> Modifier

        true -> Modifier
            // Draw on press gesture
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { position ->
                        handleGesture(position)
                    }
                )
            }
            // Draw on drag gesture
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    handleGesture(change.position)
                }
            }
    } // End when / PointerInputModifier

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

@Composable
private fun Palette(
    palette: PaletteModel,
    borderStroke: Dp,
    onTapPalette: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {

    var viewWidth by remember { mutableIntStateOf(0) }
    val swatchWidth = max(
        a = 48.dp,
        b = (viewWidth / palette.pixels.size).toDp()
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        // Palette border
        BitmapImage(
            color = palette.activeColor,
            contentDescription = "Drawing palette border",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        // Palette items
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .padding(borderStroke)
                .onSizeChanged { size ->
                    viewWidth = size.width
                }
        ) {
            itemsIndexed(items = palette.pixels) { index, color ->
                BitmapImage(
                    pixelGrid = PixelGrid(
                        pixels = intArrayOf(color),
                        size = Point(1)
                    ),
                    contentDescription = "Drawing palette border",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(swatchWidth)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { onTapPalette(index) }
                            )
                        }
                )
            } // End itemsIndexed
        } // End Lazy Row (Palette items)

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

    var currentStep by remember { mutableIntStateOf(steps.default) }

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

    val artSpace by remember {
        mutableStateOf(
            SpaceModel()
                .generateDefaultArt(8, 16, 2)
                .toArtSpace()
        )
    }

    var drawing by remember { mutableStateOf(artSpace.state.colorDrawing) }
    var palette by remember { mutableStateOf(artSpace.state.palette.toModel()) }
    var brushPreview by remember { mutableStateOf(artSpace.state.brushPreview) }
    var brushSize by remember { mutableIntStateOf(artSpace.state.brushSize) }

    var transformState by remember { mutableStateOf(TransformState(scale = 0.5f)) }
    var transformEnabled by remember { mutableStateOf(false) }

    PreviewSurface {
        ArtScreenContent(
            drawing = drawing,
            palette = palette,
            brushPreview = brushPreview,
            initialBrushSize = brushSize,
            transformState = transformState,
            transformEnabled = transformEnabled,
            onTouchDrawing = { unitPosition ->
                artSpace.updateDrawing(unitPosition = unitPosition)
                drawing = artSpace.state.colorDrawing
            },
            onTapPalette = { paletteIndex ->
                artSpace.updatePaletteActiveIndex(index = paletteIndex)
                palette = artSpace.state.palette.toModel()
                brushPreview = artSpace.state.brushPreview
            },
            onEditColor = { color ->
                artSpace.updatePaletteActiveColor(color = color)
                palette = artSpace.state.palette.toModel()
                brushPreview = artSpace.state.brushPreview
                drawing = artSpace.state.colorDrawing
            },
            onBrushSizeUpdate = { size ->
                artSpace.updateBrushSize(size = size)
                brushSize = artSpace.state.brushSize
                brushPreview = artSpace.state.brushPreview
            },
            onTransform = { newTransformState ->
                transformState = newTransformState
            },
            onTransformEnableChange = { enabled ->
                transformEnabled = enabled
            }
        )
    }

} // End ArtScreenPreview
