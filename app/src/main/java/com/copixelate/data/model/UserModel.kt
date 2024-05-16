package com.copixelate.data.model

sealed class AuthStatus {
    data object SignedIn : AuthStatus()
    data object SignedOut : AuthStatus()
    data object Pending : AuthStatus()
    data class Failed(val errorMessage: String) : AuthStatus()
}

const val DEFAULT_DISPLAY_NAME = "Noname"

data class UserModel(
    val authStatus: AuthStatus = AuthStatus.SignedOut,
    val displayName: String = DEFAULT_DISPLAY_NAME
)
