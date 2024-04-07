package com.vaxapp.passgen

internal data class Password(
    val id: Long,
    val password: String,
    var visible: Boolean = false
)