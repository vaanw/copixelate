package com.copixelate.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        UserEntity::class,
        ContactEntity::class,
        SpaceEntity::class,
        DrawingEntity::class,
        PaletteEntity::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun artDao(): ArtDao
    abstract fun userDao(): UserDao
}
