package com.copixelate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.copixelate.data.bitmap.createScaledBitmap
import com.copixelate.data.model.IdModel
import com.copixelate.data.model.SpaceModel
import com.copixelate.data.repo.ArtRepo
import com.copixelate.data.repo.UiRepo
import com.copixelate.ui.util.generateDefaultArt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel : ViewModel() {

    private val artRepo = ArtRepo
    private val uiRepo = UiRepo

    val allSpaces: StateFlow<List<SpaceModel>> =
        artRepo.allSpacesFlow()
            .stateIn(
                scope = viewModelScope,
                initialValue = emptyList(),
                started = SharingStarted.Eagerly
            )

    fun createNewArtSpace(width: Int, height: Int, paletteSize: Int) =
        viewModelScope.launch {
            artRepo.saveSpace(
                spaceModel = SpaceModel()
                    .generateDefaultArt(width, height, paletteSize)
            )
        }

    fun deleteArtSpace(spaceModel: SpaceModel) =
        viewModelScope.launch {
            artRepo.loseSpace(spaceModel = spaceModel)
        }

    fun updateCurrentSpaceId(newId: IdModel, onComplete: () -> Unit) =
        viewModelScope.launch {
            uiRepo.saveCurrentSpaceId(spaceId = newId.localId)
            onComplete()
        }

    fun exportSpace(spaceModel: SpaceModel, fileName: String, scaleFactor: Int) {
        viewModelScope.launch(context = Dispatchers.IO) {
            artRepo.exportBitmap(
                bitmap = createScaledBitmap(
                    colorDrawingModel = spaceModel.colorDrawing,
                    scaleFactor = scaleFactor
                ),
                fileName = fileName
            )
        }
    }

}
