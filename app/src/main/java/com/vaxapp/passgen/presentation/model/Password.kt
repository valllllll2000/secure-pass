package com.vaxapp.passgen.presentation.model

internal data class Password(
    val id: Long,
    val password: String,
    var visible: Boolean = false
)