package com.vaxapp.passgen.repository

import android.content.Context
import androidx.room.Room
import com.vaxapp.passgen.Password
import com.vaxapp.passgen.db.AppDatabase
import com.vaxapp.passgen.db.PasswordDao
import com.vaxapp.passgen.db.PasswordEntity

internal class DatabaseRepository(appContext: Context) {

    private val passwordDao: PasswordDao

    init {
        val db = Room.databaseBuilder(
            appContext,
            AppDatabase::class.java, "database-name"
        ).build()
        passwordDao = db.passwordDao()
    }

    suspend fun getAllPasswords(): List<Password> {
        return passwordDao.getAll().map { Password(it.id, it.password) }
    }

    suspend fun addPassword(password: Password) {
        passwordDao.insert(PasswordEntity(password.id, password.password))
    }
}