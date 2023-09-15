package com.copixelate.data.datastore

import android.app.Application
import androidx.datastore.core.DataStore
import com.copixelate.UiState
import com.copixelate.UserSettings

object DataStoreAdapter {

    lateinit var uiState: DataStore<UiState>
    lateinit var userSettings: DataStore<UserSettings>

    fun init(application: Application){
        application.run {
            uiState = uiStateDataStore
            userSettings = settingsDataStore
        }

    }

}
