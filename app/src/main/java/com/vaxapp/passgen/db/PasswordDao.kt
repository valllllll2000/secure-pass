package com.vaxapp.passgen.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PasswordDao {

    @Query("SELECT * FROM passwordentity ORDER BY id DESC")
    suspend fun getAll(): List<PasswordEntity>

    @Insert
    suspend fun insert(password: PasswordEntity)

    @Delete
    suspend fun delete(password: PasswordEntity)

}