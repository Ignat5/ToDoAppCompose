package com.example.todoappcompose.ui.screens.all_tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoappcompose.data.db.entities.TaskEntity
import com.example.todoappcompose.data.repositories.local.FilterOptions
import com.example.todoappcompose.data.repositories.local.LocalStoreRepository
import com.example.todoappcompose.data.repositories.tasks.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllTasksViewModel @Inject constructor(
    private val tasksRepository: TasksRepository,
    private val localStoreRepository: LocalStoreRepository
) : ViewModel() {

    private val filterOptionsFlow = localStoreRepository.getFilterOptionsFlow()

    val uiState: StateFlow<AllTasksScreenState> = tasksRepository.getAllTasksFlow()
        .combine(filterOptionsFlow) { tasks, filterOptions ->
            AllTasksScreenState(
                todos = filterTasks(tasks, filterOptions),
                filterOptions = filterOptions
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AllTasksScreenState(emptyList(), FilterOptions.ALL)
        )

    private fun filterTasks(tasks: List<TaskEntity>, filterOptions: FilterOptions) =
        when (filterOptions) {
            FilterOptions.ALL -> tasks
            FilterOptions.ACTIVE -> tasks.filter { !it.isDone }
            FilterOptions.COMPLETED -> tasks.filter { it.isDone }
        }

    fun onFilterOptionClick(filterOptions: FilterOptions) {
        viewModelScope.launch {
            localStoreRepository.saveFilterOption(filterOptions)
        }
    }

    fun onTaskIsDoneUndoneClick(task: TaskEntity) {
        viewModelScope.launch {
            tasksRepository.updateTask(task.copy(isDone = !task.isDone))
        }
    }

    fun onClearCompletedTasksClick() {
        viewModelScope.launch {
            tasksRepository.deleteCompletedTasks()
        }
    }

    fun addTestTask() {
        val task = TaskEntity(
            taskName = "task + ${System.currentTimeMillis().toString()}",
            taskDescription = "task descr",
            isDone = false
        )
        viewModelScope.launch {
            tasksRepository.insertTask(task)
        }
    }

}

data class AllTasksScreenState(
    val todos: List<TaskEntity>,
    val filterOptions: FilterOptions
)