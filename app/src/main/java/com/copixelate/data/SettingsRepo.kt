package com.copixelate.data

import androidx.datastore.core.DataStore
import com.copixelate.ThemeType
import com.copixelate.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepo(private val dataStore: DataStore<UserSettings>) {

    suspend fun setTheme(themeType: ThemeType) {
        dataStore.updateData { settings ->
            settings.toBuilder()
                .setThemeType(themeType)
                .build()
        }
    }

    val settingsFlow: Flow<ThemeType> = dataStore.data
        .map { settings ->
            ThemeType.forNumber(settings.themeTypeValue)
        }

}
