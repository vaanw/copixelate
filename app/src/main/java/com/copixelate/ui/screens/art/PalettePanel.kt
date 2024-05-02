package com.copixelate.ui.screens.art

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.copixelate.art.PixelGrid
import com.copixelate.data.model.PaletteModel
import com.copixelate.ui.common.BitmapImage
import com.copixelate.ui.icon.IconCatalog

@Composable
internal fun PalettePanel(
    palette: PaletteModel,
    brushPreview: PixelGrid,
    initialBrushSize: Int,
    onTapPalette: (paletteIndex: Int) -> Unit,
    onEditColor: (color: Int) -> Unit,
    onBrushSizeUpdate: (size: Int) -> Unit,
    onRecordPaletteHistory: (end: Boolean) -> Unit
) {



    Column {

        var previousColor by remember(palette.activeIndex) {
            mutableIntStateOf(palette.activeColor)
        }

        // Expanding / collapsing animation state
        val expanding = remember { MutableTransitionState(false) }

        // Update previous color when collapsing animation completes
        LaunchedEffect(expanding.isIdle) {
            if (expanding.isIdle && !expanding.currentState)
                previousColor = palette.activeColor
        }

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
                        onTapPalette = onTapPalette,
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

            // Toggle ColorEditor visibility
            IconToggleButton(
                checked = expanding.targetState,
                onCheckedChange = { newValue ->
                    expanding.targetState = newValue
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) {
                when (expanding.targetState) {
                    false -> Icon(
                        imageVector = IconCatalog.paletteAlt,
                        contentDescription = "Open color picker"
                    )

                    true -> Icon(
                        imageVector = IconCatalog.palette,
                        contentDescription = "Close color picker"
                    )
                }

            }

        } // End Box

        // Collapsible ColorEditor with history
        AnimatedVisibility(visibleState = expanding) {

            var isRecordingHistory by remember { mutableStateOf(false) }

            // If activeIndex changes or composition is disposed, end history record
            DisposableEffect(palette.activeIndex){
                onDispose {
                    if (isRecordingHistory) {
                        onRecordPaletteHistory(true)
                        isRecordingHistory = false
                    }
                }
            }

            ColorEditor(
                color = palette.activeColor,
                previousColor = previousColor,
                onColorChange = { newColor ->
                    // If color changes, begin recording history
                    if (!isRecordingHistory) {
                        isRecordingHistory = true
                        onRecordPaletteHistory(false)
                    }

                    onEditColor(newColor)
                }
            ) // End ColorEditor

        } // End AnimatedVisibility

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
