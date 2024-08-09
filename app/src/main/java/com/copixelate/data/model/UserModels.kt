package com.copixelate.data.model

import com.copixelate.data.room.UserEntity

data class UserModel (
    val id: IdModel = IdModel(),
    val friendCode: String? = null,
)

data class FriendModel (
    val id: IdModel = IdModel(),
    val displayName: String
)

//
// Database Conversion
//

// UserEntity
fun UserEntity.toModel(): UserModel = UserModel(
    id = IdModel(id, remoteKey),
    friendCode = friendCode
)

fun UserModel.toEntity(): UserEntity = UserEntity(
    id = id.localId,
    remoteKey = id.remoteKey,
    friendCode = friendCode
)
