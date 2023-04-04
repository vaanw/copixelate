package com.copixelate.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.copixelate.ThemeSetting
import com.copixelate.data.repo.SettingsRepo
import com.copixelate.data.proto.settingsDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SettingsViewModel(private val repo: SettingsRepo) : ViewModel() {

    val themeSetting: StateFlow<ThemeSetting> =
        repo.themeSettingFlow.stateIn(
            scope = viewModelScope,
            initialValue = runBlocking { repo.themeSettingFlow.first() },
            started = SharingStarted.WhileSubscribed()
        )

    fun saveThemeSetting(setting: ThemeSetting) = viewModelScope.launch {
        repo.saveThemeSetting(setting)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context = (this[APPLICATION_KEY] as Application).applicationContext
                SettingsViewModel(
                    repo = SettingsRepo(
                        dataStore = context.settingsDataStore
                    )
                )
            }
        }
    }

}
