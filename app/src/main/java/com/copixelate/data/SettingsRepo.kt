package com.copixelate.data

import androidx.datastore.core.DataStore
import com.copixelate.ThemeSetting
import com.copixelate.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepo(private val dataStore: DataStore<UserSettings>) {

    suspend fun setThemeSetting(themeSetting: ThemeSetting) {
        dataStore.updateData { settings ->
            settings.toBuilder()
                .setThemeSetting(themeSetting)
                .build()
        }
    }

    val themeSettingFlow: Flow<ThemeSetting> = dataStore.data
        .map { settings ->
            ThemeSetting.forNumber(settings.themeSettingValue)
        }

}
