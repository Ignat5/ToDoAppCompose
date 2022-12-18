package com.example.todoappcompose.integration_tests.all_tasks

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todoappcompose.common.TestUtils.printSemanticTree
import com.example.todoappcompose.common.data.FakeLocalStoreRepository
import com.example.todoappcompose.common.repository.FakeTasksRepository
import com.example.todoappcompose.data.db.entities.TaskEntity
import com.example.todoappcompose.data.repositories.local.FilterOptions
import com.example.todoappcompose.data.repositories.local.LocalStoreRepository
import com.example.todoappcompose.data.repositories.tasks.TasksRepository
import com.example.todoappcompose.rules.SetMainDispatcherRule
import com.example.todoappcompose.ui.screens.all_tasks.AllTasksScreen
import com.example.todoappcompose.ui.screens.all_tasks.AllTasksViewModel
import com.example.todoappcompose.ui.theme.ToDoAppComposeTheme
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(
    application = HiltTestApplication::class,
    instrumentedPackages =
    ["androidx.loader.content"]
)
@OptIn(ExperimentalCoroutinesApi::class)
class AllTasksScreenTest {

    @get: Rule(order = 0)
    val setMainDispatcherRule = SetMainDispatcherRule()

    @get: Rule(order = 1)
    val composeRule = createAndroidComposeRule<ComponentActivity>()
    private val activity get() = composeRule.activity

    private lateinit var tasksRepository: TasksRepository
    private lateinit var localStoreRepository: LocalStoreRepository
    private lateinit var allTasksViewModel: AllTasksViewModel

    // don't put too many items - they all should be displayed at the screen from the start or test 'savedTasksAreDisplayed' may fail
    private val allTasks = listOf(
        TaskEntity(
            taskName = "taskName1",
            taskDescription = "taskDescription1",
            isDone = false
        ),
        TaskEntity(
            taskName = "taskName2",
            taskDescription = "taskDescription2",
            isDone = true
        )
    )

    @Before
    fun setUp() = runTest {
        tasksRepository = FakeTasksRepository()
        allTasks.forEach {
            tasksRepository.insertTask(it)
        }
        localStoreRepository = FakeLocalStoreRepository(FilterOptions.ALL)
        allTasksViewModel = AllTasksViewModel(tasksRepository, localStoreRepository)

        composeRule.setContent {
            ToDoAppComposeTheme {
                AllTasksScreen(
                    viewModel = allTasksViewModel,
                    onTaskClicked = {},
                    onAddTaskClicked = {},
                    onDrawerClick = {})
            }
        }
    }

    @Test
    fun savedTasksAreDisplayedCorrectly() = runTest {
        allTasksViewModel.uiState.first()
        composeRule.printSemanticTree()
        assertThat(allTasksViewModel.uiState.value.filterOptions == FilterOptions.ALL).isTrue()
        // check that all tasks are displayed at the same time - depends on input test set
        assertThat(allTasks.size < 5).isTrue()
        allTasks.forEach { task ->
            val taskItemNode = composeRule.onNode(
                matcher = hasText(task.taskName),
                useUnmergedTree = false
            )
            taskItemNode.assertIsDisplayed()
            val taskCheckboxNode =
                composeRule.onNode(hasContentDescription(task.taskId) and isToggleable())
            if (task.isDone) taskCheckboxNode.assertIsOn()
            else taskCheckboxNode.assertIsOff()
        }
    }

    @Test
    fun checkTaskAsDoneUndone_StateIsChanged_ChangesAreSaved() = runTest {
        allTasksViewModel.uiState.first()
        assertThat(
            allTasksViewModel.uiState.value.filterOptions in listOf(
                FilterOptions.ALL,
                FilterOptions.ACTIVE
            )
        ).isTrue()
        val activeTasks = allTasks.filter { !it.isDone }
        assertThat(activeTasks).isNotEmpty()
        val testTask = activeTasks.first()
        // undone -> done state change
        val activeTaskCheckBoxNode =
            composeRule.onNode(hasContentDescription(testTask.taskId) and isToggleable())
        activeTaskCheckBoxNode.assertIsOff()
        activeTaskCheckBoxNode.performClick()
        // check that screen state is changed correctly
        assertThat((allTasksViewModel.uiState.first().todos.find { it.taskId == testTask.taskId })?.isDone != testTask.isDone).isTrue()
        // check that ui state is changed correctly
        composeRule.onNode(hasContentDescription(testTask.taskId) and isToggleable()).assertIsOn()
        // check that data is changed correctly
        assertThat(tasksRepository.getTaskById(testTask.taskId)?.isDone).isTrue()
    }

