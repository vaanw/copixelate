package com.copixelate.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.copixelate.art.ArtSpace
import com.copixelate.art.ArtSpaceResult
import com.copixelate.art.PointF
import com.copixelate.data.proto.uiStateDataStore
import com.copixelate.data.repo.ArtRepo
import com.copixelate.data.repo.UiRepo
import com.copixelate.data.room.RoomAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ArtViewModel(
    private val artRepo: ArtRepo,
    private val uiRepo: UiRepo
) : ViewModel() {

    private val artSpace = ArtSpace()

    init {
        viewModelScope.launch {
            artRepo.stub()
        }
    }

    private val _drawing = MutableStateFlow(artSpace.state.drawing)
    private val _palette = MutableStateFlow(artSpace.state.palette)
    private val _activeColor = MutableStateFlow(artSpace.state.activeColor)
    private val _brushPreview = MutableStateFlow(artSpace.state.brushPreview)

    val drawing = _drawing.asStateFlow()
    val palette = _palette.asStateFlow()
    val activeColor = _activeColor.asStateFlow()
    val brushPreview = _brushPreview.asStateFlow()

    val brushSize = artSpace.state.brushSize

    fun updateDrawing(unitPosition: PointF) =
        viewModelScope.launch {

            artSpace.updateDrawing(unitPosition).let { result ->
                when (result) {
                    is ArtSpaceResult.Success -> _drawing.value = artSpace.state.drawing
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
            _brushPreview.value = artSpace.state.brushPreview

        }

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
