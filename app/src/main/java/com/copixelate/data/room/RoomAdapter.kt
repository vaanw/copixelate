package com.copixelate.data.room

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow

class RoomAdapter(applicationContext: Context) {

    private val db = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, "copixelate-db"
    ).build()

    fun getAllSpaces(): Flow<List<SpaceEntityWithArt>> = db.artDao().getAllSpaces()
//    suspend fun getSpace(id: Int): SpaceEntityWithArt = db.artDao().findByIdWithArt(id)
    suspend fun saveSpace(entity: SpaceEntityWithArt): Unit = db.artDao().insertSpaces(entity)

//    suspend fun saveDrawing(entity: DrawingEntity) = db.artDao().insertAll()
//    suspend fun savePalette(entity: PaletteEntity) = db.artDao().insertAll()

}
