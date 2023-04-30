package com.copixelate.data.model

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
