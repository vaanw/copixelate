package com.copixelate.ui.screens.art

import android.animation.ArgbEvaluator
import android.graphics.Color
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderPositions
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.copixelate.ui.components.BitmapImage
import com.copixelate.ui.util.PreviewSurface
import androidx.compose.ui.graphics.Color as ComposeColor


fun Int.toHSL(): List<Float> =
    FloatArray(3).apply {
        val r = Color.red(this@toHSL)
        val g = Color.green(this@toHSL)
        val b = Color.blue(this@toHSL)
        ColorUtils.RGBToHSL(r, g, b, this)
    }.toList()

fun List<Float>.toColor() =
    ColorUtils.HSLToColor(toFloatArray())


@Composable
fun ColorEditor(
    colorComponents: List<Float>,
    previousColor: Int,
    modifier: Modifier = Modifier,
    onUpdateComponents: (List<Float>) -> Unit = {},
    onRevert: () -> Unit = {},
) {

    Column(
        modifier = modifier
    ) {

        // Hue slider
        ColorEditorSlider(
            value = colorComponents[0],
            valueRange = 0f..360f,
            onValueChange = { newValue ->
                onUpdateComponents(colorComponents.toMutableList()
                    .apply { this[0] = newValue }
                )
            },
            colors = List(
                size = 18
            ) { index: Int ->
                colorComponents
                    .toMutableList()
                    .apply { this[0] = (index * 1f / 18) * 360f }
                    .toColor()
            }
        )

        // Saturation slider
        ColorEditorSlider(
            value = colorComponents[1],
            valueRange = 0f..1f,
            onValueChange = { newValue ->
                onUpdateComponents(colorComponents.toMutableList()
                    .apply { this[1] = newValue }
                )
            },
            colors = List(
                size = 10
            ) { index ->
                colorComponents
                    .toMutableList()
                    .apply { this[1] = (index * 1f / 10) }
                    .toColor()
            }
        )

        // Lightness slider
        ColorEditorSlider(
            value = colorComponents[2],
            valueRange = 0f..1f,
            onValueChange = { newValue ->
                onUpdateComponents(colorComponents.toMutableList()
                    .apply { this[2] = newValue }
                )
            },
            colors = List(
                size = 10
            ) { index ->
                colorComponents
                    .toMutableList()
                    .apply { this[2] = (index * 1f / 10) }
                    .toColor()
            }
        )

        // Revert
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(ButtonDefaults.MinHeight)
        ) {


            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {


                // Revert preview
                Row{

                    BitmapImage(
                        color = colorComponents.toColor(),
                        contentScale = ContentScale.FillBounds,
                        contentDescription = "Localized Description",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                    BitmapImage(
                        color = previousColor,
                        contentScale = ContentScale.FillBounds,
                        contentDescription = "Localized Description",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )

                }

                IconButton(
                    onClick = { onRevert() },
                ) {

                    val iconColor: Int = ArgbEvaluator()
                        .evaluate(0.5f, previousColor, colorComponents.toColor()) as Int

                    Icon(
                        imageVector = Icons.Default.Undo,
                        contentDescription = "",
                        tint = ComposeColor(iconColor),
                        modifier = Modifier.size(32.dp)
                    )

                }

            }

            // Revert button
            TextButton(
                shape = RectangleShape,
                onClick = { onRevert() }
            ) { Text(text = "Revert") }

        }

    }
}


const val SLIDER_HEIGHT = 40

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
                    sliderPositions = sliderPositions,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        )

    } // End Box
} // End PaletteToolSlider


const val THUMB_SIZE = 40
const val THUMB_PADDING = 3
const val THUMB_BORDER_WIDTH = 3
const val TRACK_WIDTH = 3

@Composable
private fun ColorEditorSliderThumb() {

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(THUMB_SIZE.dp)
            .padding(THUMB_PADDING.dp)
    ) {
        Box(
            modifier = Modifier
                .border(
                    width = THUMB_BORDER_WIDTH.dp,
                    color = MaterialTheme.colorScheme.surface
                )
                .fillMaxSize()
                .padding(THUMB_BORDER_WIDTH.dp)
        ) {
            Box(
                modifier = Modifier
                    .border(
                        width = (THUMB_BORDER_WIDTH / 3 + 1).dp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    .fillMaxSize()

            )
        }
    }

}

@Composable
private fun ColorEditorSliderTrack(
    sliderPositions: SliderPositions,
    color: ComposeColor,
) {

    Canvas(
        Modifier
            .fillMaxWidth()
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

        val thumbCenter = (sliderStart.x
                + (sliderEnd.x - sliderStart.x)
                * sliderPositions.activeRange.endInclusive)
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
                color,
                sliderStart,
                centerStart,
                trackStrokeWidth,
                StrokeCap.Square
            )
        }

        if ((sliderEnd.x > centerEnd.x).xor(isRtl)) {
            drawLine(
                color,
                centerEnd,
                sliderEnd,
                trackStrokeWidth,
                StrokeCap.Square
            )
        }
    } // End Canvas

}

@Preview
@Composable
private fun ColorEditorPreview() {
    PreviewSurface {

        val color = 0x5f46ea // Purple
        val previousColor = 0xcc1cbd // Pink
        var colorComponents by remember { mutableStateOf(color.toHSL()) }

        ColorEditor(
            colorComponents = colorComponents,
            previousColor = previousColor,
            onUpdateComponents = { newComponents -> colorComponents = newComponents },
            onRevert = { colorComponents = previousColor.toHSL() }
        )
    }
}
