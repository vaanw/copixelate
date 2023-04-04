package com.copixelate.data.model

data class IDModel(
    val localID: Long? = null,
    val remoteID: String? = null,
)

data class SpaceModel(
    val id: IDModel = IDModel(),
    val drawing: DrawingModel,
    val palette: PaletteModel,
) {
    override fun equals(other: Any?): Boolean {
        if(other !is SpaceModel) return false
        return this.id.localID == other.id.localID
    }

    override fun hashCode(): Int {
        return id.localID.hashCode()
    }
}

data class DrawingModel(
    val id: IDModel = IDModel(),
    val size: SizeModel = SizeModel(),
    val pixels: List<Int> = emptyList(),
)

data class PaletteModel(
    val id: IDModel = IDModel(),
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
