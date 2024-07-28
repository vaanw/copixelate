package com.copixelate.data.model

import com.copixelate.data.room.UserEntity

data class UserModel (
    val id: IdModel = IdModel(),
    val contactCode: String? = null,
)

data class ContactModel (
    val id: IdModel = IdModel(),
    val displayName: String
)

//
// Database Conversion
//

// UserEntity
fun UserEntity.toModel(): UserModel = UserModel(
    id = IdModel(id, remoteKey),
    contactCode = contactCode
)

fun UserModel.toEntity(): UserEntity = UserEntity(
    id = id.localId,
    remoteKey = id.remoteKey,
    contactCode = contactCode
)
