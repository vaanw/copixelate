package com.copixelate.data.repo

import androidx.datastore.core.DataStore
import com.copixelate.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UiRepo(private val dataStore: DataStore<UiState>) {

    val currentSpaceIdFlow: Flow<Long> = dataStore.data
        .map { uiState -> uiState.currentSpaceId }

    suspend fun saveCurrentSpaceId(spaceId: Long) =
        dataStore.updateData { uiState ->
            uiState.toBuilder().setCurrentSpaceId(spaceId).build()
        }

}
