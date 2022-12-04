package com.example.todoappcompose.ui.screens.statistics

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoappcompose.data.repositories.tasks.TasksRepository
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
        val allCount = allTasks.count()
        val activeCount = allTasks.count {
            !it.isDone
        }
        if (allCount == 0) {
            StatisticsScreenState()
        } else {
            val activePercent: Float = (activeCount.toFloat() / allCount.toFloat()) * 100
            val completed = allCount - activeCount
            Log.d("myTag", "allCount: $allCount, activeCount: $activeCount, completed: $completed")
            val completedPercent: Float = (completed.toFloat() / allCount.toFloat()) * 100
            StatisticsScreenState(
                activeTasksStatistics = activePercent,
                completedTasksStatistics = completedPercent
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StatisticsScreenState()
    )
}

data class StatisticsScreenState(
    val activeTasksStatistics: Float = 0.0f,
    val completedTasksStatistics: Float = 0.0f
)