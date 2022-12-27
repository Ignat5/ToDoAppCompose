package com.example.todoappcompose.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.todoappcompose.data.db.dao.TasksDao
import com.example.todoappcompose.data.db.database.AppDatabase
import com.example.todoappcompose.di.modules.DataSourceModule
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataSourceModule::class]
)
object DatabaseTestModule {

    @Singleton
    @Provides
    fun provideTaskDao(@ApplicationContext applicationContent: Context): TasksDao {
        Log.d("myTag", "DatabaseTestModule: provideTaskDao...")
        val database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        return database.tasksDao()
    }
}