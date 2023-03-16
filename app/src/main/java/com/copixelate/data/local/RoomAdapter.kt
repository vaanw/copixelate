package com.copixelate.data.local

import android.content.Context
import androidx.room.Room

class RoomAdapter(applicationContext: Context) {

    private val db = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, "copixelate-db"
    ).build()

    suspend fun getAllSpaces(): List<SpaceEntityWithArt> = db.spaceDao().getAllWithArt()
    suspend fun getSpace(id: Int): SpaceEntityWithArt = db.spaceDao().findByIdWithArt(id)
    suspend fun saveSpace(entity: SpaceEntity): Unit = db.spaceDao().insertAll(entity)

    suspend fun saveDrawing(entity: DrawingEntity) = db.drawingDao().insertAll()
    suspend fun savePalette(entity: PaletteEntity) = db.paletteDao().insertAll()

}
