package com.example.todoappcompose.integration_tests.add_edit_task

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todoappcompose.HiltTestActivity
import com.example.todoappcompose.R
import com.example.todoappcompose.data.repositories.tasks.TasksRepository
import com.example.todoappcompose.ui.screens.add_edit_task.AddEditTaskScreen
import com.example.todoappcompose.ui.screens.add_edit_task.AddEditTaskViewModel
import com.example.todoappcompose.ui.theme.ToDoAppComposeTheme
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
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
import org.robolectric.annotation.TextLayoutMode
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
@TextLayoutMode(TextLayoutMode.Mode.REALISTIC)
@HiltAndroidTest
//@Config(
//    application = HiltTestApplication::class,
//)
@Config(
    application = HiltTestApplication::class,
    instrumentedPackages =
    ["androidx.loader.content"]
)
@OptIn(ExperimentalCoroutinesApi::class)
class AddEditTaskScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()
    private val activity get() = composeTestRule.activity

    @Inject
    lateinit var repository: TasksRepository

    lateinit var addEditTaskViewModel: AddEditTaskViewModel

    @Before
    fun setUp() {
        // inject fields in test class
        hiltRule.inject()
        addEditTaskViewModel = AddEditTaskViewModel(repository, SavedStateHandle())
        // set up compose ui content
        composeTestRule.setContent {
            ToDoAppComposeTheme {
                AddEditTaskScreen(
                    viewModel = addEditTaskViewModel,
                    onBack = { }
                )
            }
        }
    }

    @Test
    fun invalidInput_TaskNotAddedOrUpdated_MessageIsShown() = runTest {
        // just for debug
        println(composeTestRule.onRoot(useUnmergedTree = false).printToString())
        println("***************************")
        println(composeTestRule.onRoot(useUnmergedTree = true).printToString())
        // init state check
        assertThat(addEditTaskViewModel.uiState.first().userMessage).isNull()
        assertThat(addEditTaskViewModel.uiState.first().shouldNavigateBack).isFalse()
        // find nodes
        val taskNameTextFieldNode = composeTestRule.onNode(
            matcher = hasText(activity.getString(R.string.input_task_name_label))
                    and hasSetTextAction(),
            useUnmergedTree = false
        )
        val taskDescriptionTextFieldNode = composeTestRule.onNode(
            matcher = hasText(activity.getString(R.string.input_task_description_label))
                    and hasSetTextAction(),
            useUnmergedTree = false
        )

        val taskDoneBtnNode = composeTestRule.onNode(
            matcher = hasContentDescription(activity.getString(R.string.add_edit_task_done)) and
                    hasClickAction(),
            useUnmergedTree = false
        )
        // perform actions for adding/editing task
        taskNameTextFieldNode.performTextInput("")
        taskDescriptionTextFieldNode.performTextInput("")
        taskDoneBtnNode.performClick()
        // check that state is correct
        assertThat(addEditTaskViewModel.uiState.first().userMessage).isNotNull()
        assertThat(addEditTaskViewModel.uiState.first().shouldNavigateBack).isFalse()
        // check that appropriate message is shown to user
        composeTestRule.onNode(hasText(activity.getString(R.string.user_message_task_title_empty)))
            .assertIsDisplayed()
        // check that repository doesn't contain any new tasks
        assertThat(repository.getAllTasksFlow().first()).isEmpty()
        // just for debug
        println("**********message should be shown************")
        println(composeTestRule.onRoot(useUnmergedTree = false).printToString())
    }

    @Test
    fun validTask_SavedCorrectly() = runTest {
        // check init set up is correct
        assertThat(addEditTaskViewModel.uiState.first().userMessage).isNull()
        assertThat(addEditTaskViewModel.uiState.first().shouldNavigateBack).isFalse()
        assertThat(repository.getAllTasksFlow().first()).isEmpty()
        // fill in fields and click 'done'
        val newTaskName = "testTaskName"
        val newTaskDescription = "testTaskDescription"
        setTextForTaskNameTextField(newTaskName)
        setTextForTaskDescriptionTextField(newTaskDescription)
        performDoneClick()
        // check that state changed and user will navigate back
        assertThat(addEditTaskViewModel.uiState.first().shouldNavigateBack).isTrue()
        assertThat(addEditTaskViewModel.uiState.first().userMessage).isNull()
        // check that task was successfully saved
        assertThat(repository.getAllTasksFlow().first()).isNotEmpty()
    }

    private fun setTextForTaskNameTextField(text: String) {
        composeTestRule.onNode(
            matcher = hasText(activity.getString(R.string.input_task_name_label))
                    and hasSetTextAction(),
            useUnmergedTree = false
        ).performTextInput(text)
    }

    private fun setTextForTaskDescriptionTextField(text: String) {
        composeTestRule.onNode(
            matcher = hasText(activity.getString(R.string.input_task_description_label))
                    and hasSetTextAction(),
            useUnmergedTree = false
        ).performTextInput(text)
    }

    private fun performDoneClick() {
        composeTestRule.onNode(
            matcher = hasContentDescription(activity.getString(R.string.add_edit_task_done)) and
                    hasClickAction(),
            useUnmergedTree = false
        ).performClick()
    }

}