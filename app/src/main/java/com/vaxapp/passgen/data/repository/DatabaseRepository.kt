package com.vaxapp.passgen.data.repository

import com.vaxapp.passgen.data.db.PasswordDao
import com.vaxapp.passgen.data.db.PasswordEntity
import com.vaxapp.passgen.presentation.model.Password
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class DatabaseRepository @Inject constructor(private val passwordDao: PasswordDao) {

    fun getAllPasswords(): Flow<List<Password>> {
        return passwordDao.getAll().map { items -> items.map { toPassword(it) } }
    }

    suspend fun addPassword(password: Password) {
        passwordDao.insert(toPasswordEntity(password))
    }

   suspend fun updatePassword(password: Password) {
        passwordDao.update(toPasswordEntity(password))
    }

    suspend fun deletePasswords() = passwordDao.deleteAll()

    private fun toPasswordEntity(password: Password) =
        PasswordEntity(password.id, password.password, password.visible, password.label)

    private fun toPassword(it: PasswordEntity) =
        Password(it.id, it.password, it.isVisible, it.label)

}
