package com.copixelate.data.firebase

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName

@IgnoreExtraProperties
data class UserJson(
    @PropertyName("contact_code")
    val contactCode: Int? = null,
)

@IgnoreExtraProperties
data class InvitationJson(
//    @PropertyName("inviter_uid")
    @get:PropertyName("inviter_uid")
    val inviterUid: String? = null,
    @PropertyName("type")
    val type: String? = null
)

@IgnoreExtraProperties
data class SizeJson(
    @PropertyName("x")
    val x: Int? = null,
    @PropertyName("y")
    val y: Int? = null
)

@IgnoreExtraProperties
data class SpaceJson(
    @PropertyName("drawing_key")
    val drawingKey: String? = null,
    @PropertyName("palette_key")
    val paletteKey: String? = null,
    @PropertyName("creator_key")
    val creatorKey: String? = null,
    @PropertyName("member_keys")
    val memberKeys: List<String>? = null,
)
