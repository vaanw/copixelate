package com.copixelate.data.repo

import android.content.Context
import com.copixelate.data.room.*
import com.copixelate.data.model.*
import com.copixelate.data.firebase.FirebaseAdapter

class ArtRepo(applicationContext: Context) {

    private val local: RoomAdapter = RoomAdapter(applicationContext)
    private val remote: FirebaseAdapter = FirebaseAdapter()

    suspend fun stub() {

    }

    suspend fun getAllSpaces(): List<SpaceModel> =
        local.getAllSpaces().map { entity ->
            entity.toModel()
        }

    suspend fun getSpaceByID(model: IDModel): SpaceModel =
        local.getSpace(model.localID!!).toModel()

    suspend fun saveSpace(model: SpaceModel) {
        local.saveSpace(model.toEntity())
    }

    suspend fun saveDrawing(model: DrawingModel) {
        local.saveDrawing(model.toEntity())
    }

    suspend fun savePalette(model: PaletteModel) {
        local.savePalette(model.toEntity())
    }

}


private fun Int.toIDModel() = IDModel(localID = this)

private fun IntArray.toSizeModel() = SizeModel(x = this[0], y = this[1])

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

private fun SizeModel.toIntArray() = intArrayOf(x, y)

private fun SpaceModel.toEntity() = SpaceEntity(
    id = id.localID ?: 0,
    drawingId = drawing.id.localID ?: 0,
    paletteId = palette.id.localID ?: 0,
    remoteKey = id.remoteID
)

private fun DrawingModel.toEntity() = DrawingEntity(
    id = id.localID ?: 0,
    pixels = pixels,
    size = this.size.toIntArray(),
    remoteKey = id.remoteID
)

private fun PaletteModel.toEntity() = PaletteEntity(
    id = id.localID ?: 0,
    pixels = pixels,
    size = this.size.toIntArray(),
    remoteKey = id.remoteID
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
