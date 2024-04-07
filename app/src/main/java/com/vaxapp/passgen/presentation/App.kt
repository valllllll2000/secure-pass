package com.vaxapp.passgen.presentation

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.vaxapp.passgen.R
import com.vaxapp.passgen.presentation.model.Password

@Composable
internal fun App(viewModel: PassViewModel) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val uiState by produceState<PassGenState>(
        initialValue = PassGenState.Loading,
        key1 = lifecycle,
        key2 = viewModel
    ) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            viewModel.passGenState.collect {
                value = it
            }
        }
    }
    val showToast: Boolean = viewModel.showToast.collectAsState().value
    val context = LocalContext.current
    when (uiState) {
        is PassGenState.Error -> Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
        PassGenState.Loading -> Progress()
        is PassGenState.Success -> {
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
                PassList(passwords = (uiState as PassGenState.Success).passwords, viewModel)
            }
        }
    }
    if (showToast) {
        viewModel.onToastShown()
        Toast.makeText(context, stringResource(R.string.toast_copied), Toast.LENGTH_SHORT).show()
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

@Composable
private fun PassList(passwords: List<Password>, viewModel: PassViewModel) {
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
                stringResource(R.string.image_description_hide_password)
            } else {
                stringResource(R.string.image_description_show_password)
            }

            IconButton(onClick = {
                passwordVisible = !passwordVisible
                viewModel.updatePasswordVisibility(password)
            }) {
                Icon(imageVector = image, description)
            }
        }
    )
}
