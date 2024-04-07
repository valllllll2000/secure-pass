package com.vaxapp.passgen.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PasswordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val password: String,
    val isVisible: Boolean
)