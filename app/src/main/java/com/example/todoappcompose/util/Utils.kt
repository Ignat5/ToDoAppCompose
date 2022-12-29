package com.example.todoappcompose.util

import com.example.todoappcompose.data.db.entities.TaskEntity

object Utils {
    fun getRoundedPercent(percent: Float, digits: Int) =
        String.format("%.${digits}f", percent)

    fun calculateTasksStatistics(tasks: List<TaskEntity>): StatisticsModel {
        val allTasksCount = tasks.count()
        if (allTasksCount == 0) {
            return StatisticsModel(
                activeTasksPercent = 0f,
                completedTasksPercent = 0f
            )
        }
        val activeCount = tasks.count {
            !it.isDone
        }
        val completedCount = allTasksCount - activeCount
        val activeTasksPercent: Float = activeCount.toFloat() / allTasksCount.toFloat()
        val completedTasksPercent: Float = completedCount.toFloat() / allTasksCount.toFloat()
        return StatisticsModel(
            activeTasksPercent = activeTasksPercent * 100,
            completedTasksPercent = completedTasksPercent * 100
        )
    }
}

data class StatisticsModel(
    val activeTasksPercent: Float,
    val completedTasksPercent: Float
)