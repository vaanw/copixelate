package com.copixelate.data.model

data class IDModel(
    val localID: Int? = null,
    val remoteID: String? = null,
)

data class SpaceModel(
    val id: IDModel = IDModel(),
    val drawing: DrawingModel,
    val palette: PaletteModel,
)

class DrawingModel(
    val id: IDModel = IDModel(),
    val size: SizeModel = SizeModel(),
    val pixels: IntArray = IntArray(0),
)

class PaletteModel(
    val id: IDModel = IDModel(),
    val size: SizeModel = SizeModel(),
    val pixels: IntArray = IntArray(0),
)

data class SizeModel(
    val x: Int = 0,
    val y: Int = 0
)

data class UpdateModel(
    val key: Int,
    val value: Int
)
