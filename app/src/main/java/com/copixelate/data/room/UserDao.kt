package com.copixelate.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // User Queries

    @Transaction
    @Query("SELECT * FROM user LIMIT 1")
    fun userFlow(): Flow<UserEntity>

    @Transaction
    suspend fun replaceUser(user: UserEntity) {
        deleteAllUsers()
        insertUser(user)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Query("DELETE FROM user")
    suspend fun deleteAllUsers()

    // Contact Queries

    @Query("SELECT * FROM contact WHERE user_id = :userId")
    fun userContactsFlow(userId: Long): Flow<List<ContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity): Long

    @Delete
    suspend fun deleteContact(contact: ContactEntity)

}
