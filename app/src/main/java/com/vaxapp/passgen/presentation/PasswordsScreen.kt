package com.vaxapp.passgen.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.vaxapp.passgen.R
import com.vaxapp.passgen.presentation.model.Password

@Composable
internal fun PasswordsScreen(viewModel: PassViewModel) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val uiState by produceState<PassGenState>(
        initialValue = PassGenState.Loading, key1 = lifecycle, key2 = viewModel
    ) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            viewModel.passGenState.collect {
                value = it
            }
        }
    }
    val showToast: Boolean = viewModel.showToast.collectAsState().value
    val context = LocalContext.current
    Scaffold(topBar = { TopBar(viewModel) }) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (uiState) {
                is PassGenState.Error -> Toast.makeText(
                    context, stringResource(R.string.loading_error), Toast.LENGTH_SHORT
                ).show()

                PassGenState.Loading -> Progress()
                is PassGenState.Success -> {
                    Box(Modifier.fillMaxSize()) {
                        PassList(passwords = (uiState as PassGenState.Success).passwords, viewModel)
                        FABView(
                            viewModel,
                            Modifier
                                .padding(16.dp)
                                .align(Alignment.BottomEnd)
                        )
                    }
                }
            }
        }
    }
    if (showToast) {
        viewModel.onToastShown()
        Toast.makeText(context, stringResource(R.string.toast_copied), Toast.LENGTH_SHORT).show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopBar(viewModel: PassViewModel) {
    TopAppBar(title = {
        Text(
            text = stringResource(R.string.password_screen_title),
        )
    },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = FloatingActionButtonDefaults.containerColor),
        actions = {
            IconButton(onClick = { viewModel.deleteAll() }) {
                Icon(
                    imageVector = Icons.Default.Delete, contentDescription = "Delete all passwords"
                )
            }
        })
}

@Composable
private fun FABView(viewModel: PassViewModel, modifier: Modifier) {
    FloatingActionButton(
        onClick = { viewModel.addPassword() }, modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(id = R.string.new_password)
        )
    }
}

@Composable
fun Progress() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
private fun PassList(passwords: List<Password>, viewModel: PassViewModel) {
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(passwords) { password: Password ->
            Card(
                onClick = {
                    viewModel.onCardTapped(password, context)
                },
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(0.9f),

                ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    PasswordField(password = password, viewModel)
                }
            }
        }
    }
}

@Composable
private fun PasswordField(password: Password, viewModel: PassViewModel) {
    val focusRequester = remember { FocusRequester() }
    var passwordLabel by rememberSaveable {
        mutableStateOf(password.label)
    }
    OutlinedTextField(
        value = TextFieldValue(passwordLabel, selection = TextRange(passwordLabel.length)),
        onValueChange = {
            passwordLabel = it.text
            viewModel.updatePasswordLabel(password, it.text)
        },
        readOnly = false,
        enabled = true,
        singleLine = true,
        trailingIcon = {
            val image = Icons.Filled.Edit
            val description = "Edit password name"
            IconButton(onClick = { focusRequester.requestFocus() }) {
                Icon(imageVector = image, description)
            }
        },
        label = {
            Text(text = "Password name")
        },
        modifier = Modifier.focusRequester(focusRequester)
    )
    val passwordText = password.password
    OutlinedTextField(value = passwordText,
        onValueChange = {},
        readOnly = true,
        enabled = true,
        singleLine = true,
        visualTransformation = if (password.visible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            val image = if (!password.visible) {
                Icons.Filled.Visibility
            } else {
                Icons.Filled.VisibilityOff
            }
            val description = if (password.visible) {
                stringResource(R.string.image_description_hide_password)
            } else {
                stringResource(R.string.image_description_show_password)
            }

            IconButton(onClick = {
                viewModel.updatePasswordVisibility(password)
            }) {
                Icon(imageVector = image, description)
            }
        },
        label = {
            Text(text = "Password")
        })
}
