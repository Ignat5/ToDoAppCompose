package com.example.todoappcompose.ui.screens.detail_task

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoappcompose.data.db.entities.TaskEntity
import com.example.todoappcompose.data.repositories.tasks.TasksRepository
import com.example.todoappcompose.ui.navigation.NavArg
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class DetailTaskViewModel @Inject constructor(
    private val repository: TasksRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val currentTaskId: String = savedStateHandle.get<String>(NavArg.ARG_TASK_ID) ?: ""
    val uiState: StateFlow<DetailTaskScreenState> = repository.getTaskFlowById(currentTaskId).map {
        DetailTaskScreenState(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = DetailTaskScreenState(null)
    )
}

data class DetailTaskScreenState(
    val currentTask: TaskEntity?
)