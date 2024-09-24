package com.cyber.restory.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cyber.restory.data.model.entity.RecentSearch

@Database(entities = [RecentSearch::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recentSearchDao(): RecentSearchDao
}