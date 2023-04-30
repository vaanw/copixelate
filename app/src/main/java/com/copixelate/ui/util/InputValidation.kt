package com.copixelate.ui.util

import android.util.Patterns
import com.copixelate.R

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

    sealed class Invalid(val stringId: Int) : InputValidity() {

        sealed class Email(stringId: Int) : Invalid(stringId) {
            object Invalid : Email(R.string.invalid_email)
        }

        sealed class DisplayName(stringId: Int) : Invalid(stringId) {
            object TooShort : DisplayName(R.string.invalid_display_name_too_short)
            object TooLong : DisplayName(R.string.invalid_display_name_too_long)
        }

        sealed class Password(stringId: Int) : Invalid(stringId) {
            object TooShort : Password(R.string.invalid_password_too_short)
            object TooLong : Password(R.string.invalid_password_too_long)
            object NoMatch : Password(R.string.invalid_password_no_match)
        }

    }

}
