package com.copixelate.ui.screens.art

import androidx.compose.animation.AnimatedVisibility
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

        var expanded by remember { mutableStateOf(false) }
        var previousColor by remember(expanded) { mutableIntStateOf(palette.activeColor) }

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
                            previousColor = palette.pixels[index]
                            onTapPalette(index)
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

            // Toggle ColorEditor visibility
            IconToggleButton(
                checked = expanded,
                onCheckedChange = { newValue -> expanded = newValue },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) {
                when (expanded) {
                    false -> Icon(
                        imageVector = Icons.Outlined.Palette,
                        contentDescription = "Open color picker"
                    )

                    true -> Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Close color picker"
                    )
                }

            }

        } // End Box

        // Collapsible ColorEditor with history
        AnimatedVisibility(visible = expanded) {

            var isRecordingHistory by remember { mutableStateOf(false) }

            // If activeIndex or expanded changes, end history record
            LaunchedEffect(palette.activeIndex, expanded) {
                if (isRecordingHistory) {
                    onRecordPaletteHistory(true)
                    isRecordingHistory = false
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
