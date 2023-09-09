package com.copixelate.ui.screens.library

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.copixelate.data.model.SpaceModel
import com.copixelate.data.model.toArtSpace
import com.copixelate.ui.common.AddItemFab
import com.copixelate.ui.common.BitmapImage
import com.copixelate.ui.nav.NavInfo
import com.copixelate.ui.nav.navigateTopLevel
import com.copixelate.ui.util.PreviewSurface
import com.copixelate.ui.util.generateDefaultArt
import com.copixelate.ui.util.toDp
import com.copixelate.viewmodel.LibraryViewModel

@Composable
fun LibraryScreen(navController: NavHostController, libraryViewModel: LibraryViewModel) {
    LibraryScreenContent(
        spaces = libraryViewModel.allSpaces.collectAsState().value,
        onCreate = { width, height, paletteSize ->
            libraryViewModel.createNewArtSpace(width, height, paletteSize)
        },
        onDelete = { spaceModel ->
            libraryViewModel.deleteArtSpace(spaceModel)
        },
        onOpen = { spaceModel ->
            libraryViewModel.updateCurrentSpaceId(spaceModel.id)
            navController.navigateTopLevel(navInfo = NavInfo.Art)
        },
        onExport = { spaceModel, fileName, scaleFactor ->
            libraryViewModel.exportSpace(spaceModel, fileName, scaleFactor)
        }
    )
}

@Composable
private fun LibraryScreenContent(
    spaces: List<SpaceModel>,
    onCreate: (width: Int, height: Int, paletteSize: Int) -> Unit,
    onDelete: (SpaceModel) -> Unit,
    onOpen: (SpaceModel) -> Unit,
    onExport: (SpaceModel, String, Int) -> Unit,
) {

    val scrollState = rememberLazyListState()
    var addItemJustOccurred by remember { mutableStateOf(false) }

    var fabHeight by remember { mutableIntStateOf(0) }
    val fabClearance = fabHeight.toDp() + 16.dp + 16.dp

    var showCreateDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    var spaceModelToExport by remember { mutableStateOf(SpaceModel()) }

    // Scroll to the bottom when a new item is added
    LaunchedEffect(spaces.size) {
        if (addItemJustOccurred) {
            scrollState.scrollToItem(index = spaces.lastIndex)
            addItemJustOccurred = false
        }
    }

    // Library items
    LazyColumn(
        state = scrollState,
        contentPadding = PaddingValues(
            bottom = fabClearance,
            top = 16.dp, start = 16.dp, end = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        itemsIndexed(items = spaces) { index, spaceModel ->
            LibraryArtSpaceItem(
                spaceModel = spaceModel,
                isNew = addItemJustOccurred && (spaces.lastIndex == index),
                onDelete = onDelete,
                onOpen = onOpen,
                onExport = { model, fileName ->
                    spaceModelToExport = model
                    showExportDialog = true
                }
            )
        }

    }

    // Fab, opens dialog to create item
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AddItemFab(
            onClick = { showCreateDialog = true },
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    fabHeight = coordinates.size.height
                }
                .align(Alignment.BottomEnd)
        )
    }

    // Dialog, create item
    if (showCreateDialog)
        CreateItemDialog(
            onCreate = onCreate,
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
    isNew: Boolean,
    onDelete: (SpaceModel) -> Unit,
    onOpen: (SpaceModel) -> Unit,
    onExport: (SpaceModel, String) -> Unit,
) {

    val artSpace = spaceModel.toArtSpace()
    val cardShape = CardDefaults.shape

    // Opacity animation for newly created items, out -> in
    var targetAlpha by remember {
        mutableStateOf(
            when (isNew) {
                true -> 0f; false -> 1f
            }
        )
    }
    val animatedAlpha: Float by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearOutSlowInEasing,
        ),
    )
    // Begins the alpha float animation, 0f -> 1f
    if (isNew) targetAlpha = 1f

    Card(
        shape = cardShape,
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha = animatedAlpha)
    ) {

        Column {
            // Drawing image
            BitmapImage(
                pixelGrid = artSpace.state.colorDrawing,
                contentDescription = "Localized description",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(cardShape),
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
                            onExport(spaceModel, "copixelate-tmp.png")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = "Localized description"
                        )
                    }
                    // Share icon button
                    IconButton(onClick = { /* Handle click */ }) {
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
        )
    }
}
