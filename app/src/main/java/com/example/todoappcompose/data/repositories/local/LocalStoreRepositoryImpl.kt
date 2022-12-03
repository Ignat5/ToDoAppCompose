package com.example.todoappcompose.data.repositories.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.todoappcompose.util.AppConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(AppConstants.DATA_STORE_NAME_V1)

class LocalStoreRepositoryImpl @Inject constructor(
    private val context: Context
) : LocalStoreRepository {

    private val filterKey = stringPreferencesKey(AppConstants.FILTER_OPTION_KEY)

    override fun getFilterOptionsFlow(): Flow<FilterOptions> =
        context.dataStore.data.map { preferences ->
            preferences[filterKey]?.let {
                FilterOptions.valueOf(it)
            } ?: kotlin.run {
                FilterOptions.ALL
            }
        }

    override suspend fun saveFilterOption(newFilterOptions: FilterOptions) {
        context.dataStore.edit { preferences ->
            preferences[filterKey] = newFilterOptions.name
        }
    }

}

enum class FilterOptions {
    ALL,
    ACTIVE,
    COMPLETED
}