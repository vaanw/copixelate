package com.copixelate.data.repo

import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.copixelate.data.firebase.FirebaseDatabaseAdapter
import com.copixelate.data.firebase.FirebaseResult.Failure
import com.copixelate.data.model.UserModel
import com.copixelate.data.model.toEntity
import com.copixelate.data.model.toModel
import com.copixelate.data.room.RoomAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.random.Random

object UserRepo {

    private val firebaseDatabase = FirebaseDatabaseAdapter
    private val room = RoomAdapter

    private val lifecycleScope = ProcessLifecycleOwner.get().lifecycleScope

    private val userModelFlow: Flow<UserModel> =
        room.userFlow().map { entity ->
            entity.toModel()
        }

    private val userModelStateFlow: StateFlow<UserModel> =
        userModelFlow
            .stateIn(
                scope = lifecycleScope,
                started = WhileSubscribed(),
                initialValue = UserModel(),
            )

    private suspend fun saveUser(userModel: UserModel) =
        room.saveUser(userModel.toEntity())

    suspend fun generateFriendCode() = AuthRepo.doIfSignedIn { uid ->
        generateFriendCode(uid)
    }

    private suspend fun generateFriendCode(uid: String) {

        val codeFormat = "%012d"
        var newCode: String

        do {
            val rand12 = Random.nextLong(from = 0, until = 9999_9999_9999)
            newCode = codeFormat.format(rand12.toString())
            val result = firebaseDatabase.getFriendCodeUid(newCode)

            if (result is Failure) {
                if (result.exception is NullPointerException) break
                else return
            }
        } while (true)

        firebaseDatabase.setFriendCode(uid, newCode).run {
            if (success) {
                // Save to local database
                val newUserModel = userModelStateFlow.value.copy(friendCode = newCode)
                saveUser(newUserModel)
            }
        }
    }

}
