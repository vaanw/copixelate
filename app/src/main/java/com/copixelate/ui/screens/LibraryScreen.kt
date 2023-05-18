package com.copixelate.ui.screens

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.copixelate.art.ArtSpace
import com.copixelate.data.model.SpaceModel
import com.copixelate.data.model.toArtSpace
import com.copixelate.data.model.toModel
import com.copixelate.nav.NavInfo
import com.copixelate.ui.components.BitmapImage
import com.copixelate.ui.util.PreviewSurface
import com.copixelate.ui.util.toDp
import com.copixelate.viewmodel.LibraryViewModel

@Composable
fun LibraryScreen(navController: NavHostController, libraryViewModel: LibraryViewModel) {
    LibraryScreenContent(
        spaces = libraryViewModel.allSpaces.collectAsState().value,
        onCreateNew = { libraryViewModel.saveArtSpace() },
        onDelete = { spaceModel ->
            libraryViewModel.loseArtSpace(spaceModel)
        },
        onOpen = { spaceModel ->
            libraryViewModel.updateCurrentSpaceId(spaceModel.id)
            navController.navigate(NavInfo.Art.route)
        }
    )
}

@Composable
fun LibraryScreenContent(
    spaces: List<SpaceModel>,
    onCreateNew: () -> Unit,
    onDelete: (SpaceModel) -> Unit,
    onOpen: (SpaceModel) -> Unit,
) {

    val scrollState = rememberLazyListState()
    var addItemJustOccurred by remember { mutableStateOf(false) }

    val fabHeight = remember { mutableStateOf(0) }
    val fabClearance =  fabHeight.value.toDp() + 16.dp + 16.dp

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
            )
        }

    }
    // Fab, add item
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AddItemFab(
            onClick = {
                onCreateNew()
                addItemJustOccurred = true
            },
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    fabHeight.value = coordinates.size.height
                }
                .align(Alignment.BottomEnd)
        )
    }

}

@Composable
fun LibraryArtSpaceItem(
    spaceModel: SpaceModel,
    isNew: Boolean,
    onDelete: (SpaceModel) -> Unit,
    onOpen: (SpaceModel) -> Unit
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
                    IconButton(onClick = { /* Handle click */ }) {
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

}

@Composable
fun AddItemFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Localized description"
        )
    }
}

@Preview
@Composable
fun LibraryScreenPreview() {
    PreviewSurface {
        LibraryScreenContent(
            spaces = listOf(
                ArtSpace().toModel(),
                ArtSpace().toModel(),
                ArtSpace().toModel(),
            ),
            onCreateNew = {},
            onDelete = {},
            onOpen = {},
        )
    }
}
