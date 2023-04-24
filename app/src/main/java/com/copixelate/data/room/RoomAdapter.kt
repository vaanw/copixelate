package com.copixelate.data.room

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow

class RoomAdapter(applicationContext: Context) {

    private val db = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, "copixelate-db"
    ).build()

    fun allSpacesFlow(): Flow<List<SpaceEntityWithArt>> = db.artDao().allSpacesFlow()
    suspend fun getSpace(id: Long): SpaceEntityWithArt? = db.artDao().findSpaceById(id)
    suspend fun getDefaultSpace(): SpaceEntityWithArt? = db.artDao().findDefaultSpace()
    suspend fun saveSpace(entity: SpaceEntityWithArt): List<Long> = db.artDao().insertSpaces(entity)
    suspend fun loseSpace(entity: SpaceEntityWithArt) = db.artDao().deleteSpace(entity.space)

}
