package com.vaxapp.passgen.presentation.model

internal data class Password(
    val id: Long,
    val password: String,
    val visible: Boolean = false,
    val label: String = id.toString()
)