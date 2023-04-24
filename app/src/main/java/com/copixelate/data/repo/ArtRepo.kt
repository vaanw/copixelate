package com.copixelate.data.repo

import com.copixelate.art.ArtSpace
import com.copixelate.art.PixelGrid
import com.copixelate.art.Point
import com.copixelate.data.firebase.FirebaseAdapter
import com.copixelate.data.model.*
import com.copixelate.data.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ArtRepo(
    private val roomAdapter: RoomAdapter,
    private val firebaseAdapter: FirebaseAdapter = FirebaseAdapter()
) {

    suspend fun saveSpace(artSpace: ArtSpace): List<Long> =
        roomAdapter.saveSpace(artSpace.toSpaceModel().toEntityWithArt())

    fun allSpacesFlow(): Flow<List<SpaceModel>> =
        roomAdapter.allSpacesFlow().map { list ->
            list.map { entity ->
                entity.toModel()
            }
        }

    suspend fun getSpaceByIdOrDefault(id: Long): SpaceModel? =
        getSpaceById(id) ?: getDefaultSpace()

    private suspend fun getSpaceById(id: Long): SpaceModel? =
        roomAdapter.getSpace(id)?.toModel()

    private suspend fun getDefaultSpace(): SpaceModel? =
        roomAdapter.getDefaultSpace()?.toModel()

    suspend fun loseSpace(spaceModel: SpaceModel) =
        roomAdapter.loseSpace(spaceModel.toEntityWithArt())

}

fun ArtSpace.toSpaceModel() =
    SpaceModel(
        drawing = this.state.drawing.toDrawingModel(),
        palette = this.state.palette.toPaletteModel()
    )

fun SpaceModel.toArtSpace() = ArtSpace().apply {
    clear(
        drawingState = drawing.toPixelGrid(),
        paletteState = palette.toPixelGrid()
    )
}

private fun PixelGrid.toDrawingModel() =
    DrawingModel(
        size = this.size.toSizeModel(),
        pixels = this.pixels.asList()
    )

private fun PixelGrid.toPaletteModel() =
    PaletteModel(
        size = this.size.toSizeModel(),
        pixels = this.pixels.asList()
    )

private fun Point.toSizeModel() = SizeModel(x = x, y = y)

private fun Long.toIDModel() = IDModel(localID = this)

private fun List<Int>.toSizeModel() = SizeModel(x = this[0], y = this[1])

private fun DrawingEntity.toModel() = DrawingModel(
    id = IDModel(localID = id),
    size = size.toSizeModel(),
    pixels = pixels
)

private fun PaletteEntity.toModel() = PaletteModel(
    id = IDModel(localID = id),
    size = size.toSizeModel(),
    pixels = pixels
)

private fun SpaceEntityWithArt.toModel(): SpaceModel = SpaceModel(
    id = this.space.id.toIDModel(),
    drawing = drawing.toModel(),
    palette = palette.toModel(),
)

private fun SizeModel.toIntList() = listOf(x, y)

private fun SpaceModel.toEntity() = SpaceEntity(
    id = id.localID ?: 0,
    drawingId = drawing.id.localID ?: 0,
    paletteId = palette.id.localID ?: 0,
    remoteKey = id.remoteID
)

private fun SpaceModel.toEntityWithArt() = SpaceEntityWithArt(
    space = this.toEntity(),
    drawing = this.drawing.toEntity(),
    palette = this.palette.toEntity()
)

private fun DrawingModel.toEntity() = DrawingEntity(
    id = id.localID ?: 0,
    pixels = pixels,
    size = this.size.toIntList(),
    remoteKey = id.remoteID
)

private fun PaletteModel.toEntity() = PaletteEntity(
    id = id.localID ?: 0,
    pixels = pixels,
    size = this.size.toIntList(),
    remoteKey = id.remoteID
)

