package com.example.todoappcompose.data.repositories.tasks

import com.example.todoappcompose.data.db.dao.TasksDao
import com.example.todoappcompose.data.db.entities.TaskEntity
import com.example.todoappcompose.data.repositories.tasks.TasksRepository

class TasksRepositoryImpl(private val tasksDao: TasksDao): TasksRepository {

    override fun getAllTasksFlow() = tasksDao.getTasksFlow()

    override suspend fun insertTask(task: TaskEntity) {
        tasksDao.insertTask(task)
    }

    override suspend fun updateTask(task: TaskEntity) {
        tasksDao.updateTask(task)
    }

    override suspend fun deleteCompletedTasks() {
        tasksDao.deleteCompletedTasks()
    }

}