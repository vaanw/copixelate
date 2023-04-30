package com.copixelate.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.copixelate.ui.util.InputValidity

@Composable
fun ValidatedTextInputField(
    value: String,
    onValueChange: (value: String) -> Unit,
    label: @Composable (() -> Unit),
    validity: InputValidity,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
) {

    var lastFocusedState by rememberSaveable { mutableStateOf(false) }

    var isError by rememberSaveable { mutableStateOf(false) }
    isError = isError && validity.isNotValid && value.isNotEmpty()

    Column {
        ImeActionTextInputField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            isError = isError,
            imeAction = imeAction,
            modifier = modifier.onFocusChanged { focusState ->
                isError = isError || (!focusState.isFocused && lastFocusedState)
                lastFocusedState = focusState.isFocused
            },
            visualTransformation = visualTransformation,
            trailingIcon = trailingIcon,

            )

        if (isError && validity is InputValidity.Invalid) {
            Text(
                text = stringResource(validity.stringId),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 8.dp, bottom = 0.dp)
            )
        }

    }// End Column

}// End ValidatedTextInputField

private enum class Visibility {
    VISIBLE, HIDDEN;

    fun toggle() = when (this) {
        VISIBLE -> HIDDEN
        HIDDEN -> VISIBLE
    }
}

@Composable
fun SecretTextInputField(
    value: String,
    onValueChange: (value: String) -> Unit,
    label: @Composable (() -> Unit),
    validity: InputValidity,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
) {

    var visibility: Visibility by remember { mutableStateOf(Visibility.HIDDEN) }

    ValidatedTextInputField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        validity = validity,
        imeAction = imeAction,
        visualTransformation = when (visibility) {
            Visibility.VISIBLE -> VisualTransformation.None
            Visibility.HIDDEN -> PasswordVisualTransformation()
        },
        trailingIcon = {
            val image = when (visibility) {
                Visibility.VISIBLE -> Icons.Filled.VisibilityOff
                Visibility.HIDDEN -> Icons.Filled.Visibility
            }
            val contentDescription = when (visibility) {
                Visibility.VISIBLE -> "Hide password"
                Visibility.HIDDEN -> "Show password"
            }
            IconButton(onClick = { visibility = visibility.toggle() }) {
                Icon(image, contentDescription)
            }
        },
        modifier = modifier
    )

}// End SecretTextInputField

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ImeActionTextInputField(
    value: String,
    onValueChange: (value: String) -> Unit,
    label: @Composable (() -> Unit),
    isError: Boolean,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
) {

    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        isError = isError,
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = when (imeAction) {
            ImeAction.Next -> {
                KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) })
            }
            ImeAction.Done -> {
                KeyboardActions(onDone = { focusManager.clearFocus() })
            }
            else -> KeyboardActions.Default
        },
        modifier = modifier,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
    )

}// End KeyboardActionTextInputField
