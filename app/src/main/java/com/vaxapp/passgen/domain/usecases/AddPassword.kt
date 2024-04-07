package com.vaxapp.passgen.domain.usecases

import com.vaxapp.passgen.presentation.model.Password
import com.vaxapp.passgen.data.repository.DatabaseRepository
import javax.inject.Inject

internal class AddPassword @Inject constructor(private val repository: DatabaseRepository) {

    suspend operator fun invoke(password: Password) = repository.addPassword(password)
}
