package com.copixelate.data.firebase

sealed interface FirebaseResult<out T> {
    data class Success<T>(val value: T) : FirebaseResult<T>
    data class Failure(val exception: Exception) : FirebaseResult<Nothing>
    val success get() = this is Success
}

sealed interface FirebaseStatus<out T> {
    data class Success<T>(val value: T) : FirebaseStatus<T>
    data class Failure(val exception: Exception) : FirebaseStatus<Nothing>
    data object Pending : FirebaseStatus<Nothing>
}
