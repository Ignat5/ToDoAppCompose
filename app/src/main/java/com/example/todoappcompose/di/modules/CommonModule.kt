package com.example.todoappcompose.di.modules

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.todoappcompose.data.db.dao.TasksDao
import com.example.todoappcompose.data.db.database.AppDatabase
import com.example.todoappcompose.data.repositories.TasksRepository
import com.example.todoappcompose.data.repositories.TasksRepositoryImpl
import com.example.todoappcompose.util.AppConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonModule {

    @Singleton
    @Provides
    fun provideTasksRepository(tasksDao: TasksDao): TasksRepository {
        return TasksRepositoryImpl(tasksDao)
    }

    @Singleton
    @Provides
    fun provideTasksDao(@ApplicationContext applicationContent: Context): TasksDao {
        val db = Room.databaseBuilder(
            applicationContent,
            AppDatabase::class.java,
            AppConstants.DATABASE_NAME
        ).build()
        return db.tasksDao()
    }

}