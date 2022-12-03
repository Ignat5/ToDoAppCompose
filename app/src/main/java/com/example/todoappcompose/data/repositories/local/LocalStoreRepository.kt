package com.example.todoappcompose.data.repositories.local

import kotlinx.coroutines.flow.Flow

interface LocalStoreRepository {

    fun getFilterOptionsFlow(): Flow<FilterOptions>

    suspend fun saveFilterOption(newFilterOptions: FilterOptions)

}