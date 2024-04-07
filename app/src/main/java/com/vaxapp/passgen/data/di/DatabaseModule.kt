package com.vaxapp.passgen.data.di

import android.content.Context
import androidx.room.Room
import com.vaxapp.passgen.data.db.AppDatabase
import com.vaxapp.passgen.data.db.PasswordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    fun providePasswordDao(appDatabase: AppDatabase): PasswordDao {
       return appDatabase.passwordDao()
    }

    @Provides
    @Singleton
    fun providePasswordDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java, "passwords"
        ).build()
    }
}