private fun DrawingModel.toPixelGrid() = PixelGrid(
    size = size.toPoint(),
    pixels = this.pixels.toIntArray()
)

private fun PaletteModel.toPixelGrid() = PixelGrid(
    size = size.toPoint(),
    pixels = this.pixels.toIntArray()
)

private fun SizeModel.toPoint() = Point(
    x = x,
    y = y
)

// ViewModel functions
// getAllSpaces
// getSpaceById
// getDrawingUpdateFlowById
// saveSpace, repo decides if it needs to be saved remotely

//    suspend fun getSpace(localID: Int): Flow<SpaceModel> {
//
//    }
//
//    suspend fun getAllDrawings(): Flow<List<SpaceModel>> {
//
//    }

//    suspend fun getRemoteDrawing(drawingId: String): DrawingModel =
//        coroutineScope {
//            val data = async { adapter.getDrawingData(drawingKey) }
//            val size = async { adapter.getSizeJson(drawingKey) }
//
//            DrawingModel(
//                size = size.await().toModel(),
//                pixels = data.await(),
//            )
//        }


//class ArtRepo {
//
//    suspend fun stub() {
//
//        if(Auth.state == Auth.State.SIGNED_OUT) return
//
//        coroutineScope {
//            Log.d("GET_SPACE", remoteDataSource.getSpace("space_1").toString())
//            Log.d("GET_DRAWING", remoteDataSource.getDrawing("drawing_1").toString())
//            Log.d("GET_PALETTE", remoteDataSource.getPalette("palette_1").toString())
//
//            launch {
//                remoteDataSource.getDrawingUpdateFlow("drawing_1").collect { model ->
//                    Log.d("GET_DRAWING_UPDATE", model.toString())
//                }
//            }
//
//            remoteDataSource.setDrawing(
//                "drawing_3",
//                DrawingModel(
//                    pixels = listOf(1, 2, 3, 4, 5, 6),
//                    size = SizeModel(2, 3)
//                )
//            )
//        }
//
//    }
//
//}


//class FirebaseDataSource : ArtRepo.RemoteDataSource {
//
//    private val adapter = FirebaseAdapter()
//
//    override suspend fun getSpace(spaceKey: String): SpaceModel =
//        adapter.getSpaceJson(spaceKey).toModel()
//
//    override suspend fun getDrawing(drawingKey: String): DrawingModel =
//        coroutineScope {
//            val data = async { adapter.getDrawingData(drawingKey) }
//            val size = async { adapter.getSizeJson(drawingKey) }
//
//            DrawingModel(
//                size = size.await().toModel(),
//                pixels = data.await(),
//            )
//        }
//
//    override suspend fun getDrawingUpdateFlow(drawingKey: String): Flow<UpdateModel> =
//        adapter.getDrawingDataUpdateFlow(drawingKey).map { pair ->
//            UpdateModel(key = pair.first, value = pair.second)
//        }
//
//    override suspend fun setDrawing(drawingKey: String, drawingModel: DrawingModel) {
//        coroutineScope {
//            awaitAll(
//                async { adapter.setDrawingData(drawingKey, drawingModel.pixels) },
//                async { adapter.setSizeJson(drawingKey, drawingModel.size.toJson()) }
//            )
//        }
//    }
//
//    override suspend fun getPalette(paletteKey: String): PaletteModel =
//        coroutineScope {
//            val data = async { adapter.getPaletteData(paletteKey) }
//            val size = async { adapter.getSizeJson(paletteKey) }
//
//            PaletteModel(
//                size = size.await().toModel(),
//                pixels = data.await()
//            )
//        }
//
//    override suspend fun setPalette(paletteKey: String, paletteModel: PaletteModel) {
//        coroutineScope {
//            awaitAll(
//                async { adapter.setPaletteData(paletteKey, paletteModel.pixels) },
//                async { adapter.setSizeJson(paletteKey, paletteModel.size.toJson()) }
//            )
//        }
//    }
//
//
//
//}
