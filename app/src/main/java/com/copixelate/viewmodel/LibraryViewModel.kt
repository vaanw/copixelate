package com.copixelate.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.copixelate.data.model.IdModel
import com.copixelate.data.model.SpaceModel
import com.copixelate.data.repo.ArtRepo
import com.copixelate.data.repo.UiRepo
import com.copixelate.ui.util.generateDefaultArt
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

    fun updateCurrentSpaceId(newId: IdModel) =
        viewModelScope.launch {
            newId.localId?.let {
                uiRepo.saveCurrentSpaceId(spaceId = it)
            }
        }

    fun exportSpace(spaceModel: SpaceModel, fileName: String) {
        val bitmapConfig = Bitmap.Config.ARGB_8888
        viewModelScope.launch {
            spaceModel.colorDrawing.run {
                artRepo.exportBitmap(
                    bitmap = Bitmap.createBitmap(pixels.toIntArray(), size.x, size.y, bitmapConfig),
                    fileName = fileName
                )
            }
        }
    }

}
