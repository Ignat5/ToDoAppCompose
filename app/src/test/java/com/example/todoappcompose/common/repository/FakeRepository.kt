package com.example.todoappcompose.common.repository

import com.example.todoappcompose.data.db.entities.TaskEntity
import com.example.todoappcompose.data.repositories.tasks.TasksRepository
import kotlinx.coroutines.flow.*

class FakeRepository : TasksRepository {

    private val _currentTasksState: MutableStateFlow<LinkedHashMap<String, TaskEntity>> =
        MutableStateFlow(LinkedHashMap())
    val currentTasksState: StateFlow<LinkedHashMap<String, TaskEntity>> = _currentTasksState

    override fun getAllTasksFlow(): Flow<List<TaskEntity>> {
        return currentTasksState.map {
            it.values.toList()
        }
    }

    override fun getTaskFlowById(taskId: String): Flow<TaskEntity?> {
        return currentTasksState.map { tasks ->
            tasks.values
                .toList()
                .firstOrNull { it.taskId == taskId }
        }
    }

    override suspend fun getTaskById(taskId: String): TaskEntity? {
        return currentTasksState.value[taskId]
    }

    override suspend fun insertTask(task: TaskEntity) {
        _currentTasksState.update { tasksState ->
            val newTasks = LinkedHashMap(tasksState)
            newTasks[task.taskId] = task
            newTasks
        }
    }

    override suspend fun updateTask(task: TaskEntity) {
        _currentTasksState.update { tasks ->
            val newTasks = LinkedHashMap(tasks)
            newTasks[task.taskId] = task
            newTasks
        }
    }

    override suspend fun deleteTask(task: TaskEntity) {
        _currentTasksState.update { tasks ->
            val newTasks = LinkedHashMap(tasks)
            newTasks.remove(task.taskId)
            newTasks
        }
    }

    override suspend fun deleteCompletedTasks() {
        _currentTasksState.update { tasks ->
            val newTasks = LinkedHashMap(tasks)
            val completed = tasks.filter { it.value.isDone }
            completed.forEach {
                newTasks.remove(it.key)
            }
            newTasks
        }
    }

}