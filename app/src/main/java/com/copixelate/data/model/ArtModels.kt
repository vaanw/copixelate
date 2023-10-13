package com.copixelate.data.model

import com.copixelate.art.ArtSpace
import com.copixelate.art.PixelGrid
import com.copixelate.art.PixelRow
import com.copixelate.art.Point
import com.copixelate.data.room.DrawingEntity
import com.copixelate.data.room.PaletteEntity
import com.copixelate.data.room.SpaceEntity
import com.copixelate.data.room.SpaceEntityWithArt

data class IdModel(
    val localId: Long = 0,
    val remoteId: String? = null,
) : Comparable<IdModel> {
    override fun compareTo(other: IdModel): Int {
        return localId.compareTo(other.localId)
    }
}

data class SpaceModel(
    val id: IdModel = IdModel(),
    val palette: PaletteModel = PaletteModel(),
    val drawing: DrawingModel = DrawingModel()
) {
    val colorDrawing: DrawingModel
        get() = drawing.run {
            copy(
                pixels = List(pixels.size) { index ->
                    palette.pixels[pixels[index]]
                }
            )
        }

}

data class DrawingModel(
    val id: IdModel = IdModel(),
    val size: SizeModel = SizeModel(),
    val pixels: List<Int> = emptyList(),
)

data class PaletteModel(
    val id: IdModel = IdModel(),
    val pixels: List<Int> = emptyList(),
    val activeIndex: Int = 0
) {
    val activeColor
        get() = pixels[activeIndex]
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
// ArtSpace Conversion
//

fun SpaceModel.toArtSpace() = ArtSpace().apply {
    clear(
        drawingState = drawing.toPixelGrid(),
        paletteState = palette.toPixelRow(),
    )
}

fun SpaceModel.copyFrom(artSpace: ArtSpace) = copy(
    drawing = artSpace.state.indexDrawing.toModel().copy(id = this.drawing.id),
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
// Database Conversion
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
    id = id.localId,
    drawingId = drawing.id.localId,
    paletteId = palette.id.localId,
    remoteKey = id.remoteId
)

// DrawingEntity
private fun DrawingEntity.toModel() = DrawingModel(
    id = IdModel(localId = id),
    size = size.toSizeModel(),
    pixels = pixels
)

private fun DrawingModel.toEntity() = DrawingEntity(
    id = id.localId,
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
    id = id.localId,
    pixels = pixels,
    remoteKey = id.remoteId
)
