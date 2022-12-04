package com.example.todoappcompose.ui.screens.detail_task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoappcompose.data.db.entities.TaskEntity
import com.example.todoappcompose.data.repositories.tasks.TasksRepository
import com.example.todoappcompose.ui.navigation.NavArg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailTaskViewModel @Inject constructor(
    private val repository: TasksRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val currentTaskId: String = savedStateHandle.get<String>(NavArg.ARG_TASK_ID) ?: ""
    private val _currentTask: Flow<TaskEntity?> = repository.getTaskFlowById(currentTaskId)
    private val _showDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _shouldNavigateBack: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val uiState: StateFlow<DetailTaskScreenState> = combine(
        _currentTask, _showDialog, _shouldNavigateBack
    ) { currentTask, showDeleteDialog, shouldNavigateBack ->
        DetailTaskScreenState(
            currentTask = currentTask,
            showDeleteDialog = showDeleteDialog,
            shouldNavigateBack = shouldNavigateBack
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = DetailTaskScreenState(null)
    )

    fun onTaskDoneUndoneClick() {
        uiState.value.currentTask?.let { currentTask ->
            viewModelScope.launch {
                repository.updateTask(
                    task = currentTask.copy(isDone = !currentTask.isDone)
                )
            }
        }
    }

    fun onDeleteTaskClick() {
        _showDialog.value = true
    }

    fun onHideDeleteDialog() {
        _showDialog.value = false
    }

    fun onConfirmTaskDeletion() {
        uiState.value.currentTask?.let {
            viewModelScope.launch {
                _showDialog.value = false
                repository.deleteTask(it)
                _shouldNavigateBack.value = true
            }
        }
    }
}

data class DetailTaskScreenState(
    val currentTask: TaskEntity?,
    val showDeleteDialog: Boolean = false,
    val shouldNavigateBack: Boolean = false
)