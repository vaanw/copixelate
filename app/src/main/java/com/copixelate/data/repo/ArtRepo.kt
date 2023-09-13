package com.copixelate.data.repo

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import com.copixelate.data.bitmap.createScaledBitmap
import com.copixelate.data.firebase.FirebaseAdapter
import com.copixelate.data.model.SpaceModel
import com.copixelate.data.model.toEntityWithArt
import com.copixelate.data.model.toModel
import com.copixelate.data.room.RoomAdapter
import com.copixelate.data.room.SpaceEntityWithArt
import com.copixelate.data.storage.StorageAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

object ArtRepo {

    fun init(activity: Activity) {
        RoomAdapter.init(activity.applicationContext)
        StorageAdapter.init(activity.application)
    }

    private val room = RoomAdapter
    private val firebase = FirebaseAdapter
    private val storage = StorageAdapter

    suspend fun saveSpace(spaceModel: SpaceModel): Long =
        room.saveSpace(spaceModel.toEntityWithArt())[0]

    fun allSpacesFlow(): Flow<List<SpaceModel>> =
        room.allSpacesFlow().map { list ->
            list.map { entity: SpaceEntityWithArt ->
                entity.toModel()
            }
        }

    fun spaceByIdFlow(id: Long): Flow<SpaceModel?> =
        room.spaceByIdFlow(id = id).map { entity: SpaceEntityWithArt? ->
            entity?.toModel()
        }.distinctUntilChanged()

    suspend fun getDefaultSpace(): SpaceModel? =
        room.getDefaultSpace()?.toModel()

    suspend fun loseSpace(spaceModel: SpaceModel) =
        room.loseSpace(spaceModel.toEntityWithArt())

    fun exportBitmap(bitmap: Bitmap, fileName: String) {
        storage.writeNewImageFile(bitmap, fileName)
    }

    fun shareSpace(spaceModel: SpaceModel, scaleFactor: Int): Uri =
        storage.createTemporaryShareableImage(
            bitmap = createScaledBitmap(
                colorDrawingModel = spaceModel.colorDrawing,
                scaleFactor = scaleFactor
            )
        )

}


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
