package com.copixelate.data.proto

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.copixelate.UiState
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object UiStateSerializer : Serializer<UiState> {
    override val defaultValue: UiState = UiState.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UiState {
        try {
            return UiState.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: UiState,
        output: OutputStream
    ) = t.writeTo(output)
}

val Context.uiStateDataStore: DataStore<UiState> by dataStore(
    fileName = "ui_state.pb",
    serializer = UiStateSerializer
)
