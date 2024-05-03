package com.copixelate.ui.screens.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.copixelate.data.model.SizeModel
import com.copixelate.ui.icon.IconCatalog
import com.copixelate.ui.util.PreviewSurface


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportImageDialog(
    onExport: (scaleFactor: Int) -> Unit,
    onCancel: () -> Unit,
    size: SizeModel
) {

    var scaleFactor by rememberSaveable { mutableIntStateOf(1) }

    BasicAlertDialog(onDismissRequest = onCancel) {

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

                // Dialog title icon
                Icon(
                    imageVector = IconCatalog.save,
                    contentDescription = "Localized description",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(all = 8.dp)
                )

                // Dialog title
                Text(
                    text = "Export image",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Scale factor editor
                IntEditor(
                    label = "Scale",
                    value = scaleFactor,
                    onValueChange = { newValue ->
                        scaleFactor = newValue
                    },
                    valueRange = 1..100,
                    superDelta = 5
                )

                // Output resolution info text
                Text(
                    text = "Output resolution",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = "${size.x * scaleFactor} x ${size.y * scaleFactor}",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(all = 4.dp)
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
                            onExport(scaleFactor)
                            onCancel()
                        },
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                    ) {
                        Text("Export")
                    }
                }

            } // End Column

        } // End Surface

    } // End AlertDialog

} // End AddItemDialog

@Preview
@Composable
private fun ExportImageDialogPreview() {
    PreviewSurface {
        ExportImageDialog(
            onExport = { },
            onCancel = { },
            size = SizeModel(32, 32)
        )
    }
}
