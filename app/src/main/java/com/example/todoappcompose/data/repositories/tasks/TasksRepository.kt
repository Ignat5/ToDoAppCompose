package com.example.todoappcompose.data.repositories.tasks

import com.example.todoappcompose.data.db.entities.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TasksRepository {

    fun getAllTasksFlow(): Flow<List<TaskEntity>>

    fun getTaskFlowById(taskId: String): Flow<TaskEntity?>

    suspend fun getTaskById(taskId: String): TaskEntity?

    suspend fun insertTask(task: TaskEntity)

    suspend fun updateTask(task: TaskEntity)

    suspend fun deleteTask(task: TaskEntity)

    suspend fun deleteCompletedTasks()

}