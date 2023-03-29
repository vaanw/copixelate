package com.copixelate.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.copixelate.SettingsRepo
import com.copixelate.data.proto.settingsDataStore

class SettingsViewModel(private val repo: SettingsRepo) : ViewModel() {

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
