package com.copixelate.data.model

data class IdModel(
    val localId: Long = 0,
    val remoteKey: String? = null,
) : Comparable<IdModel> {
    override fun compareTo(other: IdModel): Int {
        return localId.compareTo(other.localId)
    }
}
