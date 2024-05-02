package com.copixelate.ui.screens.art

import android.graphics.Color
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring.StiffnessVeryLow
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.copixelate.ui.common.BitmapImage
import com.copixelate.ui.util.PreviewSurface
import kotlin.math.abs

internal fun Int.toHSL(): List<Float> =
    FloatArray(3).apply {
        val r = Color.red(this@toHSL)
        val g = Color.green(this@toHSL)
        val b = Color.blue(this@toHSL)
        ColorUtils.RGBToHSL(r, g, b, this)
    }.toList()

internal fun List<Float>.toColor() =
    ColorUtils.HSLToColor(toFloatArray())


@Composable
internal fun ColorEditor(
    color: Int,
    previousColor: Int,
    onColorChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {

    var colorComponents by remember { mutableStateOf(color.toHSL()) }

    LaunchedEffect(color) {
        if (colorComponents.toColor() != color) {
            colorComponents = color.toHSL()
        }
    }

    fun updateColorComponent(index: Int, newValue: Float) {
        colorComponents = colorComponents
            .toMutableList()
            .apply { this[index] = newValue }
        onColorChange(colorComponents.toColor())
    }

    // Main container
    Column(modifier = modifier) {

        RevertButtonRow(
            color = color,
            previousColor = previousColor,
            onRevert = {
                colorComponents = previousColor.toHSL()
                onColorChange(colorComponents.toColor())
            }
        )

        // Hue slider
        ColorEditorSlider(
            value = colorComponents[0],
            valueRange = 0f..360f,
            onValueChange = { newValue ->
                updateColorComponent(0, newValue)
            },
            colors = List(
                size = 18
            ) { index: Int ->
                colorComponents
                    .toMutableList()
                    .apply { this[0] = (index * 1f / 17) * 360f }
                    .toColor()
            }
        )

        // Saturation slider
        ColorEditorSlider(
            value = colorComponents[1],
            valueRange = 0f..1f,
            onValueChange = { newValue ->
                updateColorComponent(1, newValue)
            },
            colors = List(
                size = 10
            ) { index ->
                colorComponents
                    .toMutableList()
                    .apply { this[1] = (index * 1f / 9) }
                    .toColor()
            }
        )

        // Lightness slider
        ColorEditorSlider(
            value = colorComponents[2],
            valueRange = 0f..1f,
            onValueChange = { newValue ->
                updateColorComponent(2, newValue)
            },
            colors = List(
                size = 10
            ) { index ->
                colorComponents
                    .toMutableList()
                    .apply { this[2] = (index * 1f / 9) }
                    .toColor()
            }
        )

    } // End Column

} // End ColorEditor


@Composable
private fun RevertButtonRow(
    color: Int,
    previousColor: Int,
    onRevert: () -> Unit
) {

    Row(
        modifier = Modifier
            .height(ButtonDefaults.MinHeight)
    ) {

        BitmapImage(
            color = color,
            contentScale = ContentScale.FillBounds,
            contentDescription = "Localized Description",
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )

        Box(
            modifier = Modifier
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {

            BitmapImage(
                color = previousColor,
                contentScale = ContentScale.FillBounds,
                contentDescription = "Localized Description",
                modifier = Modifier
                    .fillMaxSize()
            )

            val backgroundLuminance =
                Color.luminance(previousColor)
            val textLuminance =
                Color.luminance(MaterialTheme.colorScheme.onSurface.toArgb())
            val inverseTextLuminance =
                Color.luminance(MaterialTheme.colorScheme.inverseOnSurface.toArgb())

            // Choose icon color based on luminance
            val iconColor =
                when (abs(backgroundLuminance - textLuminance)
                        > abs(backgroundLuminance - inverseTextLuminance)
                ) {
                    true -> MaterialTheme.colorScheme.onSurface
                    false -> MaterialTheme.colorScheme.inverseOnSurface
                }

            val animatedIconColor by animateColorAsState(
                targetValue = iconColor,
                animationSpec = spring(stiffness = StiffnessVeryLow),
                label = "RevertIconColorAnimation"
            )

            val isColorMismatch = remember(color) { color != previousColor }

            androidx.compose.animation.AnimatedVisibility(
                visible = isColorMismatch,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {

                IconButton(
                    onClick = onRevert
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.Undo,
                        contentDescription = "Undo color edit",
                        tint = animatedIconColor,
                    )
                }
            }

        } // End Box

    } // End Row
} // End RevertButtonRow


private const val SLIDER_HEIGHT = 48

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColorEditorSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    colors: List<Int>
) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(SLIDER_HEIGHT.dp)
            .systemGestureExclusion()
    ) {

        // Color spectrum
        Row {
            for (color in colors) {
                BitmapImage(
                    color = color,
                    contentScale = ContentScale.FillBounds,
                    contentDescription = "Localized Description",
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )
            }
        }

        Slider(
            value = value,
            valueRange = valueRange,
            onValueChange = onValueChange,
            thumb = { ColorEditorSliderThumb() },
            track = { sliderPositions ->
                ColorEditorSliderTrack(
                    sliderState = sliderPositions
                )
            }
        )

    } // End Box
} // End PaletteToolSlider


