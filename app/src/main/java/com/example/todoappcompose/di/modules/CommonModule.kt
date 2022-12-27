package com.example.todoappcompose.di.modules

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.todoappcompose.data.db.dao.TasksDao
import com.example.todoappcompose.data.db.database.AppDatabase
import com.example.todoappcompose.data.repositories.local.LocalStoreRepository
import com.example.todoappcompose.data.repositories.local.LocalStoreRepositoryImpl
import com.example.todoappcompose.data.repositories.tasks.TasksRepository
import com.example.todoappcompose.data.repositories.tasks.TasksRepositoryImpl
import com.example.todoappcompose.util.AppConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideLocalStoreRepository(@ApplicationContext applicationContent: Context): LocalStoreRepository {
        return LocalStoreRepositoryImpl(applicationContent)
    }

    @Singleton
    @Provides
    fun provideTasksRepository(tasksDao: TasksDao): TasksRepository {
        Log.d("myTag", "DataSourceModule: provideTasksRepository...")
        return TasksRepositoryImpl(tasksDao)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Singleton
    @Provides
    fun provideTasksDao(@ApplicationContext applicationContent: Context): TasksDao {
        Log.d("myTag", "DataSourceModule: provideTaskDao...")
        val db = Room.databaseBuilder(
            applicationContent,
            AppDatabase::class.java,
            AppConstants.DATABASE_NAME
        ).build()
        return db.tasksDao()
    }
}

//@Module
//@InstallIn(SingletonComponent::class)
//object CommonModule {
//
//    @Singleton
//    @Provides
//    fun provideLocalStoreRepository(@ApplicationContext applicationContent: Context): LocalStoreRepository {
//        return LocalStoreRepositoryImpl(applicationContent)
//    }
//
//    @Singleton
//    @Provides
//    fun provideTasksRepository(tasksDao: TasksDao): TasksRepository {
//        return TasksRepositoryImpl(tasksDao)
//    }
//
//    @Singleton
//    @Provides
//    fun provideTasksDao(@ApplicationContext applicationContent: Context): TasksDao {
//        val db = Room.databaseBuilder(
//            applicationContent,
//            AppDatabase::class.java,
//            AppConstants.DATABASE_NAME
//        ).build()
//        return db.tasksDao()
//    }
//
//}