package com.vaxapp.passgen.usecases

import com.vaxapp.passgen.presentation.model.Password
import com.vaxapp.passgen.repository.DatabaseRepository

internal class UpdatePassword(private val repository: DatabaseRepository) {

    suspend operator fun invoke(password: Password) = repository.updatePassword(password)
}