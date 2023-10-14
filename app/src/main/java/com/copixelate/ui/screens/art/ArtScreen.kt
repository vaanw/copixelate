package com.copixelate.ui.screens.art

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition.Companion.None
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.copixelate.art.HistoryAvailability
import com.copixelate.art.PixelGrid
import com.copixelate.art.PointF
import com.copixelate.data.model.PaletteModel
import com.copixelate.data.model.SpaceModel
import com.copixelate.data.model.toArtSpace
import com.copixelate.data.model.toModel
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
                drawingHistory = artViewModel.drawingHistory.collectAsState().value,
                paletteHistory = artViewModel.paletteHistory.collectAsState().value,
                historyExpanded = artViewModel.historyExpanded.collectAsState().value,

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
                onRecordPaletteHistory = { end ->
                    artViewModel.recordPaletteHistory(end)
                },
                onClickDrawingHistory = { redo ->
                    artViewModel.updateDrawingHistory(redo)
                },
                onClickPaletteHistory = { redo ->
                    artViewModel.updatePaletteHistory(redo)
                },
                onClickExpandHistory = { expanded ->
                    artViewModel.updateHistoryExpanded(expanded)
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
    drawingHistory: HistoryAvailability,
    paletteHistory: HistoryAvailability,
    historyExpanded: Boolean,

    onTouchDrawing: (unitPosition: PointF, status: TouchStatus) -> Unit,
    onTapPalette: (paletteIndex: Int) -> Unit,
    onEditColor: (color: Int) -> Unit,
    onBrushSizeUpdate: (Int) -> Unit,
    onTransform: (transformState: TransformState) -> Unit,
    onClickEnableTransform: (enabled: Boolean) -> Unit,
    onRecordPaletteHistory: (end: Boolean) -> Unit,
    onClickDrawingHistory: (redo: Boolean) -> Unit,
    onClickPaletteHistory: (redo: Boolean) -> Unit,
    onClickExpandHistory: (expanded: Boolean) -> Unit
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
                drawingHistory = drawingHistory,
                paletteHistory = paletteHistory,
                onClickEnableTransform = onClickEnableTransform,
                onClickExpand = onClickExpandHistory,
                onClickDrawingHistory = onClickDrawingHistory,
                onClickPaletteHistory = onClickPaletteHistory,
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
            onRecordPaletteHistory = onRecordPaletteHistory
        )

    } // End Column
} // ArtScreenContent


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

    var drawingHistory by remember { mutableStateOf(artSpace.state.drawingHistory) }
    var paletteHistory by remember { mutableStateOf(artSpace.state.paletteHistory) }
    var historyExpanded by remember { mutableStateOf(false) }


    PreviewSurface {
        ArtScreenContent(
            drawing = drawing,
            palette = palette,
            brushPreview = brushPreview,
            initialBrushSize = brushSize,
            transformState = transformState,
            transformEnabled = transformEnabled,
            drawingHistory = drawingHistory,
            paletteHistory = paletteHistory,
            historyExpanded = historyExpanded,

            onTouchDrawing = { unitPosition, touchStatus ->
                if (touchStatus == TouchStatus.STARTED)
                    artSpace.recordDrawingHistory()

                artSpace.updateDrawing(unitPosition = unitPosition)
                drawing = artSpace.state.colorDrawing

                if (touchStatus == TouchStatus.ENDED) {
                    artSpace.recordDrawingHistory(end = true)
                    drawingHistory = artSpace.state.drawingHistory
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
            onRecordPaletteHistory = { end ->
                artSpace.recordPaletteHistory(end)
                paletteHistory = artSpace.state.paletteHistory
            },
            onClickExpandHistory = { expanded ->
                historyExpanded = expanded
            },
            onClickDrawingHistory = { redo ->
                artSpace.applyDrawingHistory(redo)
                drawing = artSpace.state.colorDrawing
                drawingHistory = artSpace.state.drawingHistory
            },
            onClickPaletteHistory = { redo ->
                artSpace.applyPaletteHistory(redo)
                drawing = artSpace.state.colorDrawing
                palette = artSpace.state.palette.toModel()
                paletteHistory = artSpace.state.paletteHistory
            }
        )
    }

} // End ArtScreenPreview
