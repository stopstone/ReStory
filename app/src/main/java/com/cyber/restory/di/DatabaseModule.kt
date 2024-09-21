package com.cyber.restory.di

import android.content.Context
import androidx.room.Room
import com.cyber.restory.data.db.AppDatabase
import com.cyber.restory.data.db.RecentSearchDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    fun provideRecentSearchDao(database: AppDatabase): RecentSearchDao {
        return database.recentSearchDao()
    }
}