    @Test
    fun changeFilterOptions_AppropriateStateIsSet() = runTest {
        // check that we start with option 'ALL'
        assertThat(allTasksViewModel.uiState.first().filterOptions == FilterOptions.ALL).isTrue()
        val filterMenuNode = composeRule.onNode(
            hasContentDescription(activity.getString(com.example.todoappcompose.R.string.all_tasks_filter_option)) and hasClickAction()
        ).also {
            it.performClick()
        }
        // check that menu item is shown and click it (ACTIVE option)
        composeRule.onNode(
            hasText(activity.getString(com.example.todoappcompose.R.string.filter_option_active)) and hasClickAction()
        ).performClick()
        // check that state was changed appropriately
        val currentUiStateActive = allTasksViewModel.uiState.first()
        assertThat(currentUiStateActive.filterOptions == FilterOptions.ACTIVE).isTrue()
        assertThat(currentUiStateActive.todos.all { !it.isDone }).isTrue()

        filterMenuNode.performClick()
        // check that menu item is shown and click it (COMPLETED option)
        composeRule.onNode(
            hasText(activity.getString(com.example.todoappcompose.R.string.filter_option_completed)) and hasClickAction()
        ).performClick()
        // check that state was changed appropriately
        val currentUiStateCompleted = allTasksViewModel.uiState.first()
        assertThat(currentUiStateCompleted.filterOptions == FilterOptions.COMPLETED).isTrue()
        assertThat(currentUiStateCompleted.todos.all { it.isDone }).isTrue()
    }

    @Test
    fun tasksEmpty_AppropriateMessageIsShown() = runTest {
        // clear all tasks
        allTasks.forEach {
            tasksRepository.deleteTask(it)
        }
        // ALL
        assertThat(allTasksViewModel.uiState.first().todos).isEmpty()
        assertThat(allTasksViewModel.uiState.first().filterOptions == FilterOptions.ALL).isTrue()
        composeRule.onNode(hasText(activity.getString(com.example.todoappcompose.R.string.filter_option_all_title)))
        composeRule.onNode(hasText(activity.getString(com.example.todoappcompose.R.string.filter_option_all_no_tasks_message)))
        // ACTIVE
        changeFilterOption(FilterOptions.ACTIVE)
        assertThat(allTasksViewModel.uiState.first().filterOptions == FilterOptions.ACTIVE).isTrue()
        composeRule.onNode(hasText(activity.getString(com.example.todoappcompose.R.string.filter_option_active_title)))
        composeRule.onNode(hasText(activity.getString(com.example.todoappcompose.R.string.filter_option_active_no_active_tasks_message)))
        // COMPLETED
        changeFilterOption(FilterOptions.COMPLETED)
        assertThat(allTasksViewModel.uiState.first().filterOptions == FilterOptions.COMPLETED).isTrue()
        composeRule.onNode(hasText(activity.getString(com.example.todoappcompose.R.string.filter_option_completed_title)))
        composeRule.onNode(hasText(activity.getString(com.example.todoappcompose.R.string.filter_option_completed_no_completed_tasks_message)))
    }

    private fun changeFilterOption(filterOptions: FilterOptions) {
        composeRule.onNode(
            hasContentDescription(activity.getString(com.example.todoappcompose.R.string.all_tasks_filter_option)) and hasClickAction()
        ).performClick()
        val filterText = when (filterOptions) {
            FilterOptions.ALL -> activity.getString(com.example.todoappcompose.R.string.all_tasks_filter_option)
            FilterOptions.ACTIVE -> activity.getString(com.example.todoappcompose.R.string.filter_option_active)
            FilterOptions.COMPLETED -> activity.getString(com.example.todoappcompose.R.string.filter_option_completed)

        }
        composeRule.onNode(
            hasText(filterText) and hasClickAction()
        ).performClick()
    }

    @Test
    fun clearAllCompletedTasks_ChangesAreSaved() = runTest {
        assertThat(tasksRepository.getAllTasksFlow().first().filter { it.isDone }).isNotEmpty()
        composeRule.onNode(
            hasContentDescription(activity.getString(com.example.todoappcompose.R.string.all_tasks_clear_completed_option))
                    and hasClickAction()
        ).performClick()
        composeRule.onNode(
            hasText(activity.getString(com.example.todoappcompose.R.string.all_tasks_clear_completed_option_title))
                    and hasClickAction()
        ).performClick()
        val currentUiState = allTasksViewModel.uiState.first()
        assertThat(currentUiState.todos.all { !it.isDone }).isTrue()
        assertThat(tasksRepository.getAllTasksFlow().first().all { !it.isDone }).isTrue()
    }


}