package com.copixelate.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.copixelate.art.ArtSpace
import com.copixelate.data.model.IDModel
import com.copixelate.data.model.SpaceModel
import com.copixelate.data.proto.uiStateDataStore
import com.copixelate.data.repo.ArtRepo
import com.copixelate.data.repo.UiRepo
import com.copixelate.data.room.RoomAdapter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val artRepo: ArtRepo,
    private val uiRepo: UiRepo
) : ViewModel() {

    val allSpaces: StateFlow<List<SpaceModel>> =
        artRepo.allSpacesFlow()
            .stateIn(
                scope = viewModelScope,
                initialValue = emptyList(),
                started = SharingStarted.WhileSubscribed()
            )

    fun saveArtSpace(artSpace: ArtSpace = ArtSpace()) =
        viewModelScope.launch {
            artRepo.saveSpace(artSpace = artSpace)
        }

    fun loseArtSpace(spaceModel: SpaceModel) =
        viewModelScope.launch {
            artRepo.loseSpace(spaceModel = spaceModel)
        }

    fun updateCurrentSpaceId(newId: IDModel) =
        viewModelScope.launch {
            newId.localID?.let {
                uiRepo.saveCurrentSpaceId(spaceId = it)
            }
        }


    // ViewModel Factory for using custom arguments
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application).applicationContext
                LibraryViewModel(
                    artRepo = ArtRepo(roomAdapter = RoomAdapter(context)),
                    uiRepo = UiRepo(dataStore = context.uiStateDataStore)
                )
            }
        }
    }

}
