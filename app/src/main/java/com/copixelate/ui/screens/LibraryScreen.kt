package com.copixelate.ui.screens

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.copixelate.art.ArtSpace
import com.copixelate.data.model.SpaceModel
import com.copixelate.data.model.toArtSpace
import com.copixelate.data.model.toModel
import com.copixelate.ui.components.BitmapImage
import com.copixelate.ui.nav.NavInfo
import com.copixelate.ui.nav.navigate
import com.copixelate.ui.util.PreviewSurface
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
            navController.navigate(navInfo = NavInfo.Art)
        }
    )
}

@Composable
private fun LibraryScreenContent(
    spaces: List<SpaceModel>,
    onCreate: (width: Int, height: Int, paletteSize: Int) -> Unit,
    onDelete: (SpaceModel) -> Unit,
    onOpen: (SpaceModel) -> Unit,
) {

    val scrollState = rememberLazyListState()
    var addItemJustOccurred by remember { mutableStateOf(false) }

    var fabHeight by remember { mutableStateOf(0) }
    val fabClearance = fabHeight.toDp() + 16.dp + 16.dp

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

    var openDialog by remember { mutableStateOf(false) }

    // Fab, opens dialog to create item
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AddItemFab(
            onClick = { openDialog = true },
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    fabHeight = coordinates.size.height
                }
                .align(Alignment.BottomEnd)
        )
    }

    // Dialog, creates item
    if (openDialog)
        CreateItemDialog(
            onCreate = onCreate,
            onCancel = { openDialog = false }
        )

}

@Composable
private fun LibraryArtSpaceItem(
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

} // LibraryArtSpaceItem

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateItemDialog(
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
private fun LibraryScreenPreview() {
    PreviewSurface {
        val fauxModel = ArtSpace().toModel()
        LibraryScreenContent(
            spaces = listOf(fauxModel, fauxModel, fauxModel),
            onCreate = { _, _, _ -> },
            onDelete = {},
            onOpen = {},
        )
    }
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
