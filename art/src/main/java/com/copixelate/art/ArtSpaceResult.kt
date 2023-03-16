package com.copixelate.art

sealed class ArtSpaceResult<out T> {
    data class Success<R>(val value: R) : ArtSpaceResult<R>()
    data class Failure(val cause: Throwable = Throwable()) : ArtSpaceResult<Nothing>()

    val isSuccess
        get() = when (this) {
            is Success -> true
            else -> false
        }

    val isFailure
        get() = !isSuccess
}
