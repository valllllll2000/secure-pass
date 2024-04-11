package com.vaxapp.passgen.domain.usecases

import com.vaxapp.passgen.data.repository.DatabaseRepository
import javax.inject.Inject

internal class DeletePasswords @Inject constructor(private val repository: DatabaseRepository) {

    suspend operator fun invoke() = repository.deletePasswords()
}
