package com.copixelate.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.copixelate.art.ArtSpace
import com.copixelate.data.model.SpaceModel
import com.copixelate.data.repo.toArtSpace
import com.copixelate.data.repo.toSpaceModel
import com.copixelate.ui.util.PreviewSurface
import com.copixelate.viewmodel.LibraryViewModel

@Composable
fun LibraryScreen(libraryViewModel: LibraryViewModel) {
    LibraryScreenContent(
        spaces = libraryViewModel.allSpaces.collectAsState().value
    )
}

@Composable
fun LibraryScreenContent(
    spaces: List<SpaceModel>
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn() {
            items(items = spaces) { spaceModel ->
                LibraryArtSpaceItem(spaceModel = spaceModel)
            }
        }
        AddItemFab(modifier = Modifier.align(Alignment.BottomEnd))
    }

}

@Composable
fun LibraryArtSpaceItem(spaceModel: SpaceModel) {
    val artSpace = spaceModel.toArtSpace()
    BitmapImage(
        pixelGrid = artSpace.state.colorDrawing,
        contentDescription = "Localized description",
        contentScale = ContentScale.FillWidth,
        modifier = Modifier.fillMaxWidth()
    )
    BitmapImage(
        pixelGrid = artSpace.state.palette,
        contentDescription = "Localized description",
        contentScale = ContentScale.FillWidth,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun AddItemFab(modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = { /* do something */ },
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
                ArtSpace().toSpaceModel(),
                ArtSpace().toSpaceModel(),
                ArtSpace().toSpaceModel(),
            )
        )
    }
}
