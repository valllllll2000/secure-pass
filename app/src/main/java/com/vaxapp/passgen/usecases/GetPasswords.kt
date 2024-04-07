package com.vaxapp.passgen.usecases

import com.vaxapp.passgen.repository.DatabaseRepository

internal class GetPasswords(private val repository: DatabaseRepository) {
    operator fun invoke() = repository.getAllPasswords()
}
