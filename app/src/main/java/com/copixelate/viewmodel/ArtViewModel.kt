package com.copixelate.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.copixelate.art.*
import com.copixelate.data.proto.uiStateDataStore
import com.copixelate.data.repo.ArtRepo
import com.copixelate.data.repo.UiRepo
import com.copixelate.data.repo.toArtSpace
import com.copixelate.data.room.RoomAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ArtViewModel(
    private val artRepo: ArtRepo,
    private val uiRepo: UiRepo
) : ViewModel() {

    private var artSpace = ArtSpace()

    private val _drawing = MutableStateFlow(artSpace.state.colorDrawing)
    private val _palette = MutableStateFlow(artSpace.state.palette)
    private val _activeColor = MutableStateFlow(artSpace.state.activeColor)
    private val _brushPreview = MutableStateFlow(artSpace.state.brushPreview)
    private val _brushSize = MutableStateFlow(artSpace.state.brushSize)

    val drawing = _drawing.asStateFlow()
    val palette = _palette.asStateFlow()
    val activeColor = _activeColor.asStateFlow()
    val brushPreview = _brushPreview.asStateFlow()
    val brushSize = _brushSize.asStateFlow()


    init {
        viewModelScope.launch {
            @OptIn(ExperimentalCoroutinesApi::class)
            uiRepo.currentSpaceIdFlow
                .flatMapLatest { currentId ->
                    flowOf<ArtSpace>(getSpaceByIdOrCreateDefault(spaceId = currentId))
                }.collect { newArtSpace ->
                    refreshArtSpace(newArtSpace = newArtSpace)
                }
        }
    }

    private suspend fun getSpaceByIdOrCreateDefault(spaceId: Long): ArtSpace =
        artRepo.getSpaceByIdOrDefault(id = spaceId)?.toArtSpace()
            ?: artSpace.also { artSpace: ArtSpace ->
                val newId = artRepo.saveSpace(artSpace = artSpace)[0]
                uiRepo.saveCurrentSpaceId(spaceId = newId)
            }

    private fun refreshArtSpace(newArtSpace: ArtSpace) {
        artSpace = newArtSpace
        _drawing.value = artSpace.state.colorDrawing
        _palette.value = artSpace.state.palette
        _activeColor.value = artSpace.state.activeColor
        _brushPreview.value = artSpace.state.brushPreview
    }


    fun updateDrawing(unitPosition: PointF) =
        viewModelScope.launch {

            artSpace.updateDrawing(unitPosition).let { result ->
                when (result) {
                    is ArtSpaceResult.Success -> _drawing.value = artSpace.state.colorDrawing
                    is ArtSpaceResult.Failure -> Log.d(javaClass.simpleName, result.toString())
                }
            }

        }

    fun updatePalette(unitPosition: PointF) =
        viewModelScope.launch {

            artSpace.updatePalette(unitPosition).let { result ->
                when (result) {
                    is ArtSpaceResult.Success -> {
                        _palette.value = artSpace.state.palette
                        _activeColor.value = artSpace.state.activeColor
                        _brushPreview.value = artSpace.state.brushPreview
                    }
                    is ArtSpaceResult.Failure -> Log.d(javaClass.simpleName, result.toString())
                }
            }

        }

    fun updateBrush(size: Int) =
        viewModelScope.launch {
            artSpace.updateBrushSize(size)
            _brushSize.value = artSpace.state.brushSize
            _brushPreview.value = artSpace.state.brushPreview
        }

    // ViewModel Factory for using custom arguments
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context = (this[APPLICATION_KEY] as Application).applicationContext
                ArtViewModel(
                    artRepo = ArtRepo(roomAdapter = RoomAdapter(context)),
                    uiRepo = UiRepo(dataStore = context.uiStateDataStore)
                )
            }
        }
    }

}
