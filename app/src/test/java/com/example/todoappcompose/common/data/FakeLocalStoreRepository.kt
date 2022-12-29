package com.example.todoappcompose.common.data

import com.example.todoappcompose.data.repositories.local.FilterOptions
import com.example.todoappcompose.data.repositories.local.LocalStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class FakeLocalStoreRepository(initFilterOptions: FilterOptions = FilterOptions.ALL): LocalStoreRepository {

    private val _filterState: MutableStateFlow<FilterOptions> = MutableStateFlow(initFilterOptions)
    private val filterState: StateFlow<FilterOptions> = _filterState

    override fun getFilterOptionsFlow(): Flow<FilterOptions> = filterState

    override suspend fun saveFilterOption(newFilterOptions: FilterOptions) {
        _filterState.update {
            newFilterOptions
        }
    }

}