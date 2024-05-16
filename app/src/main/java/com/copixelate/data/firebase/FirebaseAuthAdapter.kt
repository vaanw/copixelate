package com.copixelate.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

sealed interface AuthResult<out T> {
    data class Success<T>(val value: T) : AuthResult<T>
    data class Failure(val exception: Exception) : AuthResult<Nothing>
    data object Pending : AuthResult<Nothing>
}

object FirebaseAuthAdapter {

    private val auth: FirebaseAuth = Firebase.auth

    init {
        auth.addAuthStateListener { authState ->
            handleAuthSuccess(authState.currentUser)
        }
    }

    private val _firebaseUserSharedFlow: MutableSharedFlow<AuthResult<FirebaseUser?>> =
        MutableSharedFlow(extraBufferCapacity = 1)

    val firebaseUserSharedFlow = _firebaseUserSharedFlow.asSharedFlow()

    private fun handleAuthSuccess(firebaseUser: FirebaseUser?) =
        _firebaseUserSharedFlow.tryEmit(AuthResult.Success(firebaseUser))

    private fun handleAuthFailure(e: Exception) =
        _firebaseUserSharedFlow.tryEmit(AuthResult.Failure(e))

    private fun setAuthPending() =
        _firebaseUserSharedFlow.tryEmit(AuthResult.Pending)

    suspend fun signIn(email: String, password: String): Unit =
        suspendCancellableCoroutine { continuation ->
            setAuthPending()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    task.exception?.run {
                        handleAuthFailure(this)
                    }
                    continuation.resume(Unit)
                }
        }

    fun signOut() = auth.signOut()

    suspend fun createAccount(email: String, password: String): Unit =
        suspendCancellableCoroutine { continuation ->
            setAuthPending()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    task.exception?.run {
                        handleAuthFailure(this)
                    }
                    continuation.resume(Unit)
                }
        }

    suspend fun updateDisplayName(displayName: String): Unit =
        suspendCancellableCoroutine { continuation ->

            auth.currentUser?.let { firebaseUser ->

                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()

                firebaseUser.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) handleAuthSuccess(firebaseUser)
                        continuation.resume(Unit)
                    }
            }
        }

}
