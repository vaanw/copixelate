package com.copixelate

import android.app.Application
import com.copixelate.data.datastore.DataStoreAdapter
import com.copixelate.data.room.RoomAdapter
import com.copixelate.data.storage.StorageAdapter

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        RoomAdapter.init(this)
        StorageAdapter.init(this)
        DataStoreAdapter.init(this)
    }

}
