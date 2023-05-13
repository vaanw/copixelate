package com.copixelate.data.model

import com.copixelate.art.ArtSpace
import com.copixelate.data.repo.toDrawingModel
import com.copixelate.data.repo.toPaletteModel

data class IdModel(
    val localId: Long? = null,
    val remoteId: String? = null,
)

data class SpaceModel(
    val id: IdModel = IdModel(),
    val drawing: DrawingModel,
    val palette: PaletteModel,
)

data class DrawingModel(
    val id: IdModel = IdModel(),
    val size: SizeModel = SizeModel(),
    val pixels: List<Int> = emptyList(),
)

data class PaletteModel(
    val id: IdModel = IdModel(),
    val size: SizeModel = SizeModel(),
    val pixels: List<Int> = emptyList(),
)

data class SizeModel(
    val x: Int = 0,
    val y: Int = 0
)

data class UpdateModel(
    val key: Int,
    val value: Int
)

fun SpaceModel.copyFrom(artSpace: ArtSpace) = copy(
    drawing = artSpace.state.drawing.toDrawingModel().copy(id = this.drawing.id),
    palette = artSpace.state.palette.toPaletteModel().copy(id = this.palette.id)
)
