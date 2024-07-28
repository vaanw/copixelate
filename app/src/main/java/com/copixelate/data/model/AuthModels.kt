package com.copixelate.data.model

sealed class AuthStatus {
    data class SignedIn(val uid: String) : AuthStatus()
    data object SignedOut : AuthStatus()
    data object Pending : AuthStatus()
    data class Failed(val errorMessage: String) : AuthStatus()
}

const val DEFAULT_DISPLAY_NAME = "Noname"

data class AuthModel(
    val authStatus: AuthStatus = AuthStatus.SignedOut,
    val displayName: String = DEFAULT_DISPLAY_NAME,
)
