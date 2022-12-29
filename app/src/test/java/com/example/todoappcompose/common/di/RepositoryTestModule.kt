package com.example.todoappcompose.common.di

import android.util.Log
import com.example.todoappcompose.common.data.FakeLocalStoreRepository
import com.example.todoappcompose.common.repository.FakeTasksRepository
import com.example.todoappcompose.data.repositories.local.LocalStoreRepository
import com.example.todoappcompose.data.repositories.tasks.TasksRepository
import com.example.todoappcompose.di.modules.RepositoryModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
object RepositoryTestModule {

    @Singleton
    @Provides
    fun provideTestTasksRepository(): TasksRepository {
        Log.d("myTag", "DataSourceModule: provideTestTasksRepository...")
        return FakeTasksRepository()
    }

    @Singleton
    @Provides
    fun provideTestLocalStoreRepository(): LocalStoreRepository {
        return FakeLocalStoreRepository()
    }

}