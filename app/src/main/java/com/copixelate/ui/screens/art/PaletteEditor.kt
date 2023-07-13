package com.copixelate.ui.screens.art

import android.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.graphics.ColorUtils
import com.copixelate.ui.components.BitmapImage
import com.copixelate.ui.util.PreviewSurface

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
fun PaletteEditor(
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
        PaletteEditorSlider(
            value = colorComponent1,
            valueRange = 0f..360f,
            scale = 1/3.6f,
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
        PaletteEditorSlider(
            value = colorComponent2,
            valueRange = 0f..1f,
            scale = 100f,
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
        PaletteEditorSlider(
            value = colorComponent3,
            valueRange = 0f..1f,
            scale = 100f,
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

            // Revert preview
            Row(
                modifier = Modifier
                    .weight(1f)
            ) {

                BitmapImage(
                    color = previousColor,
                    contentScale = ContentScale.FillBounds,
                    contentDescription = "Localized Description",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                BitmapImage(
                    color = color,
                    contentScale = ContentScale.FillBounds,
                    contentDescription = "Localized Description",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }

            // Revert button
            TextButton(
                onClick = {
                    revert()
                    onRevert()
                }
            ) { Text(text = "Revert") }

        }

    }
}

@Composable
fun PaletteEditorSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    scale: Float = 1f,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    colors: List<Int>
) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier
                .weight(weight = 1f)
                .zIndex(1f)
        ) {

            // Color spectrum
            Row(
                modifier = Modifier
                    .height(30.dp)
            ) {
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
                onValueChange = onValueChange
            )
        } // End Column

        Column{

            // Increase icon button
            IconButton(
                onClick = {
                    onValueChange(value + 1 / scale)
                },
                enabled = value < valueRange.endInclusive,
                modifier = Modifier
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = ""
                )
            }

            // Decrease icon button
            IconButton(
                onClick = {
                    onValueChange(value - 1 / scale)
                },
                enabled = value > valueRange.start,
                modifier = Modifier
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = ""
                )
            }

        }// End Column
    } // End Row
} // End PaletteToolSlider

@Preview
@Composable
private fun PaletteEditorPreview() {
    PreviewSurface {

        val previousColor = Color.MAGENTA
        var color by remember { mutableStateOf(previousColor) }
        PaletteEditor(
            color = color,
            previousColor = previousColor,
            onUpdateColor = { newColor -> color = newColor },
            onRevert = { color = previousColor }
        )
    }
}

@Preview
@Composable
private fun PaletteEditorPreviewAlt() {
    PreviewSurface {
        Surface(color = MaterialTheme.colorScheme.surfaceVariant) {

            val previousColor = Color.BLUE
            var color by remember { mutableStateOf(previousColor) }
            PaletteEditor(
                color = color,
                previousColor = previousColor,
                onUpdateColor = { newColor -> color = newColor },
                onRevert = { color = previousColor }
            )
        }
    }
}
