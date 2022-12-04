package com.example.todoappcompose.data.repositories.tasks

import com.example.todoappcompose.data.db.dao.TasksDao
import com.example.todoappcompose.data.db.entities.TaskEntity
import com.example.todoappcompose.data.repositories.tasks.TasksRepository
import kotlinx.coroutines.flow.Flow

class TasksRepositoryImpl(private val tasksDao: TasksDao): TasksRepository {

    override fun getAllTasksFlow() = tasksDao.getTasksFlow()

    override fun getTaskFlowById(taskId: String): Flow<TaskEntity?> = tasksDao.getTaskFlowById(taskId)

    override suspend fun getTaskById(taskId: String): TaskEntity? = tasksDao.getTaskById(taskId)

    override suspend fun insertTask(task: TaskEntity) {
        tasksDao.insertTask(task)
    }

    override suspend fun updateTask(task: TaskEntity) {
        tasksDao.updateTask(task)
    }

    override suspend fun deleteTask(task: TaskEntity) {
        tasksDao.deleteTask(task)
    }

    override suspend fun deleteCompletedTasks() {
        tasksDao.deleteCompletedTasks()
    }

}