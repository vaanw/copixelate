package com.copixelate.ui.screens.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.copixelate.ui.util.PreviewSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateItemDialog(
    onCreate: (width: Int, height: Int, paletteSize: Int) -> Unit,
    onCancel: () -> Unit
) {

    var width by rememberSaveable { mutableStateOf(32) }
    var height by remember { mutableStateOf(32) }
    var paletteSize by remember { mutableStateOf(4) }

    AlertDialog(onDismissRequest = onCancel) {

        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation,
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Icon(
                    imageVector = Icons.Outlined.AddPhotoAlternate,
                    contentDescription = "Localized description",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(all = 8.dp)
                )

                Text(
                    text = "New canvas",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Width editor
                DialogIntValueEditor(
                    label = "Width",
                    value = width,
                    onValueChange = { newValue -> width = newValue },
                    valueRange = 1..128
                )

                // Height control
                DialogIntValueEditor(
                    label = "Height",
                    value = height,
                    onValueChange = { newValue -> height = newValue },
                    valueRange = 1..128
                )

                // Palette size control
                DialogIntValueEditor(
                    label = "Colors",
                    value = paletteSize,
                    onValueChange = { newValue -> paletteSize = newValue },
                    valueRange = 2..32,
                    superDelta = 2,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Create & cancel buttons
                Row(modifier = Modifier.align(Alignment.End)) {
                    TextButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            onCreate(width, height, paletteSize)
                            onCancel()
                        },
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                    ) {
                        Text("Create")
                    }
                }

            }
        }
    } // End AlertDialog

} // End AddItemDialog

@Composable
private fun DialogIntValueEditor(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    valueRange: IntRange,
    modifier: Modifier = Modifier,
    superDelta: Int = 10
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
private fun IntTuner(
    value: Int,
    onValueChange: (Int) -> Unit,
    valueRange: IntRange,
    modifier: Modifier = Modifier,
    superDelta: Int = 10
) {

    val delta = 1
    val iconSize = 40.dp

    fun onValueChangeClamped(newValue: Int) =
        onValueChange(newValue.coerceIn(valueRange))

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
                onClick = { onValueChangeClamped(value - delta) },
                modifier = Modifier.size(iconSize),
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Localized description"
                )
            }

            // Super decrement button
            IconButton(
                onClick = { onValueChangeClamped(value - superDelta) },
                modifier = Modifier.size(iconSize)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardDoubleArrowLeft,
                    contentDescription = "Localized description"
                )
            }


            // Value display
            Text(
                text = value.toString(), // "%.${scale}f".format(input)
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(all = 4.dp)
                    .defaultMinSize(minWidth = 32.dp)
            )

            // Super increment button
            IconButton(
                onClick = { onValueChangeClamped(value + superDelta) },
                modifier = Modifier.size(iconSize),
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardDoubleArrowRight,
                    contentDescription = "Localized description"
                )
            }

            // Increment button
            IconButton(
                onClick = { onValueChangeClamped(value + delta) },
                modifier = Modifier.size(iconSize)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Localized description"
                )
            }

        } // End Row

    } // End Surface

} // End ValueEditor

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


@Preview
@Composable
private fun CreateItemDialogPreview() {
    PreviewSurface {
        CreateItemDialog(
            onCreate = { _, _, _ -> },
            onCancel = { }
        )
    }
}
