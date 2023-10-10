package com.copixelate.ui.screens.art

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class HistoryAvailability(
    val drawingUndo: Boolean = false,
    val drawingRedo: Boolean = false,
    val paletteUndo: Boolean = false,
    val paletteRedo: Boolean = false
)

@Composable
internal fun HistoryToolBar(
    onClickHistory: (Boolean) -> Unit,
    availability: HistoryAvailability,
    modifier: Modifier = Modifier
) {

    Row(modifier = modifier) {

        // Undo drawing history button
        IconButton(
            enabled = availability.drawingUndo,
            onClick = { onClickHistory(false) },
        ) {
            Icon(
                imageVector = Icons.Default.Undo,
                contentDescription = "Undo draw"
            )
        }

        // Redo drawing history button
        IconButton(
            enabled = availability.drawingRedo,
            onClick = { onClickHistory(true) },
        ) {
            Icon(
                imageVector = Icons.Default.Redo,
                contentDescription = "Redo draw"
            )
        }

    } // End Row

} // End HistoryToolBar
