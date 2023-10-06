package com.copixelate.ui.screens.library

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.copixelate.data.model.SpaceModel
import com.copixelate.data.model.toArtSpace
import com.copixelate.ui.animation.AnimationCatalog.libraryItemEnter
import com.copixelate.ui.animation.AnimationCatalog.libraryItemExit
import com.copixelate.ui.common.AddItemFab
import com.copixelate.ui.common.BitmapImage
import com.copixelate.ui.nav.NavInfo
import com.copixelate.ui.nav.navigateTopLevel
import com.copixelate.ui.util.PreviewSurface
import com.copixelate.ui.util.ScreenSurface
import com.copixelate.ui.util.generateDefaultArt
import com.copixelate.ui.util.toDp
import com.copixelate.viewmodel.ActivityViewModel
import com.copixelate.viewmodel.LibraryViewModel

@Composable
fun LibraryScreen(
    navController: NavHostController,
    libraryViewModel: LibraryViewModel,
    activityViewModel: ActivityViewModel
) {

    ScreenSurface {
        LibraryScreenContent(
            spaces = libraryViewModel.allSpaces.collectAsState().value,
            onCreate = { width, height, paletteSize ->
                libraryViewModel.createNewArtSpace(width, height, paletteSize)
            },
            onDelete = { spaceModel ->
                libraryViewModel.deleteArtSpace(spaceModel)
            },
            onOpen = { spaceModel ->
                libraryViewModel.updateCurrentSpaceId(spaceModel.id) {
                    navController.navigateTopLevel(navInfo = NavInfo.Art)
                }
            },
            onExport = { spaceModel, fileName, scaleFactor ->
                libraryViewModel.exportSpace(spaceModel, fileName, scaleFactor)
            },
            onShare = { spaceModel ->
                activityViewModel.shareSpace(spaceModel, 10)
            }
        ) // End LibraryScreenContent
    } // End ScreenSurface

} // End LibraryScreen

