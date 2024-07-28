package com.copixelate.data.repo

import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.copixelate.data.firebase.FirebaseAuthAdapter
import com.copixelate.data.firebase.FirebaseStatus
import com.copixelate.data.model.AuthModel
import com.copixelate.data.model.AuthStatus
import com.copixelate.data.model.DEFAULT_DISPLAY_NAME
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

object AuthRepo {

    private val auth = FirebaseAuthAdapter

    val authModelFlow: StateFlow<AuthModel> =
        auth.firebaseUserSharedFlow
            .map { authResult ->
                authResult.toModel()
            }
            .stateIn(
                scope = ProcessLifecycleOwner.get().lifecycleScope,
                started = WhileSubscribed(),
                initialValue = AuthModel(),
            )

    /**
     * Executes the provided [block] of code only if the user is currently signed in.
     *
     * This function checks the current authentication status and if the user is signed in,
     * it executes the [block] lambda, passing the user's UID as a parameter.
     *
     * @param block The lambda to execute if the user is signed in. It receives the user's UID as a parameter.
     */
    suspend fun doIfSignedIn(block: suspend (uid: String) -> Unit) {
        val authStatus = authModelFlow.value.authStatus
        if (authStatus is AuthStatus.SignedIn) {
            block(authStatus.uid)
        }
    }

    suspend fun signIn(email: String, password: String) =
        auth.signIn(email, password)

    fun signOut() = auth.signOut()

    suspend fun signUp(email: String, password: String, displayName: String) {
        auth.createAccount(email, password)
        auth.updateDisplayName(displayName)
    }

} // End UserRepo

private fun FirebaseStatus<FirebaseUser?>.toModel(): AuthModel =
    when (this) {
        is FirebaseStatus.Success -> toModel()
        is FirebaseStatus.Pending -> toModel()
        is FirebaseStatus.Failure -> toModel()
    }

private fun FirebaseStatus.Success<FirebaseUser?>.toModel(): AuthModel =
    value?.toModel() ?: AuthModel()

private fun FirebaseStatus.Pending.toModel(): AuthModel =
    AuthModel(authStatus = AuthStatus.Pending)

private fun FirebaseStatus.Failure.toModel(): AuthModel =
    AuthModel(
        authStatus =
        AuthStatus.Failed(
            errorMessage = exception.message ?: "No message provided by exception"
        )
    )

val FirebaseUser.isAuthenticated: Boolean
    get() = !isAnonymous

private fun FirebaseUser.toModel(): AuthModel =
    AuthModel(
        authStatus = when (isAuthenticated) {
            false -> AuthStatus.SignedOut
            true -> AuthStatus.SignedIn(uid)
        },
        displayName = displayName ?: DEFAULT_DISPLAY_NAME
    )
