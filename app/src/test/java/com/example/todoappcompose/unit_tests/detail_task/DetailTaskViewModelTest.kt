package com.example.todoappcompose.unit_tests.detail_task

import androidx.lifecycle.SavedStateHandle
import com.example.todoappcompose.common.repository.FakeRepository
import com.example.todoappcompose.data.db.entities.TaskEntity
import com.example.todoappcompose.data.repositories.tasks.TasksRepository
import com.example.todoappcompose.rules.SetMainDispatcherRule
import com.example.todoappcompose.ui.navigation.NavArg
import com.example.todoappcompose.ui.screens.detail_task.DetailTaskViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


private const val TEST_TASK_ID = "0"
private const val TEST_TASK_NAME = "taskNameTest"
private const val TEST_TASK_DESCRIPTION = "taskDescriptionTest"
private const val TEST_TASK_IS_DONE = false

@OptIn(ExperimentalCoroutinesApi::class)
class DetailTaskViewModelTest {

    private lateinit var detailTaskViewModel: DetailTaskViewModel
    private lateinit var repository: TasksRepository

    private val testTask = TaskEntity(
        TEST_TASK_ID,
        TEST_TASK_NAME,
        TEST_TASK_DESCRIPTION,
        TEST_TASK_IS_DONE
    )

    @get: Rule
    val setMainDispatcherRule: SetMainDispatcherRule = SetMainDispatcherRule()

    @Before
    fun setUp() = runTest {
        repository = FakeRepository()
        repository.insertTask(testTask)
        detailTaskViewModel = DetailTaskViewModel(
            repository,
            SavedStateHandle(mapOf(NavArg.ARG_TASK_ID to TEST_TASK_ID))
        )
    }

    @Test
    fun detailTask_IsObtainedAndCorrect() = runTest {
        val currentTask = detailTaskViewModel.uiState.first().currentTask
        assertThat(currentTask).isNotNull()
        assertThat(currentTask?.taskId).isEqualTo(testTask.taskId)
        assertThat(currentTask?.taskName).isEqualTo(testTask.taskName)
        assertThat(currentTask?.taskDescription).isEqualTo(testTask.taskDescription)
        assertThat(currentTask?.isDone).isEqualTo(testTask.isDone)
    }

    @Test
    fun changeDoneTaskState_Task_Updated() = runTest {
        val collectScope = launch {
            detailTaskViewModel.uiState.collect()
        }
        val currentTask = detailTaskViewModel.uiState.first().currentTask
        assertThat(currentTask).isNotNull()
        assertThat(currentTask?.isDone == TEST_TASK_IS_DONE).isTrue()
        detailTaskViewModel.onTaskDoneUndoneClick()
        assertThat(repository.getTaskById(currentTask?.taskId!!)?.isDone == !TEST_TASK_IS_DONE).isTrue()
        assertThat(detailTaskViewModel.uiState.first().currentTask?.isDone == !TEST_TASK_IS_DONE).isTrue()
        collectScope.cancel()
    }

    @Test
    fun deleteTask_TaskIsDeleted() = runTest {
        val currentTask = detailTaskViewModel.uiState.first().currentTask
        assertThat(currentTask).isNotNull()
        val taskBeforeDeletion = repository.getTaskById(currentTask?.taskId!!)
        assertThat(taskBeforeDeletion).isNotNull()
        detailTaskViewModel.onConfirmTaskDeletion()
        val taskAfterDeletion = repository.getTaskById(currentTask.taskId)
        assertThat(taskAfterDeletion).isNull()
        assertThat(detailTaskViewModel.uiState.first().shouldNavigateBack).isTrue()
    }

}