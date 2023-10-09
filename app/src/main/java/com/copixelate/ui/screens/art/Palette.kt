package com.copixelate.ui.screens.art

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.copixelate.art.PixelGrid
import com.copixelate.art.Point
import com.copixelate.data.model.PaletteModel
import com.copixelate.ui.common.BitmapImage
import com.copixelate.ui.util.toDp

@Composable
internal fun Palette(
    palette: PaletteModel,
    borderStroke: Dp,
    onTapPalette: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {

    var viewWidth by remember { mutableIntStateOf(0) }
    val swatchWidth = max(
        a = 48.dp,
        b = (viewWidth / palette.pixels.size).toDp()
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        // Palette border
        BitmapImage(
            color = palette.activeColor,
            contentDescription = "Drawing palette border",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        // Palette items
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .padding(borderStroke)
                .onSizeChanged { size ->
                    viewWidth = size.width
                }
        ) {
            itemsIndexed(items = palette.pixels) { index, color ->
                BitmapImage(
                    pixelGrid = PixelGrid(
                        pixels = intArrayOf(color),
                        size = Point(1)
                    ),
                    contentDescription = "Drawing palette border",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(swatchWidth)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { onTapPalette(index) }
                            )
                        }
                )
            } // End itemsIndexed
        } // End Lazy Row (Palette items)

    } // End Box

} // End Palette
