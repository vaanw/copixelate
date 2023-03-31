package com.copixelate.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.copixelate.ThemeType
import com.copixelate.data.SettingsRepo
import com.copixelate.data.proto.settingsDataStore
import kotlinx.coroutines.launch

class SettingsViewModel(private val repo: SettingsRepo) : ViewModel() {

    val themeTypeFlow = repo.themeTypeFlow
    fun setThemeType(type: ThemeType) = viewModelScope.launch {
        repo.setThemeType(type)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context = (this[APPLICATION_KEY] as Application).applicationContext
                val dataStore = context.settingsDataStore
                SettingsViewModel(repo = SettingsRepo(dataStore))
            }
        }
    }

}
