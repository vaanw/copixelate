package com.copixelate.ui.screens.art

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.copixelate.art.HistoryAvailability
import com.copixelate.ui.animation.AnimationCatalog
import com.copixelate.ui.icon.IconCatalog

@Composable
internal fun DrawingToolbar(
    transformable: Boolean,
    expanded: Boolean,
    drawingHistory: HistoryAvailability,
    paletteHistory: HistoryAvailability,
    onClickEnableTransform: (transformEnabled: Boolean) -> Unit,
    onClickExpand: (expanded: Boolean) -> Unit,
    onClickDrawingHistory: (redo: Boolean) -> Unit,
    onClickPaletteHistory: (redo: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    Row(modifier = modifier) {

        // Container needed for background
        Box() {

            // Background with padding
            Box(
                modifier = Modifier
                    .padding(all = 4.dp)
                    .matchParentSize()
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = CircleShape
                    )

            )

            // History icons
            Row {

                // Collapsible history icons
                AnimatedVisibility(
                    visible = expanded,
                    enter = AnimationCatalog.toolbarEnter,
                    exit = AnimationCatalog.toolbarExit
                ) {

                    Row {
                        // Undo palette history button
                        IconButton(
                            enabled = paletteHistory.undoAvailable,
                            onClick = { onClickPaletteHistory(false) },
                        ) {
                            Icon(
                                imageVector = IconCatalog.undo,
                                contentDescription = "Undo palette edit"
                            )
                        }
                        // Palette icon
                        Icon(
                            imageVector = IconCatalog.palette,
                            contentDescription = "Palette history",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .size(16.dp)
                        )
                        // Redo palette history button
                        IconButton(
                            enabled = paletteHistory.redoAvailable,
                            onClick = { onClickPaletteHistory(true) },
                        ) {
                            Icon(
                                imageVector = IconCatalog.redo,
                                contentDescription = "Redo palette edit"
                            )
                        }

                        // Undo drawing history button
                        IconButton(
                            enabled = drawingHistory.undoAvailable,
                            onClick = { onClickDrawingHistory(false) },
                        ) {
                            Icon(
                                imageVector = IconCatalog.undo,
                                contentDescription = "Undo draw"
                            )
                        }
                        // Drawing icon
                        Icon(
                            imageVector = IconCatalog.draw,
                            contentDescription = "Draw history",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .size(16.dp)
                        )

                        // Redo drawing history button
                        IconButton(
                            enabled = drawingHistory.redoAvailable,
                            onClick = { onClickDrawingHistory(true) },
                        ) {
                            Icon(
                                imageVector = IconCatalog.redo,
                                contentDescription = "Redo draw"
                            )
                        }

                    } // End Row

                } // End AnimatedVisibility

                // History collapse toggle button
                IconToggleButton(
                    checked = expanded,
                    onCheckedChange = { newValue ->
                        onClickExpand(newValue)
                    },
                ) {
                    Icon(
                        imageVector = IconCatalog.history,
                        contentDescription = "Expand/collapse history",
                        modifier = Modifier
                    )
                }

            } // End Row

        } // End Box

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
                imageVector = IconCatalog.dragPan(),
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
            drawingHistory = HistoryAvailability(undoAvailable = false, redoAvailable = true),
            paletteHistory = HistoryAvailability(undoAvailable = true, redoAvailable = false),
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
