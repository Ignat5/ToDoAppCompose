package com.example.todoappcompose.unit_tests.all_tasks

import androidx.test.core.app.ActivityScenario.launch
import com.example.todoappcompose.common.data.FakeLocalStoreRepository
import com.example.todoappcompose.common.repository.FakeTasksRepository
import com.example.todoappcompose.data.db.entities.TaskEntity
import com.example.todoappcompose.data.repositories.local.FilterOptions
import com.example.todoappcompose.rules.SetMainDispatcherRule
import com.example.todoappcompose.ui.screens.all_tasks.AllTasksViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalCoroutinesApi::class)
class AllTasksViewModelTest {

    @get: Rule
    val setMainDispatcherRule = SetMainDispatcherRule()

    private lateinit var allTasksViewModel: AllTasksViewModel
    private lateinit var tasksRepository: FakeTasksRepository
    private lateinit var localStoreRepository: FakeLocalStoreRepository

    private val initFilterOption = FilterOptions.ALL

    private val activeTasks = listOf(
        TaskEntity(
            taskId = "0",
            taskName = "taskName0",
            taskDescription = "taskDescription0",
            isDone = false
        ),
        TaskEntity(
            taskId = "1",
            taskName = "taskName1",
            taskDescription = "taskDescription1",
            isDone = false
        )
    )

    private val completedTasks = listOf(
        TaskEntity(
            taskId = "2",
            taskName = "taskName2",
            taskDescription = "taskDescription2",
            isDone = true
        )
    )

    private val allTasks = mutableListOf<TaskEntity>()

    @Before
    fun setUp() = runTest {
        tasksRepository = FakeTasksRepository()
        localStoreRepository = FakeLocalStoreRepository(initFilterOption)
        allTasks.clear()
        allTasks.addAll(activeTasks)
        allTasks.addAll(completedTasks)
        allTasks.forEach {
            tasksRepository.insertTask(it)
        }
        allTasksViewModel = AllTasksViewModel(tasksRepository, localStoreRepository)
    }

    @Test
    fun initializationAndFilter_TasksAreObtainedCorrectly_FilterUpdatesCorrectly() = runTest {
        val scope = launch { allTasksViewModel.uiState.collect() }
        val startTodos = allTasksViewModel.uiState.first().todos
        val startFilterOption = allTasksViewModel.uiState.value.filterOptions
        checkTodosAlignWithFilter(startFilterOption, startTodos)
        // check filter change (FilterOptions.ACTIVE)
        allTasksViewModel.onFilterOptionClick(FilterOptions.ACTIVE)
        assertThat(localStoreRepository.getFilterOptionsFlow().first() == FilterOptions.ACTIVE).isTrue()
        val activeTodos = allTasksViewModel.uiState.first().todos
        checkTodosAlignWithFilter(FilterOptions.ACTIVE, activeTodos)
        // check filter change (FilterOptions.COMPLETED)
        allTasksViewModel.onFilterOptionClick(FilterOptions.COMPLETED)
        assertThat(localStoreRepository.getFilterOptionsFlow().first() == FilterOptions.COMPLETED).isTrue()
        val completedTodos = allTasksViewModel.uiState.first().todos
        checkTodosAlignWithFilter(FilterOptions.COMPLETED, completedTodos)
        scope.cancel()
    }

    @Test
    fun changeCompleteStateOfSingleTask() = runTest {
        val scope = launch { allTasksViewModel.uiState.collect() }
        val startTodos = allTasksViewModel.uiState.first().todos
        val completedTask = completedTasks.firstOrNull() ?: kotlin.run {
            val task = TaskEntity("4", "name", "desc", true)
            tasksRepository.insertTask(task)
            task
        }
        assertThat(tasksRepository.getTaskById(completedTask.taskId)?.isDone).isTrue()
        allTasksViewModel.onTaskIsDoneUndoneClick(completedTask)
        assertThat(tasksRepository.getTaskById(completedTask.taskId)?.isDone).isFalse()
        assertThat((allTasksViewModel.uiState.first().todos.find { it.taskId == completedTask.taskId })?.isDone == false).isTrue()
        scope.cancel()
    }

    @Test
    fun deleteAllCompletedTasks_TasksAreDeleted() = runTest {
        val startTodos = allTasksViewModel.uiState.first().todos
        // check that there are completed tasks
        assertThat(startTodos.filter { it.isDone }).isNotEmpty()
        allTasksViewModel.onClearCompletedTasksClick()
        // check that repo doesn't contain completed tasks
        assertThat(tasksRepository.getAllTasksFlow().first().filter { it.isDone }).isEmpty()
        // check that uiState doesn't contain completed tasks
        val todosAfterClear = allTasksViewModel.uiState.first().todos
        assertThat(todosAfterClear.filter { it.isDone }).isEmpty()
    }

    private fun checkTodosAlignWithFilter(filterOption: FilterOptions, currentTodos: List<TaskEntity>) {
        when (filterOption) {
            FilterOptions.ALL -> assertThat(currentTodos).containsExactlyElementsIn(allTasks)
            FilterOptions.ACTIVE -> assertThat(currentTodos).containsExactlyElementsIn(activeTasks)
            FilterOptions.COMPLETED -> assertThat(currentTodos).containsExactlyElementsIn(completedTasks)
        }
    }

}