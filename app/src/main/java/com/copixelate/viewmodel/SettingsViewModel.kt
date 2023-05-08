package com.copixelate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.copixelate.ThemeSetting
import com.copixelate.data.repo.SettingsRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SettingsViewModel : ViewModel() {

    private val settingsRepo = SettingsRepo

    val themeSetting: StateFlow<ThemeSetting> =
        settingsRepo.themeSettingFlow().stateIn(
            scope = viewModelScope,
            initialValue = runBlocking { settingsRepo.themeSettingFlow().first() },
            started = SharingStarted.WhileSubscribed()
        )

    fun saveThemeSetting(setting: ThemeSetting) = viewModelScope.launch {
        settingsRepo.saveThemeSetting(setting)
    }

}
