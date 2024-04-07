package com.vaxapp.passgen.presentation

import com.vaxapp.passgen.presentation.model.Password

internal sealed interface PassGenState {

    data object Loading : PassGenState
    data class Success(val passwords: List<Password>) : PassGenState
    data class Error(val throwable: Throwable) : PassGenState
}
