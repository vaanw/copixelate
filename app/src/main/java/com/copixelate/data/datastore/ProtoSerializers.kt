package com.copixelate.data.datastore

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.copixelate.UiState
import com.copixelate.UserSettings
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

const val PROTO_ERROR_MESSAGE = "Cannot read proto."
const val USER_SETTINGS_FILE = "user_settings.pb"
const val UI_STATE_FILE = "ui_state.pb"

// User Settings

object UserSettingsSerializer : Serializer<UserSettings> {
    override val defaultValue: UserSettings = UserSettings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserSettings {
        try {
            return UserSettings.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException(PROTO_ERROR_MESSAGE, exception)
        }
    }

    override suspend fun writeTo(
        t: UserSettings,
        output: OutputStream
    ) = t.writeTo(output)
}

val Context.settingsDataStore: DataStore<UserSettings> by dataStore(
    fileName = USER_SETTINGS_FILE,
    serializer = UserSettingsSerializer
)

// Ui State

object UiStateSerializer : Serializer<UiState> {
    override val defaultValue: UiState = UiState.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UiState {
        try {
            return UiState.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException(PROTO_ERROR_MESSAGE, exception)
        }
    }

    override suspend fun writeTo(
        t: UiState,
        output: OutputStream
    ) = t.writeTo(output)
}

val Context.uiStateDataStore: DataStore<UiState> by dataStore(
    fileName = UI_STATE_FILE,
    serializer = UiStateSerializer
)
