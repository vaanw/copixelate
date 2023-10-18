package com.copixelate.ui.screens.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.copixelate.ui.util.PreviewSurface
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun IntEditor(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    valueRange: IntRange,
    modifier: Modifier = Modifier,
    superDelta: Int = 8
) {

    Column(modifier = modifier) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            IntTuner(
                value = value,
                valueRange = valueRange,
                onValueChange = onValueChange,
                superDelta = superDelta,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        } // End Box

        IntSlider(
            value = value,
            valueRange = valueRange,
            onValueChange = onValueChange
        )
    } // End Column

} // End IntValueTuner

@Composable
private fun IntSlider(
    value: Int,
    valueRange: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {

    Slider(
        value = value.toFloat(),
        onValueChange = { newValue -> onValueChange(newValue.toInt()) },
        valueRange = valueRange.first.toFloat()..valueRange.last.toFloat(),
        modifier = modifier
    )
}

@Composable
private fun IntTuner(
    value: Int,
    onValueChange: (Int) -> Unit,
    valueRange: IntRange,
    modifier: Modifier = Modifier,
    superDelta: Int
) {

    val delta = 1
    val iconSize = 40.dp

    fun onValueChangeClamped(newValue: Int) =
        onValueChange(newValue.coerceIn(valueRange))

    fun increment(isDecrement: Boolean = false) {

        val newValue = when(isDecrement){
            false -> value + delta
            true -> value - delta
        }

        onValueChangeClamped(newValue)
    }

    fun superIncrement(isDecrement: Boolean = false) {

        val newValue = when(isDecrement){
            false -> value + superDelta
            true -> value - superDelta
        }

        val nearestMultiple = when (isDecrement) {
            false -> floor(newValue * 1f / superDelta)
            true -> ceil(newValue * 1f / superDelta)
        }.toInt() * superDelta

        onValueChangeClamped(nearestMultiple)
    }

    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = CircleShape,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Decrement button
            IconButton(
                onClick = { increment(true) },
                modifier = Modifier.size(iconSize),
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Localized description"
                )
            }

            // Super decrement button
            IconButton(
                onClick = { superIncrement(true) },
                modifier = Modifier.size(iconSize)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardDoubleArrowLeft,
                    contentDescription = "Localized description"
                )
            }


            // Value display
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(all = 4.dp)
                    .defaultMinSize(minWidth = 32.dp)
            )

            // Super increment button
            IconButton(
                onClick = { superIncrement() },
                modifier = Modifier.size(iconSize),
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardDoubleArrowRight,
                    contentDescription = "Localized description"
                )
            }

            // Increment button
            IconButton(
                onClick = { increment() },
                modifier = Modifier.size(iconSize)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Localized description"
                )
            }

        } // End Row

    } // End Surface

} // End IntTuner

@Preview
@Composable
private fun IntValueEditorPreview() {
    PreviewSurface {
        IntEditor(
            label= "Magic number",
            value = 32,
            onValueChange = {},
            valueRange = 1..128
        )
    }
}
