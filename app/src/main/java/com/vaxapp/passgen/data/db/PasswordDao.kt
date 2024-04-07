package com.vaxapp.passgen.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PasswordDao {

    @Query("SELECT * FROM passwordentity ORDER BY id DESC")
    fun getAll(): Flow<List<PasswordEntity>>

    @Insert
    suspend fun insert(password: PasswordEntity)

    @Delete
    suspend fun delete(password: PasswordEntity)

    @Update
    suspend fun update(password: PasswordEntity)

}
