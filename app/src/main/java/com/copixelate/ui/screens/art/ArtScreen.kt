package com.copixelate.ui.screens.art

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition.Companion.None
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.layout.onGloballyPositioned
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
import com.copixelate.data.model.createDefaultArt
import com.copixelate.data.model.toArtSpace
import com.copixelate.data.model.toModel
import com.copixelate.ui.common.BitmapImage
import com.copixelate.ui.theme.disable
import com.copixelate.ui.util.PreviewSurface
import com.copixelate.ui.util.toDp
import com.copixelate.viewmodel.ArtViewModel

@Composable
fun ArtScreen(artViewModel: ArtViewModel) {

    AnimatedVisibility(
        visible = artViewModel.contentReady.collectAsState().value,
        enter = fadeIn(), exit = None
    ) {
        ArtScreenContent(
            drawing = artViewModel.drawing.collectAsState().value,
            palette = artViewModel.palette.collectAsState().value,
            brushPreview = artViewModel.brushPreview.collectAsState().value,
            initialBrushSize = artViewModel.brushSize.collectAsState().value,
            onTouchDrawing = { unitPosition -> artViewModel.updateDrawing(unitPosition) },
            onTapPalette = { paletteIndex -> artViewModel.updatePaletteActiveIndex(paletteIndex) },
            onEditColor = { color -> artViewModel.updatePaletteActiveColor(color) },
            onBrushSizeUpdate = { size -> artViewModel.updateBrush(size) }
        )
    }

}

@Composable
fun ArtScreenContent(
    drawing: PixelGrid,
    palette: PaletteModel,
    brushPreview: PixelGrid,
    initialBrushSize: Int,
    onTouchDrawing: (unitPosition: PointF) -> Unit,
    onTapPalette: (paletteIndex: Int) -> Unit,
    onEditColor: (color: Int) -> Unit,
    onBrushSizeUpdate: (Int) -> Unit
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

            var transformable: Boolean by remember { mutableStateOf(false) }

            DrawingTransformer(
                transformable = transformable,
                modifier = Modifier
                    .fillMaxSize()
            ) { transModifier ->
                Drawing(
                    drawing = drawing,
                    editable = !transformable,
                    onTouchDrawing = onTouchDrawing,
                    modifier = transModifier
                        .aspectRatio(drawing.size.x * 1f / drawing.size.y)
                        .align(Alignment.Center)
                )
            }

            DrawingToolBar(
                transformable = transformable,
                onUpdateTransformable = { newValue: Boolean ->
                    transformable = newValue
                },
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

        var previousColor by remember { mutableStateOf(palette.activeColor) }
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
                        .height(80.dp)
                ) {

                    BrushPreview(
                        pixelGrid = brushPreview,
                        modifier = Modifier.fillMaxHeight()
                    )
                    Palette(
                        palette = palette,
                        borderStroke = 10.dp,
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
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = "Localized description"
                    )

                    true -> Icon(
                        imageVector = Icons.Default.ExpandLess,
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

@Composable
private fun DrawingTransformer(
    transformable: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit
) {

    var revert by remember { mutableStateOf(false) }

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale *= zoomChange
        offset += offsetChange
    }

    if (revert) {
        var targetScale by remember { mutableStateOf(scale) }
        val animatedScale: Float by animateFloatAsState(
            targetValue = targetScale
        )
        targetScale = 1f

        var targetOffset by remember { mutableStateOf(offset) }
        val animatedOffset: Offset by animateOffsetAsState(
            targetValue = targetOffset
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
        modifier = modifier
            // Gather transform input
            .transformable(
                state = state,
                enabled = transformable
            )
            .then(when (transformable) {
                false -> Modifier
                // Double-tap to revert transform
                true -> Modifier.pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { _ ->
                            revert = true
                        })
                }
            }
            )

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

} // End DrawingTransformer

@Composable
private fun Drawing(
    drawing: PixelGrid,
    editable: Boolean,
    onTouchDrawing: (unitPosition: PointF) -> Unit,
    modifier: Modifier = Modifier
) {

    var viewSize by remember { mutableStateOf(Point()) }

    fun IntSize.toPoint() = Point(width, height)
    fun Offset.toPointF() = PointF(x, y)

    BitmapImage(
        pixelGrid = drawing,
        contentDescription = "Drawing",
        contentScale = ContentScale.FillBounds,
        modifier = modifier
            .onGloballyPositioned { layout ->
                viewSize = layout.size.toPoint()
            }
            .then(when (editable) {
                false -> Modifier
                // Draw on tap or drag
                true -> {
                    Modifier
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = { position ->
                                    onTouchDrawing(position.toPointF() / viewSize)
                                })
                        }
                        .pointerInput(Unit) {
                            detectDragGestures { change, _ ->
                                onTouchDrawing(change.position.toPointF() / viewSize)
                            }
                        }
                } // End true
            } /* End when */) // End then
    ) // End BitmapImage

} // End Drawing

@Composable
private fun Palette(
    palette: PaletteModel,
    borderStroke: Dp,
    onTapPalette: (index: Int) -> Unit,
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
                .onGloballyPositioned { layout ->
                    viewWidth = layout.size.width
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
                        .width(paletteItemWidth)
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

    val artSpace by remember {
        mutableStateOf(
            SpaceModel()
                .createDefaultArt(8, 16, 2)
                .toArtSpace()
        )
    }

    var drawing by remember { mutableStateOf(artSpace.state.colorDrawing) }
    var palette by remember { mutableStateOf(artSpace.state.palette.toModel()) }
    var brushPreview by remember { mutableStateOf(artSpace.state.brushPreview) }
    var brushSize by remember { mutableStateOf(artSpace.state.brushSize) }

    PreviewSurface {
        ArtScreenContent(
            drawing = drawing,
            palette = palette,
            brushPreview = brushPreview,
            initialBrushSize = brushSize,
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
            }
        )
    }

} // End ArtScreenPreview
