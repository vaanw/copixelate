package com.copixelate.data.room

import androidx.room.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

@Database(
    entities = [SpaceEntity::class, DrawingEntity::class, PaletteEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun spaceDao(): SpaceDao
    abstract fun drawingDao(): DrawingDao
    abstract fun paletteDao(): PaletteDao
}

class Converters {
    @TypeConverter
    fun serializeIntArray(value: IntArray): String = Json.encodeToString(value)

    @TypeConverter
    fun deserializeIntArray(value: String): IntArray = Json.decodeFromString(value)
}


@Entity(tableName = "space")
data class SpaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "drawing_id") val drawingId: Int,
    @ColumnInfo(name = "palette_id") val paletteId: Int,
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
interface SpaceDao {

    @Query("SELECT * FROM space")
    suspend fun getAll(): List<SpaceEntity>

    @Transaction
    @Query("SELECT * FROM space")
    suspend fun getAllWithArt(): List<SpaceEntityWithArt>

    @Query("SELECT * FROM space WHERE id = (:entityId) LIMIT 1")
    suspend fun findById(entityId: Int): SpaceEntity

    @Transaction
    @Query("SELECT * FROM space WHERE id = (:entityId) LIMIT 1")
    suspend fun findByIdWithArt(entityId: Int): SpaceEntityWithArt

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg entities: SpaceEntity)

    @Delete
    suspend fun delete(entity: SpaceEntity)
}

@Entity(tableName = "drawing")
class DrawingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "pixels") val pixels: IntArray,
    @ColumnInfo(name = "size") val size: IntArray,
    @ColumnInfo(name = "remote_key") val remoteKey: String?
)

@Dao
interface DrawingDao {
    @Query("SELECT * FROM drawing")
    suspend fun getAll(): List<DrawingEntity>

    @Query("SELECT * FROM drawing WHERE id = (:entityId) LIMIT 1")
    suspend fun findById(entityId: Int): DrawingEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg entities: DrawingEntity)

    @Delete
    suspend fun delete(entity: DrawingEntity)
}

@Entity(tableName = "palette")
class PaletteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "pixels") val pixels: IntArray,
    @ColumnInfo(name = "size") val size: IntArray,
    @ColumnInfo(name = "remote_key") val remoteKey: String?
)

@Dao
interface PaletteDao {
    @Query("SELECT * FROM palette")
    suspend fun getAll(): List<PaletteEntity>

    @Query("SELECT * FROM palette WHERE id = (:entityId) LIMIT 1")
    suspend fun findById(entityId: Int): PaletteEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg entities: PaletteEntity)

    @Delete
    suspend fun delete(entity: PaletteEntity)
}
