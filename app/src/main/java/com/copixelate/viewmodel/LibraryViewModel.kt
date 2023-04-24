package com.copixelate.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.copixelate.art.ArtSpace
import com.copixelate.data.model.SpaceModel
import com.copixelate.data.repo.ArtRepo
import com.copixelate.data.room.RoomAdapter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(private val repo: ArtRepo) : ViewModel() {

    val allSpaces: StateFlow<List<SpaceModel>> =
        repo.allSpacesFlow()
            .stateIn(
                scope = viewModelScope,
                initialValue = emptyList(),
                started = SharingStarted.WhileSubscribed()
            )

    fun saveArtSpace(artSpace: ArtSpace = ArtSpace()) {
        viewModelScope.launch {
            repo.saveSpace(artSpace = artSpace)
        }
    }

    fun loseArtSpace(spaceModel: SpaceModel) {
        viewModelScope.launch {
            repo.loseSpace(spaceModel = spaceModel)
        }
    }

    // ViewModel Factory for using custom arguments
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application).applicationContext
                LibraryViewModel(
                    repo = ArtRepo(
                        roomAdapter = RoomAdapter(context)
                    )
                )
            }
        }
    }

}
