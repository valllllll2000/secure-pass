package com.vaxapp.passgen.data.repository

import com.vaxapp.passgen.data.db.PasswordDao
import com.vaxapp.passgen.data.db.PasswordEntity
import com.vaxapp.passgen.presentation.model.Password
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class DatabaseRepository @Inject constructor(private val passwordDao: PasswordDao) {

    fun getAllPasswords(): Flow<List<Password>> {
        return passwordDao.getAll().map { items -> items.map { Password(it.id, it.password, it.isVisible) } }
    }

    suspend fun addPassword(password: Password) {
        passwordDao.insert(PasswordEntity(password.id, password.password, password.visible))
    }

   suspend fun updatePassword(password: Password) {
        passwordDao.update(PasswordEntity(password.id, password.password, password.visible))
    }
}
