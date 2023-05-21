package com.copixelate.data.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Database(
    entities = [SpaceEntity::class, DrawingEntity::class, PaletteEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun artDao(): ArtDao
}

class Converters {
    @TypeConverter
    fun serializeIntList(value: List<Int>): String = Json.encodeToString(value)

    @TypeConverter
    fun deserializeIntList(value: String): List<Int> = Json.decodeFromString(value)
}

@Entity(tableName = "drawing")
class DrawingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "pixels") val pixels: List<Int>,
    @ColumnInfo(name = "size") val size: List<Int>,
    @ColumnInfo(name = "remote_key") val remoteKey: String?
)

@Entity(tableName = "palette")
class PaletteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "pixels") val pixels: List<Int>,
    @ColumnInfo(name = "size") val size: List<Int>,
    @ColumnInfo(name = "remote_key") val remoteKey: String?
)

@Entity(
    tableName = "space",
    indices = [
        Index(value = ["drawing_id"]),
        Index(value = ["palette_id"])
    ],
    foreignKeys = [ForeignKey(
        entity = DrawingEntity::class,
        parentColumns = ["id"],
        childColumns = ["drawing_id"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = PaletteEntity::class,
        parentColumns = ["id"],
        childColumns = ["palette_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class SpaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "drawing_id") var drawingId: Long,
    @ColumnInfo(name = "palette_id") var paletteId: Long,
    @ColumnInfo(name = "remote_key") val remoteKey: String?
)

data class SpaceEntityWithArt(
    @Embedded
    val space: SpaceEntity,
    @Relation(
        parentColumn = "drawing_id",
        entityColumn = "id"
    )
    val drawing: DrawingEntity,
    @Relation(
        parentColumn = "palette_id",
        entityColumn = "id"
    )
    val palette: PaletteEntity,
)

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
