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

object FirebaseAuthAdapter {

    private val auth: FirebaseAuth = Firebase.auth

    init {
        auth.addAuthStateListener { authState ->
            reportAuthSuccess(authState.currentUser)
        }
    }

    private val _firebaseUserSharedFlow: MutableSharedFlow<FirebaseStatus<FirebaseUser?>> =
        MutableSharedFlow(extraBufferCapacity = 1)

    val firebaseUserSharedFlow = _firebaseUserSharedFlow.asSharedFlow()

    private fun reportAuthSuccess(firebaseUser: FirebaseUser?) =
        _firebaseUserSharedFlow.tryEmit(FirebaseStatus.Success(firebaseUser))

    private fun reportAuthFailure(e: Exception) =
        _firebaseUserSharedFlow.tryEmit(FirebaseStatus.Failure(e))

    private fun reportAuthPending() =
        _firebaseUserSharedFlow.tryEmit(FirebaseStatus.Pending)

    suspend fun signIn(email: String, password: String): Unit =
        suspendCancellableCoroutine { continuation ->
            reportAuthPending()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    task.exception?.run {
                        reportAuthFailure(this)
                    }
                    continuation.resume(Unit)
                }
        }

    fun signOut() = auth.signOut()

    suspend fun createAccount(email: String, password: String): Unit =
        suspendCancellableCoroutine { continuation ->
            reportAuthPending()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    task.exception?.run {
                        reportAuthFailure(this)
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
                        if (task.isSuccessful) reportAuthSuccess(firebaseUser)
                        continuation.resume(Unit)
                    }
            }
        }

}
