package com.copixelate.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.copixelate.ui.icon.IconCatalog
import com.copixelate.ui.util.InputValidity

@Composable
fun ValidatedTextInputField(
    value: String,
    onValueChange: (value: String) -> Unit,
    validity: InputValidity,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next
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
            textStyle = textStyle,
            keyboardType = keyboardType
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
    validity: InputValidity,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
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
                Visibility.VISIBLE -> IconCatalog.invisible
                Visibility.HIDDEN -> IconCatalog.visible
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
fun ImeActionTextInputField(
    value: String,
    onValueChange: (value: String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
) {

    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        isError = isError,
        keyboardOptions = KeyboardOptions(imeAction = imeAction, keyboardType = keyboardType),
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
        textStyle = textStyle
    )

}// End KeyboardActionTextInputField
