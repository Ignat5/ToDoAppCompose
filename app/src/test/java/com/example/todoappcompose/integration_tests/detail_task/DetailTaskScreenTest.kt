package com.example.todoappcompose.integration_tests.detail_task

import androidx.activity.ComponentActivity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todoappcompose.common.TestUtils.printSemanticTree
import com.example.todoappcompose.common.repository.FakeTasksRepository
import com.example.todoappcompose.data.db.entities.TaskEntity
import com.example.todoappcompose.data.repositories.tasks.TasksRepository
import com.example.todoappcompose.rules.SetMainDispatcherRule
import com.example.todoappcompose.ui.navigation.NavArg
import com.example.todoappcompose.ui.screens.detail_task.DetailTaskScreen
import com.example.todoappcompose.ui.screens.detail_task.DetailTaskViewModel
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

private const val TEST_TASK_ID = "0"

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(
    application = HiltTestApplication::class,
    instrumentedPackages =
    ["androidx.loader.content"]
)
@OptIn(ExperimentalCoroutinesApi::class)
class DetailTaskScreenTest {

    @get: Rule(order = 0)
    val mainDispatcherRule = SetMainDispatcherRule()

    @get: Rule(order = 1)
    val composeRule = createAndroidComposeRule(ComponentActivity::class.java)

    private val activity get() = composeRule.activity

    private lateinit var repository: TasksRepository
    private lateinit var detailTaskViewModel: DetailTaskViewModel

    private val detailTask = TaskEntity(
        taskId = TEST_TASK_ID,
        taskName = "taskName",
        taskDescription = "taskDescription",
        isDone = false
    )

    @Before
    fun setUp() = runTest {
        repository = FakeTasksRepository()
        repository.insertTask(detailTask)
        detailTaskViewModel = DetailTaskViewModel(
            repository,
            SavedStateHandle(mapOf(NavArg.ARG_TASK_ID to TEST_TASK_ID))
        )

        composeRule.setContent {
            ToDoAppComposeTheme {
                DetailTaskScreen(
                    viewModel = detailTaskViewModel,
                    onBackPressed = { },
                    onEditClicked = {})
            }
        }
    }

    @Test
    fun taskInfoIsCorrectlyDisplayed() = runTest {
        // wait for uiState
        detailTaskViewModel.uiState.first()
        // check that task info is displayed on the screen
        composeRule.printSemanticTree()
        composeRule.onNode(
            matcher = hasText(detailTask.taskName),
            useUnmergedTree = false
        ).assertIsDisplayed()

        composeRule.onNode(
            matcher = hasText(detailTask.taskDescription),
            useUnmergedTree = false
        ).assertIsDisplayed()

        val checkBoxNode = composeRule.onNode(
            matcher = isToggleable(),
            useUnmergedTree = false
        )
        checkBoxNode.assertIsDisplayed()
        if (detailTask.isDone) checkBoxNode.assertIsOn()
        else checkBoxNode.assertIsOff()
    }

    @Test
    fun checkUncheckTaskIsDone__stateChangesAppropriately_changesAreSaved() = runTest {
        // wait for uiState
        detailTaskViewModel.uiState.first()
        // check start state
        val checkBoxNode = composeRule.onNode(
            matcher = isToggleable(),
            useUnmergedTree = false
        )
        checkBoxNode.assertIsDisplayed()
        if (detailTask.isDone) checkBoxNode.assertIsOn()
        else checkBoxNode.assertIsOff()

        // change done state
        checkBoxNode.performClick()
        // check that state is changed and changes are saved correctly
        assertThat(detailTaskViewModel.uiState.first().currentTask?.isDone != detailTask.isDone).isTrue()
        if (detailTask.isDone) checkBoxNode.assertIsOff()
        else checkBoxNode.assertIsOn()
        assertThat(repository.getTaskById(detailTask.taskId)?.isDone != detailTask.isDone).isTrue()
    }

    @Test
    fun checkDeleteTask_taskIsDeleted() = runTest {
        //check repo contains task that we about to delete
        assertThat(repository.getTaskById(TEST_TASK_ID)).isNotNull()
        // click delete icon
        composeRule.onNode(
            matcher = hasContentDescription(activity.getString(com.example.todoappcompose.R.string.detail_task_delete_task)),
            useUnmergedTree = false
        ).performClick()
        // check that uiState for showing dialog is set
        assertThat(detailTaskViewModel.uiState.first().showDeleteDialog).isTrue()
        // check that dialog is shown
        val dialogOptionNode = composeRule.onNode(
            matcher = hasText(activity.getString(com.example.todoappcompose.R.string.common_yes_option)) and hasClickAction(),
            useUnmergedTree = false
        )
        dialogOptionNode.assertIsDisplayed()
        // click 'delete'
        dialogOptionNode.performClick()
        // check that state is correct
        assertThat(detailTaskViewModel.uiState.first().showDeleteDialog).isFalse()
        assertThat(detailTaskViewModel.uiState.first().shouldNavigateBack).isTrue()
        //check that task was deleted from repo
        assertThat(repository.getTaskById(TEST_TASK_ID)).isNull()
    }

}