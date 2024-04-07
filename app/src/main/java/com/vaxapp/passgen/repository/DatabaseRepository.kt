package com.vaxapp.passgen.repository

import android.content.Context
import androidx.room.Room
import com.vaxapp.passgen.presentation.model.Password
import com.vaxapp.passgen.db.AppDatabase
import com.vaxapp.passgen.db.PasswordDao
import com.vaxapp.passgen.db.PasswordEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DatabaseRepository(appContext: Context) {

    private val passwordDao: PasswordDao

    init {
        val db = Room.databaseBuilder(
            appContext,
            AppDatabase::class.java, "database-name"
        ).build()
        passwordDao = db.passwordDao()
    }

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