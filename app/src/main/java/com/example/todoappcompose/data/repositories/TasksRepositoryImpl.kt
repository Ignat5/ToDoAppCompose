package com.example.todoappcompose.data.repositories

import com.example.todoappcompose.data.db.dao.TasksDao
import com.example.todoappcompose.data.db.entities.TaskEntity

class TasksRepositoryImpl(private val tasksDao: TasksDao): TasksRepository {

    override fun getAllTasksFlow() = tasksDao.getTasksFlow()

    override suspend fun insertTask(task: TaskEntity) {
        tasksDao.insertTask(task)
    }

}