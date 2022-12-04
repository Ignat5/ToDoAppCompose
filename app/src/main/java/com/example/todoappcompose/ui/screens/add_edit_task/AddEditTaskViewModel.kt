package com.example.todoappcompose.ui.screens.add_edit_task

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoappcompose.R
import com.example.todoappcompose.data.db.entities.TaskEntity
import com.example.todoappcompose.data.repositories.tasks.TasksRepository
import com.example.todoappcompose.ui.navigation.NavArg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TasksRepository
) : ViewModel() {

    private val taskId: String? = savedStateHandle[NavArg.ARG_TASK_ID]

    private val _uiState: MutableStateFlow<AddEditScreenState> =
        MutableStateFlow(AddEditScreenState())
    val uiState: StateFlow<AddEditScreenState> = _uiState.asStateFlow()

    init {
        if (taskId != null) {
            getTaskById(taskId)
        }
    }

    private fun getTaskById(taskId: String) {
        viewModelScope.launch {
            repository.getTaskById(taskId)?.let { currentTask ->
                _uiState.update {
                    AddEditScreenState(
                        taskTitle = currentTask.taskName,
                        taskDescription = currentTask.taskDescription,
                        it.shouldNavigateBack,
                        it.userMessage,
                        currentTask
                    )
                }
            }
        }
    }

    fun onAddEditTaskFinishedClick() {
        val taskTitle = uiState.value.taskTitle
        if (taskTitle.isBlank()) {
            _uiState.update { currentScreenState ->
                currentScreenState.copy(
                    userMessage = R.string.user_message_task_title_empty
                )
            }
        } else viewModelScope.launch {
            val currentTask = uiState.value.currentTask
            val insertTask = if (currentTask != null)
                TaskEntity(
                    taskId = currentTask.taskId,
                    taskName = uiState.value.taskTitle,
                    taskDescription = uiState.value.taskDescription,
                    isDone = uiState.value.currentTask?.isDone ?: false
                )
            else
                TaskEntity(
                    taskName = uiState.value.taskTitle,
                    taskDescription = uiState.value.taskDescription,
                    isDone = uiState.value.currentTask?.isDone ?: false
                )
            repository.insertTask(insertTask)
            _uiState.update {
                it.copy(shouldNavigateBack = true)
            }
        }
    }

    fun onTaskTitleChanged(taskTitle: String) {
        _uiState.update {
            AddEditScreenState(
                taskTitle = taskTitle,
                taskDescription = it.taskDescription,
                currentTask = it.currentTask
            )
        }
    }

    fun onTaskDescriptionChanged(taskDescription: String) {
        _uiState.update {
            AddEditScreenState(
                taskTitle = it.taskTitle,
                taskDescription = taskDescription,
                currentTask = it.currentTask
            )
        }
    }

    fun onUserMessageShown() {
        _uiState.update { currentScreenState ->
            currentScreenState.copy(
                userMessage = null
            )
        }
    }
}

data class AddEditScreenState(
    val taskTitle: String = "",
    val taskDescription: String = "",
    val shouldNavigateBack: Boolean = false,
    @StringRes val userMessage: Int? = null,
    val currentTask: TaskEntity? = null
)