package com.copixelate.data.repo

import androidx.datastore.core.DataStore
import com.copixelate.ThemeSetting
import com.copixelate.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object SettingsRepo {

    private lateinit var dataStore: DataStore<UserSettings>

    fun init(dataStore: DataStore<UserSettings>) {
        this.dataStore = dataStore
    }

    fun themeSettingFlow(): Flow<ThemeSetting> = dataStore.data
        .map { settings ->
            ThemeSetting.forNumber(settings.themeSettingValue)
        }

    suspend fun saveThemeSetting(themeSetting: ThemeSetting) =
        dataStore.updateData { settings ->
            settings.toBuilder().setThemeSetting(themeSetting).build()
        }

}
