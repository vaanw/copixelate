package com.copixelate.data.repo

import com.copixelate.data.firebase.AuthResult
import com.copixelate.data.firebase.FirebaseAuthAdapter
import com.copixelate.data.firebase.FirebaseAuthAdapter.firebaseUserSharedFlow
import com.copixelate.data.model.AuthStatus
import com.copixelate.data.model.DEFAULT_DISPLAY_NAME
import com.copixelate.data.model.UserModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object UserRepo {

    private val auth = FirebaseAuthAdapter

    fun userModelFlow(): Flow<UserModel> =
        firebaseUserSharedFlow.map { authResult ->
            authResult.toModel()
        }

    suspend fun signIn(email: String, password: String) =
        auth.signIn(email, password)

    fun signOut() = auth.signOut()

    suspend fun signUp(email: String, password: String, displayName: String) {
        auth.createAccount(email, password)
        auth.updateDisplayName(displayName)
    }

} // End UserRepo

private fun AuthResult<FirebaseUser?>.toModel(): UserModel =
    when (this) {
        is AuthResult.Success -> toModel()
        is AuthResult.Pending -> toModel()
        is AuthResult.Failure -> toModel()
    }

private fun AuthResult.Success<FirebaseUser?>.toModel(): UserModel =
    value?.toModel() ?: UserModel()

private fun AuthResult.Pending.toModel(): UserModel =
    UserModel(authStatus = AuthStatus.Pending)

private fun AuthResult.Failure.toModel(): UserModel =
    UserModel(
        authStatus =
        AuthStatus.Failed(
            errorMessage = exception.message ?: "No message provided by exception"
        )
    )

val FirebaseUser.isAuthenticated: Boolean
    get() = !isAnonymous

private fun FirebaseUser.toModel(): UserModel =
    UserModel(
        authStatus = when (isAuthenticated) {
            false -> AuthStatus.SignedOut
            true -> AuthStatus.SignedIn
        },
        displayName = displayName ?: DEFAULT_DISPLAY_NAME
    )
