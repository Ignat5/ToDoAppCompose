package com.example.todoappcompose.unit_tests.add_edit_task

import androidx.lifecycle.SavedStateHandle
import com.example.todoappcompose.common.repository.FakeTasksRepository
import com.example.todoappcompose.data.db.entities.TaskEntity
import com.example.todoappcompose.rules.SetMainDispatcherRule
import com.example.todoappcompose.ui.navigation.NavArg
import com.example.todoappcompose.ui.screens.add_edit_task.AddEditTaskViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private const val TEST_ARG_TASK_ID = "0"
private const val TEST_TASK_NAME = "testTaskName"
private const val TEST_TASK_DESCRIPTION = "testTaskDescription"
private const val TEST_TASK_IS_DONE = false

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditTaskViewModelTest {

    @get:Rule
    val setMainDispatcherRule: SetMainDispatcherRule = SetMainDispatcherRule()

    private lateinit var addEditTaskViewModel: AddEditTaskViewModel
    private lateinit var repository: FakeTasksRepository

    private val testTask1 = TaskEntity(
        taskId = TEST_ARG_TASK_ID,
        taskName = TEST_TASK_NAME,
        taskDescription = TEST_TASK_DESCRIPTION,
        isDone = TEST_TASK_IS_DONE
    )

    @Before
    fun setUp() = runTest {
        repository = FakeTasksRepository()
        repository.insertTask(testTask1)
    }

    private fun setUpViewModelAddTask() {
        addEditTaskViewModel = AddEditTaskViewModel(repository, SavedStateHandle())
    }

    private fun setUpViewModelEditTask() {
        addEditTaskViewModel = AddEditTaskViewModel(repository, SavedStateHandle(mapOf(NavArg.ARG_TASK_ID to TEST_ARG_TASK_ID)))
    }

    @Test
    fun inputValidation_incorrectTaskNotAdded_messageShown() = runTest {
        setUpViewModelAddTask()
        addEditTaskViewModel.onTaskTitleChanged(taskTitle = "")
        addEditTaskViewModel.onTaskDescriptionChanged(taskDescription = "")
        addEditTaskViewModel.onAddEditTaskFinishedClick()
        //task was not saved
        assertThat(repository.currentTasksState.value.isEmpty()).isTrue()
        // message is shown to user
        assertThat(addEditTaskViewModel.uiState.value.userMessage != null)
    }

    @Test
    fun addValidTask_taskIsSaved() {
        setUpViewModelAddTask()
        val taskName = "task1"
        val taskDescription = "description1"
        addEditTaskViewModel.onTaskTitleChanged(taskName)
        addEditTaskViewModel.onTaskDescriptionChanged(taskDescription)
        addEditTaskViewModel.onAddEditTaskFinishedClick()
        //task was saved with correct data
        val savedTask = repository.currentTasksState.value.values.firstOrNull()
        assertThat(savedTask != null).isTrue()
        assertThat(savedTask?.taskName == taskName).isTrue()
        assertThat(savedTask?.taskDescription == taskDescription).isTrue()
        assertThat(savedTask?.isDone == false).isTrue()
        //state after successful add is correct
        assertThat(addEditTaskViewModel.uiState.value.shouldNavigateBack).isTrue()
    }

    @Test
    fun editTask_detailTaskIsGotten() = runTest {
        setUpViewModelEditTask()
        val currentTask = addEditTaskViewModel.uiState.value.currentTask
        assertThat(currentTask != null).isTrue()
        assertThat(currentTask?.taskId == TEST_ARG_TASK_ID).isTrue()
        assertThat(currentTask?.taskName == TEST_TASK_NAME).isTrue()
        assertThat(currentTask?.taskDescription == TEST_TASK_DESCRIPTION).isTrue()
        assertThat(currentTask?.isDone == TEST_TASK_IS_DONE).isTrue()
    }

    @Test
    fun inputValidation_incorrectTaskNotEdited_messageShown() = runTest {
        setUpViewModelEditTask()
        addEditTaskViewModel.onTaskTitleChanged(taskTitle = "")
        addEditTaskViewModel.onTaskDescriptionChanged(taskDescription = "")
        addEditTaskViewModel.onAddEditTaskFinishedClick()
        //task have not been edited
        val currentTask = repository.getTaskById(TEST_ARG_TASK_ID)
        assertThat(currentTask?.taskName == TEST_TASK_NAME).isTrue()
        assertThat(currentTask?.taskDescription == TEST_TASK_DESCRIPTION).isTrue()
        // message is shown to user
        assertThat(addEditTaskViewModel.uiState.value.userMessage != null)
    }

    @Test
    fun editValidTask_TaskIsUpdated() = runTest {
        setUpViewModelEditTask()
        val newTitle = "testTaskNewTitle"
        val newDescription = "testTaskNewDescription"
        addEditTaskViewModel.onTaskTitleChanged(newTitle)
        addEditTaskViewModel.onTaskDescriptionChanged(newDescription)
        addEditTaskViewModel.onAddEditTaskFinishedClick()
        val updatedTask = repository.getTaskById(TEST_ARG_TASK_ID)
        assertThat(updatedTask != null).isTrue()
        assertThat(updatedTask?.taskName == newTitle).isTrue()
        assertThat(updatedTask?.taskDescription == newDescription).isTrue()
        assertThat(addEditTaskViewModel.uiState.value.shouldNavigateBack).isTrue()
    }

}