@Composable
private fun LibraryScreenContent(
    spaces: List<SpaceModel>,
    onCreate: (width: Int, height: Int, paletteSize: Int) -> Unit,
    onDelete: (SpaceModel) -> Unit,
    onOpen: (SpaceModel) -> Unit,
    onExport: (SpaceModel, String, Int) -> Unit,
    onShare: (SpaceModel) -> Unit,
) {

    var cachedSpaces by remember { mutableStateOf(spaces) }

    val workingSpaces = remember(spaces, cachedSpaces) {
        spaces
            .union(cachedSpaces)
            .toList()
            .sortedBy { it.id }
    }

    val addedItems = remember(spaces, cachedSpaces) {
        spaces.minus(cachedSpaces.toSet())
    }
    val removedItems = remember(spaces, cachedSpaces) {
        cachedSpaces.minus(spaces.toSet())
    }

    var layoutReady by remember { mutableStateOf(false) }

    val cachedLazyListState = rememberLazyListState()

    val lazyListState = remember(layoutReady) {
        when (layoutReady) {
            true -> cachedLazyListState
            false -> LazyListState()
        }
    }

    var fabHeight by remember { mutableIntStateOf(0) }

    var showCreateDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    var spaceModelToExport by remember { mutableStateOf(SpaceModel()) }

    var createJustOccurred by remember { mutableStateOf(false) }

    // Scroll to the bottom when a new item is added
    LaunchedEffect(spaces.size) {
        if (createJustOccurred) {
            lazyListState.scrollToItem(
                index = workingSpaces.lastIndex,
                scrollOffset = fabHeight
            )
            createJustOccurred = false
        }
    }

    // Library items
    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(
            bottom = fabHeight.toDp() + 16.dp + 8.dp,
            top = 8.dp, start = 16.dp, end = 16.dp
        ),
        modifier = Modifier
            .fillMaxSize()
    ) {

        items(
            items = workingSpaces,
            key = { item -> item.id.localId }
        ) { spaceModel ->

            val wasAdded = remember(addedItems) { addedItems.contains(spaceModel) }
            val wasRemoved = remember(removedItems) { removedItems.contains(spaceModel) }
            val isDynamic = remember(wasAdded, wasRemoved) { wasRemoved || wasAdded }
            val initialVisibility = remember(wasAdded, wasRemoved) { wasRemoved || !wasAdded }

            val visibleState = remember {
                MutableTransitionState(initialVisibility).apply {
                    // Start animation immediate if this is a new item
                    if (wasAdded) targetState = true
                }
            }

            AnimatedVisibility(
                visibleState = visibleState,
                enter = libraryItemEnter,
                exit = libraryItemExit,
            ) {

                LibraryArtSpaceItem(
                    spaceModel = spaceModel,
                    onDelete = { spaceModel ->
                        visibleState.targetState = false
                        onDelete(spaceModel)
                    },
                    onOpen = onOpen,
                    onExport = { model, fileName ->
                        spaceModelToExport = model
                        showExportDialog = true
                    },
                    onShare = onShare,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )

            } // End AnimatedVisibility

            val isAnimationComplete = remember(visibleState.isIdle, isDynamic) {
                visibleState.isIdle && isDynamic
            }

            if (isAnimationComplete) {
                SideEffect {
                    when {
                        // Visible
                        visibleState.currentState ->
                            if (wasAdded) cachedSpaces += spaceModel
                        // Invisible
                        !visibleState.currentState ->
                            if (wasRemoved) cachedSpaces -= spaceModel
                    } // End when
                } // End SideEffect

            } // End if

        } // End itemsIndexed

    } // End LazyColumn


    // Fab, opens dialog to create item
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AddItemFab(
            onClick = { showCreateDialog = true },
            modifier = Modifier
                .onSizeChanged { intSize ->
                    fabHeight = intSize.height
                    layoutReady = true
                }
                .align(Alignment.BottomEnd)
        )
    }

    // Dialog, create item
    if (showCreateDialog)
        CreateItemDialog(
            onCreate = { width, height, paletteSize ->
                onCreate(width, height, paletteSize)
                createJustOccurred = true
            },
            onCancel = { showCreateDialog = false }
        )

    // Dialog, export image
    if (showExportDialog)
        ExportImageDialog(
            onExport = { scaleFactor ->
                onExport(
                    spaceModelToExport,
                    "copixelate-tmp.png",
                    scaleFactor
                )
            },
            onCancel = { showExportDialog = false },
            size = spaceModelToExport.drawing.size
        )

}

@Composable
private fun LibraryArtSpaceItem(
    spaceModel: SpaceModel,
    onDelete: (SpaceModel) -> Unit,
    onOpen: (SpaceModel) -> Unit,
    onExport: (SpaceModel, String) -> Unit,
    onShare: (SpaceModel) -> Unit,
    modifier: Modifier = Modifier
) {

    val artSpace = remember { spaceModel.toArtSpace() }

    Card(
        modifier = modifier
            .fillMaxWidth()
    ) {

        Column {
            // Drawing image
            BitmapImage(
                pixelGrid = artSpace.state.colorDrawing,
                contentDescription = "Localized description",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
            )
            // Icon button row
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 4.dp)
            ) {
                // Delete icon button
                IconButton(
                    onClick = { onDelete(spaceModel) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Localized description"
                    )
                }
                // Dominant-hand icons
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Export icon button
                    IconButton(
                        onClick = {
                            onExport(spaceModel, "copixelate-tmp-export.png")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = "Localized description"
                        )
                    }
                    // Share icon button
                    IconButton(
                        onClick = {
                            onShare(spaceModel)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Localized description"
                        )
                    }
                    // Open icon button
                    IconButton(
                        onClick = { onOpen(spaceModel) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Draw,
                            contentDescription = "Localized description"
                        )
                    }
                }

            } // End Box

        } // End Column

    } // End Card

} // LibraryArtSpaceItem


@Preview
@Composable
private fun LibraryScreenPreview() {
    PreviewSurface {
        val fauxModel = SpaceModel()
            .generateDefaultArt(16, 8, 2)
        LibraryScreenContent(
            spaces = List(4) { fauxModel },
            onCreate = { _, _, _ -> },
            onDelete = { _ -> },
            onOpen = { _ -> },
            onExport = { _, _, _ -> },
            onShare = { _ -> },
        )
    }
}