private const val THUMB_SIZE = 48
private const val THUMB_PADDING = 4
private const val THUMB_BORDER_WIDTH = 2
private const val TRACK_WIDTH = 4

@Composable
private fun ColorEditorSliderThumb() {

    val outerColor = MaterialTheme.colorScheme.primary
    val innerColor = MaterialTheme.colorScheme.surfaceVariant

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(THUMB_SIZE.dp)
            .padding(THUMB_PADDING.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = (2 * THUMB_BORDER_WIDTH).dp,
                    color = innerColor,
                    shape = ShapeDefaults.ExtraSmall
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = THUMB_BORDER_WIDTH.dp,
                    color = outerColor,
                    shape = ShapeDefaults.ExtraSmall
                )
        )

    }

} // End ColorEditorSliderThumb

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColorEditorSliderTrack(
    sliderState: SliderState
) {

    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.surfaceVariant

    val normalValue = sliderState.value / sliderState.valueRange.endInclusive

    Canvas(
        Modifier.fillMaxWidth()
    ) {
        val isRtl = layoutDirection == LayoutDirection.Rtl

        val sliderLeft = Offset(0f, center.y)
        val sliderRight = Offset(size.width, center.y)
        val sliderStart = when (isRtl) {
            false -> sliderLeft; true -> sliderRight
        }
        val sliderEnd = when (isRtl) {
            false -> sliderRight; true -> sliderLeft
        }

        val trackStrokeWidth = TRACK_WIDTH.dp.toPx()

        val thumbCenter = (sliderEnd.x - sliderStart.x) * normalValue + sliderStart.x
        val thumbOffset = (THUMB_SIZE / 2 - THUMB_PADDING).dp.toPx()

        val centerLeft = Offset(
            x = thumbCenter - thumbOffset,
            y = center.y
        )

        val centerRight = Offset(
            x = thumbCenter + thumbOffset,
            y = center.y
        )

        val centerStart = when (isRtl) {
            false -> centerLeft; true -> centerRight
        }
        val centerEnd = when (isRtl) {
            false -> centerRight; true -> centerLeft
        }

        if ((sliderStart.x < centerStart.x).xor(isRtl)) {
            drawLine(
                activeColor,
                sliderStart,
                centerStart,
                trackStrokeWidth,
                StrokeCap.Round
            )
        }

        if ((sliderEnd.x > centerEnd.x).xor(isRtl)) {
            drawLine(
                inactiveColor,
                centerEnd,
                sliderEnd,
                trackStrokeWidth,
                StrokeCap.Round
            )
        }
    } // End Canvas

} // ColorEditorSliderTrack

@Preview
@Composable
private fun ColorEditorPreview() {
    PreviewSurface {

        val opaquePurple = 0xFF5F46EA.toInt()
        val opaquePink = 0xFFd673b2.toInt()

        var color by remember { mutableIntStateOf(opaquePink) }
        val previousColor = remember { opaquePurple }

        ColorEditor(
            color = color,
            previousColor = previousColor,
            onColorChange = { newColor ->
                color = newColor
            },
        )
    }
}
