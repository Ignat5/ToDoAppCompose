package com.example.todoappcompose

import com.example.todoappcompose.data.db.entities.TaskEntity
import com.example.todoappcompose.util.Utils
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class UtilsTest {

    @Test
    fun calculateTasksStatistics_allActive() {
        val testTasksList = listOf(
            TaskEntity(
                taskName = "taskName",
                taskDescription = "taskDesc",
                isDone = false
            )
        )
        val statisticsResult = Utils.calculateTasksStatistics(testTasksList)
        assertThat(statisticsResult.activeTasksPercent, `is`(100f))
        assertThat(statisticsResult.completedTasksPercent, `is`(0f))
    }

    @Test
    fun calculateTasksStatistics_allCompleted() {
        val testTasksList = listOf(
            TaskEntity(
                taskName = "taskName",
                taskDescription = "taskDesc",
                isDone = true
            )
        )
        val statisticsResult = Utils.calculateTasksStatistics(testTasksList)
        assertThat(statisticsResult.activeTasksPercent, `is`(0f))
        assertThat(statisticsResult.completedTasksPercent, `is`(100f))
    }

    @Test
    fun calculateTasksStatistics_equal() {
        val testTasksList = listOf(
            TaskEntity(
                taskName = "taskName",
                taskDescription = "taskDesc",
                isDone = true
            ),
            TaskEntity(
                taskName = "taskName",
                taskDescription = "taskDesc",
                isDone = false
            )
        )
        val statisticsResult = Utils.calculateTasksStatistics(testTasksList)
        assertThat(statisticsResult.activeTasksPercent, `is`(50f))
        assertThat(statisticsResult.completedTasksPercent, `is`(50f))
    }

    @Test
    fun calculateTasksStatistics_noTasks() {
        val testTasksList = listOf<TaskEntity>()
        val statisticsResult = Utils.calculateTasksStatistics(testTasksList)
        assertThat(statisticsResult.activeTasksPercent, `is`(0f))
        assertThat(statisticsResult.completedTasksPercent, `is`(0f))
    }
}