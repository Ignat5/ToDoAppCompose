package com.example.todoappcompose.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoappcompose.data.repositories.tasks.TasksRepository
import com.example.todoappcompose.util.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: TasksRepository
) : ViewModel() {
    val uiState: StateFlow<StatisticsScreenState> = repository.getAllTasksFlow().map { allTasks ->
        val statisticsModel = Utils.calculateTasksStatistics(tasks = allTasks)
        StatisticsScreenState(
            activeTasksStatistics = statisticsModel.activeTasksPercent,
            completedTasksStatistics = statisticsModel.completedTasksPercent
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StatisticsScreenState()
    )
}

data class StatisticsScreenState(
    val activeTasksStatistics: Float = 0f,
    val completedTasksStatistics: Float = 0f
)