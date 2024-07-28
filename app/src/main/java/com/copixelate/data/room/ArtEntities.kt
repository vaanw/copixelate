package com.copixelate.data.room

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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
