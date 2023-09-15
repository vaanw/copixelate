package com.copixelate.data.repo

import com.copixelate.data.datastore.DataStoreAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object UiRepo {

    private val dataStore = DataStoreAdapter.uiState

    fun currentSpaceIdFlow(): Flow<Long> = dataStore.data
        .map { uiState -> uiState.currentSpaceId }

    suspend fun saveCurrentSpaceId(spaceId: Long) =
        dataStore.updateData { uiState ->
            uiState.toBuilder().setCurrentSpaceId(spaceId).build()
        }

}
