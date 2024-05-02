package com.copixelate.ui.screens.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.copixelate.ui.icon.IconCatalog
import com.copixelate.ui.util.PreviewSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateItemDialog(
    onCreate: (width: Int, height: Int, paletteSize: Int) -> Unit,
    onCancel: () -> Unit
) {

    var width by rememberSaveable { mutableStateOf(32) }
    var height by remember { mutableStateOf(32) }
    var paletteSize by remember { mutableStateOf(6) }

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
                    imageVector = IconCatalog.addPhoto,
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
                IntEditor(
                    label = "Width",
                    value = width,
                    onValueChange = { newValue -> width = newValue },
                    valueRange = 1..128
                )

                // Height control
                IntEditor(
                    label = "Height",
                    value = height,
                    onValueChange = { newValue -> height = newValue },
                    valueRange = 1..128
                )

                // Palette size control
                IntEditor(
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
