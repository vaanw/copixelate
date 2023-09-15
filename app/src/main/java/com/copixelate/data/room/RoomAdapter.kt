package com.copixelate.data.room

import android.app.Application
import androidx.room.Room
import kotlinx.coroutines.flow.Flow

object RoomAdapter {

    private lateinit var db: AppDatabase

    fun init(application: Application) {
        db = Room.databaseBuilder(
            application,
            AppDatabase::class.java, "copixelate-db"
        ).build()
    }

    fun allSpacesFlow(): Flow<List<SpaceEntityWithArt>> = db.artDao().allSpacesFlow()
    fun spaceByIdFlow(id: Long): Flow<SpaceEntityWithArt?> = db.artDao().spaceByIdFlow(id)

    suspend fun getDefaultSpace(): SpaceEntityWithArt? = db.artDao().findDefaultSpace()
    suspend fun saveSpace(entity: SpaceEntityWithArt): List<Long> = db.artDao().insertSpaces(entity)
    suspend fun loseSpace(entity: SpaceEntityWithArt) = db.artDao().deleteSpace(entity.space)

}
