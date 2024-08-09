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

    // Friend Queries

    @Query("SELECT * FROM friend WHERE user_id = :userId")
    fun userFriendFlow(userId: Long): Flow<List<FriendEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriend(friend: FriendEntity): Long

    @Delete
    suspend fun deleteContact(friend: FriendEntity)

}
