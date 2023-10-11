package com.copixelate.ui.screens.art

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.copixelate.R
import com.copixelate.ui.animation.AnimationCatalog.toolbarEnter
import com.copixelate.ui.animation.AnimationCatalog.toolbarExit

data class HistoryAvailability(
    val drawingUndo: Boolean = false,
    val drawingRedo: Boolean = false,
    val paletteUndo: Boolean = false,
    val paletteRedo: Boolean = false
)

@Composable
internal fun DrawingToolbar(
    transformable: Boolean,
    expanded: Boolean,
    historyAvailability: HistoryAvailability,
    onClickEnableTransform: (transformEnabled: Boolean) -> Unit,
    onClickExpand: (expanded: Boolean) -> Unit,
    onClickDrawingHistory: (redo: Boolean) -> Unit,
    onClickPaletteHistory: (redo: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = CircleShape
            )
    ) {

        AnimatedVisibility(
            visible = expanded,
            enter = toolbarEnter,
            exit = toolbarExit
        ) {

            Row {
                // Undo palette history button
                IconButton(
                    enabled = historyAvailability.paletteUndo,
                    onClick = { onClickPaletteHistory(false) },
                ) {
                    Icon(
                        imageVector = Icons.Default.Undo,
                        contentDescription = "Undo palette edit"
                    )
                }
                // Palette icon
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Palette history",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(16.dp)
                )
                // Redo palette history button
                IconButton(
                    enabled = historyAvailability.paletteRedo,
                    onClick = { onClickPaletteHistory(true) },
                ) {
                    Icon(
                        imageVector = Icons.Default.Redo,
                        contentDescription = "Redo palette edit"
                    )
                }

                // Undo drawing history button
                IconButton(
                    enabled = historyAvailability.drawingUndo,
                    onClick = { onClickDrawingHistory(false) },
                ) {
                    Icon(
                        imageVector = Icons.Default.Undo,
                        contentDescription = "Undo draw"
                    )
                }
                // Drawing icon
                Icon(
                    imageVector = Icons.Default.Draw,
                    contentDescription = "Draw history",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(16.dp)
                )

                // Redo drawing history button
                IconButton(
                    enabled = historyAvailability.drawingRedo,
                    onClick = { onClickDrawingHistory(true) },
                ) {
                    Icon(
                        imageVector = Icons.Default.Redo,
                        contentDescription = "Redo draw"
                    )
                }

            } // End Row

        } // End AnimatedVisibility

        // History toggle button
        IconToggleButton(
            checked = expanded,
            onCheckedChange = { newValue ->
                onClickExpand(newValue)
            },
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = "Undo draw",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
        }

        // Enable transformable-mode (pan & zoom)
        IconToggleButton(
            checked = transformable,
            onCheckedChange = { newValue: Boolean ->
                onClickEnableTransform(newValue)
            },
            colors = IconButtonDefaults.iconToggleButtonColors(
                containerColor = MaterialTheme.colorScheme.background,
                checkedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            )
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.drag_pan),
                contentDescription = "Enable pan and zoom"
            )
        } // End IconToggleButton

    } // End Row

} // End DrawingToolbar


@Preview
@Composable
internal fun DrawingToolbarPreview() {

    var expanded by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        DrawingToolbar(
            transformable = true,
            expanded = expanded,
            historyAvailability = HistoryAvailability(
                drawingUndo = true,
                drawingRedo = false,
                paletteUndo = true,
                paletteRedo = false
            ),
            onClickEnableTransform = {},
            onClickExpand = { newValue ->
                expanded = newValue
            },
            onClickDrawingHistory = {},
            onClickPaletteHistory = {},
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    } // End Box

} // End DrawingToolbarPreview
