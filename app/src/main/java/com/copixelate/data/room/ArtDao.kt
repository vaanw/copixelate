package com.copixelate.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtDao {

    // Space Queries

    @Transaction
    @Query("SELECT * FROM space")
    fun allSpacesFlow(): Flow<List<SpaceEntityWithArt>>

    @Transaction
    @Query("SELECT * FROM space WHERE id = (:entityId) LIMIT 1")
    fun spaceByIdFlow(entityId: Long): Flow<SpaceEntityWithArt?>

    @Transaction
    @Query("SELECT * FROM space ORDER BY id ASC LIMIT 1")
    suspend fun findDefaultSpace(): SpaceEntityWithArt?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpaces(vararg entities: SpaceEntity): List<Long>

    @Transaction
    suspend fun insertSpaces(vararg spaces: SpaceEntityWithArt): List<Long> =
        mutableListOf<Long>().apply {
            spaces.forEach { entity ->
                entity.space.drawingId = insertDrawings(entity.drawing)[0]
                entity.space.paletteId = insertPalettes(entity.palette)[0]
                add(insertSpaces(entity.space)[0])
            }
        }

    @Delete
    suspend fun deleteSpace(entity: SpaceEntity)

    // Drawing Queries

    // @Query("SELECT * FROM drawing")
    // suspend fun getAllDrawings(): List<DrawingEntity>

    @Query("SELECT * FROM drawing WHERE id = (:entityId) LIMIT 1")
    suspend fun findDrawingById(entityId: Int): DrawingEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrawings(vararg entities: DrawingEntity): LongArray

    @Delete
    suspend fun deleteDrawing(entity: DrawingEntity)

    // Palette Queries

    // @Query("SELECT * FROM palette")
    // suspend fun getAllPalettes(): List<PaletteEntity>

    @Query("SELECT * FROM palette WHERE id = (:entityId) LIMIT 1")
    suspend fun findPaletteById(entityId: Int): PaletteEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPalettes(vararg entities: PaletteEntity): LongArray

    @Delete
    suspend fun deletePalette(entity: PaletteEntity)

}
