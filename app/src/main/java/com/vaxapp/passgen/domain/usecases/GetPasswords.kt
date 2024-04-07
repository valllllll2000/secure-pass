package com.vaxapp.passgen.domain.usecases

import com.vaxapp.passgen.data.repository.DatabaseRepository
import javax.inject.Inject

internal class GetPasswords@Inject constructor(private val repository: DatabaseRepository) {
    operator fun invoke() = repository.getAllPasswords()
}
