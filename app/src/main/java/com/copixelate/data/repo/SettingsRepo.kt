package com.copixelate.data.repo

import com.copixelate.ThemeSetting
import com.copixelate.data.datastore.DataStoreAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object SettingsRepo {

    private val dataStore = DataStoreAdapter.userSettings

    fun themeSettingFlow(): Flow<ThemeSetting> = dataStore.data
        .map { settings ->
            ThemeSetting.forNumber(settings.themeSettingValue)
        }

    suspend fun saveThemeSetting(themeSetting: ThemeSetting) =
        dataStore.updateData { settings ->
            settings.toBuilder().setThemeSetting(themeSetting).build()
        }

}
