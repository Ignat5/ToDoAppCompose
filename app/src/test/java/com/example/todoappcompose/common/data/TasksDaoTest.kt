package com.example.todoappcompose.common.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todoappcompose.data.db.database.AppDatabase
import com.example.todoappcompose.data.db.entities.TaskEntity
import com.example.todoappcompose.rules.SetMainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val TEST_TASK_ID = "0"

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class TasksDaoTest {

    @get: Rule(order = 0)
    val setMainDispatcherRule = SetMainDispatcherRule()

    @get: Rule(order = 1)
    val executorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private val tasksDao get() = database.tasksDao()

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @Test
    fun insertTask_GetTaskById() = runTest {
        val testInsertTask = TaskEntity(
            taskId = TEST_TASK_ID,
            taskName = "taskName",
            taskDescription = "taskDescription",
            isDone = false
        )
        tasksDao.insertTask(testInsertTask)
        val testGetTask = tasksDao.getTaskById(testInsertTask.taskId)
        assertThat(testInsertTask == testGetTask)
    }

    @Test
    fun updateTask_GetTaskById() = runTest {
        val testInsertTask = TaskEntity(
            taskId = TEST_TASK_ID,
            taskName = "taskName",
            taskDescription = "taskDescription",
            isDone = false
        )
        tasksDao.insertTask(testInsertTask)
        tasksDao.updateTask(testInsertTask.copy(isDone = !testInsertTask.isDone))
        val testGetTask = tasksDao.getTaskById(testInsertTask.taskId)
        assertThat(testGetTask).isNotNull()
        assertThat(testGetTask?.isDone != testInsertTask.isDone).isTrue()
        val testGetTaskReverse = testGetTask?.copy(isDone = !testGetTask.isDone)
        assertThat(testGetTaskReverse).isEqualTo(testInsertTask)
    }

    @Test
    fun deleteTask_GetTaskById() = runTest {
        val testInsertTask = TaskEntity(
            taskId = TEST_TASK_ID,
            taskName = "taskName",
            taskDescription = "taskDescription",
            isDone = false
        )
        tasksDao.insertTask(testInsertTask)
        assertThat(tasksDao.getTaskById(testInsertTask.taskId)).isNotNull()
        tasksDao.deleteTask(testInsertTask)
        assertThat(tasksDao.getTaskById(testInsertTask.taskId)).isNull()
    }

    @Test
    fun deleteCompletedTasks() = runTest {
        val testUndoneTask = TaskEntity(
            taskId = "1",
            taskName = "taskName1",
            taskDescription = "taskDescription1",
            isDone = false
        )
        val testDoneTask = TaskEntity(
            taskId = "2",
            taskName = "taskName2",
            taskDescription = "taskDescription2",
            isDone = true
        )
        tasksDao.insertTask(testUndoneTask)
        tasksDao.insertTask(testDoneTask)
        val tasks = tasksDao.getTasksFlow().first()
        assertThat(tasks.contains(testUndoneTask)).isTrue()
        assertThat(tasks.contains(testDoneTask)).isTrue()
        tasksDao.deleteCompletedTasks()
        val tasksAfterDeletion = tasksDao.getTasksFlow().first()
        assertThat(tasksAfterDeletion.contains(testUndoneTask)).isTrue()
        assertThat(tasksAfterDeletion.contains(testDoneTask)).isFalse()
    }

    @After
    fun cleanUp() {
        database.close()
    }

}