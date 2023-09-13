package com.copixelate.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.copixelate.data.model.SpaceModel
import com.copixelate.data.repo.ArtRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ActivityViewModel : ViewModel() {

    private val artRepo = ArtRepo

    private val _events = MutableSharedFlow<UiEvent>(replay = 0)
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    fun shareSpace(spaceModel: SpaceModel, scaleFactor: Int) {
        viewModelScope.launch(context = Dispatchers.IO) {
            _events.emit(
                value = UiEvent.ShareImage(
                    uri = artRepo.shareSpace(spaceModel, scaleFactor)
                )
            )
        }
    }

    sealed class UiEvent {
        data class ShareImage(val uri: Uri) : UiEvent()
    }

}
