package com.copixelate.ui.screens.art

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition.Companion.None
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.copixelate.art.PixelGrid
import com.copixelate.art.PointF
import com.copixelate.data.model.PaletteModel
import com.copixelate.data.model.SpaceModel
import com.copixelate.data.model.toArtSpace
import com.copixelate.data.model.toModel
import com.copixelate.ui.common.BitmapImage
import com.copixelate.ui.util.PreviewSurface
import com.copixelate.ui.util.ScreenSurface
import com.copixelate.ui.util.generateDefaultArt
import com.copixelate.viewmodel.ArtViewModel
import com.copixelate.viewmodel.DEFAULT_BRUSH_SIZE

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
                historyExpanded = artViewModel.historyExpanded.collectAsState().value,
                historyAvailability = artViewModel.historyAvailability.collectAsState().value,

                onTouchDrawing = { unitPosition, touchStatus ->
                    artViewModel.updateDrawing(unitPosition, touchStatus)
                },
                onTapPalette = { paletteIndex ->
                    artViewModel.updatePaletteActiveIndex(paletteIndex)
                },
                onEditColor = { color ->
                    artViewModel.updatePaletteActiveColor(color)
                },
                onBrushSizeUpdate = { size ->
                    artViewModel.updateBrush(size)
                },
                onTransform = { transformState ->
                    artViewModel.updateTransformState(transformState)
                },
                onClickEnableTransform = { enabled ->
                    artViewModel.updateTransformEnabled(enabled)
                },
                onClickExpandHistory = { expanded ->
                    artViewModel.updateHistoryExpanded(expanded)
                },
                onClickDrawingHistory = { redo ->
                    artViewModel.updateDrawingHistory(redo)
                }
            )
        } // End AnimatedVisibility
    } // End ScreenSurface

}

@Composable
private fun ArtScreenContent(
    drawing: PixelGrid,
    palette: PaletteModel,
    brushPreview: PixelGrid,
    initialBrushSize: Int,
    transformState: TransformState,
    transformEnabled: Boolean,
    historyExpanded: Boolean,
    historyAvailability: HistoryAvailability,
    onTouchDrawing: (unitPosition: PointF, status: TouchStatus) -> Unit,
    onTapPalette: (paletteIndex: Int) -> Unit,
    onEditColor: (color: Int) -> Unit,
    onBrushSizeUpdate: (Int) -> Unit,
    onTransform: (transformState: TransformState) -> Unit,
    onClickEnableTransform: (enabled: Boolean) -> Unit,
    onClickExpandHistory: (expanded: Boolean) -> Unit,
    onClickDrawingHistory: (Boolean) -> Unit,
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

            DrawingToolbar(
                transformable = transformEnabled,
                expanded = historyExpanded,
                historyAvailability = historyAvailability,
                onClickEnableTransform = onClickEnableTransform,
                onClickExpand = onClickExpandHistory,
                onClickDrawingHistory = onClickDrawingHistory,
                onClickPaletteHistory = {},
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
            )

        } // End Box

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
private fun PalettePanel(
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
                .apply {
                    updateBrushSize(DEFAULT_BRUSH_SIZE)
                }
        )
    }

    var drawing by remember { mutableStateOf(artSpace.state.colorDrawing) }
    var palette by remember { mutableStateOf(artSpace.state.palette.toModel()) }
    var brushPreview by remember { mutableStateOf(artSpace.state.brushPreview) }
    var brushSize by remember { mutableIntStateOf(artSpace.state.brushSize) }

    var transformState by remember { mutableStateOf(TransformState(scale = 0.5f)) }
    var transformEnabled by remember { mutableStateOf(true) }

    var historyExpanded by remember { mutableStateOf(false) }
    var historyAvailability by remember { mutableStateOf(HistoryAvailability()) }

    fun refreshHistoryAvailability() {
        historyAvailability = historyAvailability.copy(
            drawingUndo = artSpace.state.drawingUndoAvailable,
            drawingRedo = artSpace.state.drawingRedoAvailable
        )
    }

    PreviewSurface {
        ArtScreenContent(
            drawing = drawing,
            palette = palette,
            brushPreview = brushPreview,
            initialBrushSize = brushSize,
            transformState = transformState,
            transformEnabled = transformEnabled,
            historyExpanded = historyExpanded,
            historyAvailability = historyAvailability,

            onTouchDrawing = { unitPosition, touchStatus ->
                if (touchStatus == TouchStatus.STARTED)
                    artSpace.startDrawingHistoryRecord()
                artSpace.updateDrawing(unitPosition = unitPosition)
                drawing = artSpace.state.colorDrawing
                if (touchStatus == TouchStatus.ENDED) {
                    artSpace.endDrawingHistoryRecord()
                    refreshHistoryAvailability()
                }

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
            onClickEnableTransform = { enabled ->
                transformEnabled = enabled
            },
            onClickExpandHistory = { expanded ->
                historyExpanded = expanded
            },
            onClickDrawingHistory = { redo ->
                when (redo) {
                    false -> artSpace.undoDrawingHistory()
                    true -> artSpace.redoDrawingHistory()
                }
                drawing = artSpace.state.colorDrawing
                refreshHistoryAvailability()
            }
        )
    }

} // End ArtScreenPreview
