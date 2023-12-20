package com.vaxapp.passgen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
internal fun App(viewModel: PassViewModel) {
    val state = viewModel.passGenState.collectAsState().value
    val context = LocalContext.current
    if (state.loading) {
        Progress()
    } else {
        Column {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { viewModel.addPassword() }, modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text(text = stringResource(id = R.string.new_password))
                }
            }
            PassList(state = state, viewModel)
        }

        if (state.showToast) {
            viewModel.onToastShown()
            Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun Progress() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        CircularProgressIndicator()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PassList(state: PassGenState, viewModel: PassViewModel) {
    val passwords: List<Password> = state.passwords
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(passwords) { password: Password ->
            Card(
                onClick = {
                    viewModel.onCardTapped(password, context)
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(0.9f),

                ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row {
                        PasswordField(password = password, viewModel)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasswordField(password: Password, viewModel: PassViewModel) {
    var passwordVisible by remember { mutableStateOf(password.visible) }
    val passwordText = password.password
    TextField(
        value = passwordText,
        onValueChange = {},
        readOnly = true,
        enabled = true,
        singleLine = true,
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            val image = if (!passwordVisible) {
                Icons.Filled.Visibility
            } else {
                Icons.Filled.VisibilityOff
            }
            val description = if (passwordVisible) {
                "Hide password"
            } else {
                "Show password"
            }

            IconButton(onClick = {
                passwordVisible = !passwordVisible
                viewModel.updatePasswordVisibility(password) }) {
                Icon(imageVector = image, description)
            }
        }
    )
}
