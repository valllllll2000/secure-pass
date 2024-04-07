package com.vaxapp.passgen.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PasswordEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun passwordDao(): PasswordDao
}
