package com.copixelate.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "remote_key") val remoteKey: String?,
    @ColumnInfo(name = "friend_code") val friendCode: String?
)

@Entity(
    tableName = "friend",
    indices = [Index(value = ["user_id"])],
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["id"],
        childColumns = ["user_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class FriendEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "invitation") val invitation: Boolean,
    @ColumnInfo(name = "display_name") val displayName: String?
)
