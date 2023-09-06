package com.copixelate.data.model

import android.graphics.Color
import com.copixelate.art.ArtSpace
import com.copixelate.art.PixelGrid
import com.copixelate.art.PixelRow
import com.copixelate.art.Point
import com.copixelate.data.room.DrawingEntity
import com.copixelate.data.room.PaletteEntity
import com.copixelate.data.room.SpaceEntity
import com.copixelate.data.room.SpaceEntityWithArt
import kotlin.random.Random

private const val DEFAULT_DRAWING_WIDTH = 32
private const val DEFAULT_DRAWING_HEIGHT = 32
private const val DEFAULT_PALETTE_SIZE = 6

data class IdModel(
    val localId: Long? = null,
    val remoteId: String? = null,
)

data class SpaceModel(
    val id: IdModel = IdModel(),
    val palette: PaletteModel = PaletteModel(),
    val drawing: DrawingModel = DrawingModel()
) {
    fun createDefaultArt(
        width: Int = DEFAULT_DRAWING_WIDTH,
        height: Int = DEFAULT_DRAWING_HEIGHT,
        paletteSize: Int = DEFAULT_PALETTE_SIZE
    ) = SpaceModel(
        palette = palette.createDefaultArt(paletteSize),
        drawing = drawing.createDefaultArt(width, height, paletteSize)
    )
}

data class DrawingModel(
    val id: IdModel = IdModel(),
    val size: SizeModel = SizeModel(),
    val pixels: List<Int> = emptyList(),
) {

    fun createDefaultArt(width: Int, height: Int, paletteSize: Int) = copy(
        size = SizeModel(x = width, y = height),
        pixels = List(
            size = width * height,
            init = { index: Int ->
                (index / 5) % paletteSize

            }
        )
    )

}

data class PaletteModel(
    val id: IdModel = IdModel(),
    val pixels: List<Int> = emptyList(),
    val activeIndex: Int = 0
) {
    val activeColor
        get() = pixels[activeIndex]

    fun createDefaultArt(size: Int) = copy(
        pixels = List(
            size = size,
            init = {
                val rand: Int = Random(System.nanoTime()).nextInt()
                Color.argb(
                    255,
                    Color.red(rand),
                    Color.green(rand),
                    Color.blue(rand)
                )
            }
        )
    )
}

data class SizeModel(
    val x: Int = 0,
    val y: Int = 0
)

data class UpdateModel(
    val key: Int,
    val value: Int
)

//
// ArtSpace
//
fun SpaceModel.toArtSpace() = ArtSpace().apply {
    clear(
        drawingState = drawing.toPixelGrid(),
        paletteState = palette.toPixelRow(),
    )
}

fun SpaceModel.copyFrom(artSpace: ArtSpace) = copy(
    drawing = artSpace.state.drawing.toModel().copy(id = this.drawing.id),
    palette = artSpace.state.palette.toModel().copy(id = this.palette.id)
)

// ArtSpace - Drawing
private fun PixelGrid.toModel() =
    DrawingModel(
        size = this.size.toSizeModel(),
        pixels = this.pixels.asList()
    )

private fun DrawingModel.toPixelGrid() = PixelGrid(
    size = size.toPoint(),
    pixels = this.pixels.toIntArray()
)

// ArtSpace - Palette
fun PixelRow.toModel() =
    PaletteModel(
        pixels = this.pixels.asList(),
        activeIndex = this.activeIndex
    )

private fun PaletteModel.toPixelRow() = PixelRow(
    pixels = this.pixels.toIntArray(),
    activeIndex = this.activeIndex,
)

// ArtSpace - Point
private fun SizeModel.toPoint() = Point(x = x, y = y)
private fun Point.toSizeModel() = SizeModel(x = x, y = y)


//
// Database Entities
//

// Primitive Types
private fun Long.toIDModel() = IdModel(localId = this)
private fun List<Int>.toSizeModel() = SizeModel(x = this[0], y = this[1])
private fun SizeModel.toIntList() = listOf(x, y)

// SpaceEntity
fun SpaceEntityWithArt.toModel(): SpaceModel = SpaceModel(
    id = this.space.id.toIDModel(),
    drawing = drawing.toModel(),
    palette = palette.toModel(),
)

fun SpaceModel.toEntityWithArt() = SpaceEntityWithArt(
    space = this.toEntity(),
    drawing = this.drawing.toEntity(),
    palette = this.palette.toEntity()
)

private fun SpaceModel.toEntity() = SpaceEntity(
    id = id.localId ?: 0,
    drawingId = drawing.id.localId ?: 0,
    paletteId = palette.id.localId ?: 0,
    remoteKey = id.remoteId
)

// DrawingEntity
private fun DrawingEntity.toModel() = DrawingModel(
    id = IdModel(localId = id),
    size = size.toSizeModel(),
    pixels = pixels
)

private fun DrawingModel.toEntity() = DrawingEntity(
    id = id.localId ?: 0,
    pixels = pixels,
    size = this.size.toIntList(),
    remoteKey = id.remoteId
)

// PaletteEntity
private fun PaletteEntity.toModel() = PaletteModel(
    id = IdModel(localId = id),
    pixels = pixels
)

private fun PaletteModel.toEntity() = PaletteEntity(
    id = id.localId ?: 0,
    pixels = pixels,
    remoteKey = id.remoteId
)
