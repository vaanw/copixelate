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
import androidx.compose.material3.Surface
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
    modifier: Modifier = Modifier,
    color: Int = Color.MAGENTA,
    previousColor: Int = Color.MAGENTA,
    onUpdateColor: (color: Int) -> Unit = {},
    onRevert: () -> Unit = {},
) {

    var colorComponent1 by remember { mutableStateOf(color.toHSL()[0]) }
    var colorComponent2 by remember { mutableStateOf(color.toHSL()[1]) }
    var colorComponent3 by remember { mutableStateOf(color.toHSL()[2]) }

    fun hslComponents(): List<Float> =
        listOf(colorComponent1, colorComponent2, colorComponent3)

    fun hslColor(): Int =
        hslComponents().toColor()

    fun revert() {
        val previousHsl: List<Float> = previousColor.toHSL()
        colorComponent1 = previousHsl[0]
        colorComponent2 = previousHsl[1]
        colorComponent3 = previousHsl[2]
    }

    Column(
        modifier = modifier
    ) {

        // Hue slider
        ColorEditorSlider(
            value = colorComponent1,
            valueRange = 0f..360f,
            onValueChange = { newValue ->
                colorComponent1 = newValue
                onUpdateColor(hslColor())
            },
            colors = List(
                size = 18
            ) { index: Int ->
                hslComponents()
                    .toMutableList()
                    .apply { this[0] = (index * 1f / 18) * 360f }
                    .toColor()
            }
        )

        // Saturation slider
        ColorEditorSlider(
            value = colorComponent2,
            valueRange = 0f..1f,
            onValueChange = { newValue ->
                colorComponent2 = newValue
                onUpdateColor(hslColor())
            },
            colors = List(
                size = 10
            ) { index ->
                hslComponents()
                    .toMutableList()
                    .apply { this[1] = (index * 1f / 10) }
                    .toColor()
            }
        )

        // Lightness slider
        ColorEditorSlider(
            value = colorComponent3,
            valueRange = 0f..1f,
            onValueChange = { newValue ->
                colorComponent3 = newValue
                onUpdateColor(hslColor())
            },
            colors = List(
                size = 10
            ) { index ->
                hslComponents()
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
                Row(
                    modifier = Modifier
//                        .weight(1f)
                ) {

                    BitmapImage(
                        color = color,
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
                    onClick = {
                        revert()
                        onRevert()
                    },
                    modifier = Modifier.padding(0.dp)
                ) {

                    val iconColor: Int = ArgbEvaluator()
                        .evaluate(0.5f, previousColor, color) as Int

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
                onClick = {
                    revert()
                    onRevert()
                }
            ) { Text(text = "Revert") }

        }

    }
}

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
            .height(40.dp)
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
                    color = ComposeColor(Color.BLACK)
                )
            }
        )

    } // End Box
} // End PaletteToolSlider

@Composable
private fun ColorEditorSliderThumb() {

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)
            .padding(3.dp)
    ) {
        Box(
            modifier = Modifier
                .border(width = 3.dp, color = ComposeColor(Color.BLACK))
                .fillMaxSize()
                .padding(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .border(width = 2.dp, color = ComposeColor(Color.WHITE))
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
        val sliderStart = if (isRtl) sliderRight else sliderLeft
        val sliderEnd = if (isRtl) sliderLeft else sliderRight
        val trackStrokeWidth = 3.dp.toPx()

        val sliderMid1 = Offset(
            sliderStart.x
                    + (sliderEnd.x - sliderStart.x)
                    * sliderPositions.activeRange.endInclusive
                    - 18.dp.toPx(),
            center.y
        )

        val sliderMid2 = Offset(
            sliderStart.x
                    + (sliderEnd.x - sliderStart.x)
                    * sliderPositions.activeRange.endInclusive
                    + 18.dp.toPx(),
            center.y
        )

        if (sliderStart.x < sliderMid1.x) {
            drawLine(
                color,
                sliderStart,
                sliderMid1,
                trackStrokeWidth,
                StrokeCap.Square
            )
        }

        if (sliderEnd.x > sliderMid2.x) {
            drawLine(
                color,
                sliderMid2,
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

        val previousColor = Color.MAGENTA
        var color by remember { mutableStateOf(previousColor) }
        ColorEditor(
            color = color,
            previousColor = previousColor,
            onUpdateColor = { newColor -> color = newColor },
            onRevert = { color = previousColor }
        )
    }
}

@Preview
@Composable
private fun ColorEditorPreviewAlt() {
    PreviewSurface {
        Surface(color = MaterialTheme.colorScheme.surfaceVariant) {

            val previousColor = Color.BLUE
            var color by remember { mutableStateOf(previousColor) }
            ColorEditor(
                color = color,
                previousColor = previousColor,
                onUpdateColor = { newColor -> color = newColor },
                onRevert = { color = previousColor }
            )
        }
    }
}
