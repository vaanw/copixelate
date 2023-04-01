package com.copixelate.data.firebase

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName

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
