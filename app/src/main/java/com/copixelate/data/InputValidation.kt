package com.copixelate.data

import android.util.Patterns

object InputValidation {

    fun checkEmail(value: String): InputValidity =
        when (Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            true -> InputValidity.Valid
            false -> InputValidity.Invalid.Email.Invalid
        }

    fun checkDisplayName(value: String): InputValidity =
        when (value.length > 1) {
            true -> InputValidity.Valid
            false -> InputValidity.Invalid.DisplayName.TooShort
        }

    fun checkPassword(value: String) =
        // Firebase requires a min of 6
        if (value.length < 6) InputValidity.Invalid.Password.TooShort
        // Short max for testing, change to 32 or 64
        else if (value.length > 12) InputValidity.Invalid.Password.TooLong
        else InputValidity.Valid

    fun checkPasswordMatch(p1: String, p2: String) =
        when (p1 == p2) {
            true -> InputValidity.Valid
            false -> InputValidity.Invalid.Password.NoMatch
        }

}

sealed class InputValidity {

    object Valid : InputValidity()

    val isValid
        get() = when (this) {
            is Valid -> true
            else -> false
        }

    val isNotValid
        get() = !isValid

    sealed class Invalid: InputValidity() {

        sealed class Email : Invalid() {
            object Invalid : Email()
        }

        sealed class DisplayName : Invalid() {
            object TooShort : DisplayName()
            object TooLong : DisplayName()
        }

        sealed class Password : Invalid() {
            object TooShort : Password()
            object TooLong : Password()
            object NoMatch : Password()
        }

    }

}
