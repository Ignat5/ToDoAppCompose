package com.example.todoappcompose

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todoappcompose.common.TestUtils.printSemanticTree
import com.example.todoappcompose.data.db.entities.TaskEntity
import com.example.todoappcompose.data.repositories.tasks.TasksRepository
import com.example.todoappcompose.ui.screens.app.TodoAppScreen
import com.example.todoappcompose.ui.theme.ToDoAppComposeTheme
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@OptIn(ExperimentalCoroutinesApi::class)
class AppUserFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()
    private val activity get() = composeTestRule.activity

    @Inject
    lateinit var repository: TasksRepository

    @Before
    fun init() {
        hiltRule.inject()
    }

    private fun setContent() {
        composeTestRule.setContent {
            ToDoAppComposeTheme {
                TodoAppScreen()
            }
        }
    }

    @Test
    fun clearAllCompleted_tasksAreDeleted() = runTest {
        val allTasks = listOf(
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
        val completedTasks = allTasks.filter { it.isDone }
        allTasks.forEach {
            repository.insertTask(it)
        }
        setContent()
        // check that all tasks are displayed
        val tasksListNode = composeTestRule.onNode(
            hasScrollToIndexAction()
        )
        tasksListNode.assertIsDisplayed()
        tasksListNode.onChildren().assertCountEquals(allTasks.size)
        // clear all completed tasks
        val moreMenuNode = composeTestRule.onNode(
            hasClickAction() and hasContentDescription(activity.getString(R.string.all_tasks_clear_completed_option))
        )
        moreMenuNode.assertIsDisplayed()
        moreMenuNode.performClick()
        val clearCompletedNode = composeTestRule.onNode(
            hasText(activity.getString(R.string.all_tasks_clear_completed_option_title))
        )
        clearCompletedNode.assertIsDisplayed()
        clearCompletedNode.performClick()
        tasksListNode.onChildren().assertCountEquals(allTasks.size - completedTasks.size)
        completedTasks.forEach {
            assertThat(repository.getTaskById(it.taskId)).isNull()
        }
    }

    @Test
    fun updateTask_returnCorrectlyToPreviousScreens_updatedTaskIsDisplayed_changesAreSavedToDataSource() =
        runTest {
            val task = TaskEntity(
                taskId = "0",
                taskName = "taskName",
                taskDescription = "taskDescription",
                isDone = false
            )
            repository.insertTask(task)
            setContent()
            // check that task is displayed
            val taskNode = composeTestRule.onNode(
                hasClickAction() and hasText(task.taskName)
            )
            taskNode.assertIsDisplayed()
            taskNode.performClick()
            // go to details
            composeTestRule.onNode(
                matcher = hasText(task.taskName)
            ).assertIsDisplayed()

            composeTestRule.onNode(
                matcher = hasText(task.taskDescription)
            ).assertIsDisplayed()
            // go to edit screen
            val editNode = composeTestRule.onNode(
                hasClickAction() and hasContentDescription(activity.getString(R.string.common_edit_task_description))
            )
            editNode.assertIsDisplayed()
            editNode.performClick()
            // try to save task with invalid data
            val taskNameTextInputNode = composeTestRule.onNode(
                hasSetTextAction() and hasText(activity.getString(R.string.input_task_name_label))
            )
            val taskDescriptionTextInputNode = composeTestRule.onNode(
                hasSetTextAction() and hasText(activity.getString(R.string.input_task_description_label))
            )
            taskNameTextInputNode.assertIsDisplayed()
            taskNameTextInputNode.performTextClearance()
            taskDescriptionTextInputNode.assertIsDisplayed()
            taskDescriptionTextInputNode.performTextClearance()
            val doneNode = composeTestRule.onNode(
                hasClickAction() and hasContentDescription(activity.getString(R.string.add_edit_task_done))
            )
            doneNode.assertIsDisplayed()
            doneNode.performClick()
            // check that message is shown and data isn't changed
            composeTestRule.onNode(
                hasText(activity.getString(R.string.user_message_task_title_empty))
            ).assertIsDisplayed()
            val taskAfterInvalid = repository.getTaskById(task.taskId)
            assertThat(taskAfterInvalid).isNotNull()
            assertThat(taskAfterInvalid).isEqualTo(task)
            val updatedTask = TaskEntity(
                taskId = task.taskId,
                taskName = "taskNameUpdated",
                taskDescription = "taskNameDescription",
                isDone = task.isDone
            )
            taskNameTextInputNode.performTextInput(updatedTask.taskName)
            taskDescriptionTextInputNode.performTextInput(updatedTask.taskDescription)
            doneNode.performClick()
            // check that you are back to details screen and task's data is updated
            composeTestRule.onNode(
                hasText(updatedTask.taskName)
            ).assertIsDisplayed()

            composeTestRule.onNode(
                hasText(updatedTask.taskDescription)
            ).assertIsDisplayed()

            val checkBoxNode = composeTestRule.onNode(
                isToggleable()
            )

            checkBoxNode.assertIsDisplayed()
            if (task.isDone) checkBoxNode.assertIsOn()
            else checkBoxNode.assertIsOff()
            // go back to all tasks screen
            composeTestRule.onNode(
                hasContentDescription(activity.getString(R.string.common_back_description))
            ).performClick()
            // check that data is updated
            composeTestRule.onNode(
                hasText(updatedTask.taskName)
            ).assertIsDisplayed()

            val checkBoxAllScreensNode = composeTestRule.onNode(
                isToggleable()
            )
            if (task.isDone) checkBoxAllScreensNode.assertIsOn()
            else checkBoxAllScreensNode.assertIsOff()
            // check that data source is updated
            val taskAfterUpdate = repository.getTaskById(task.taskId)
            assertThat(taskAfterUpdate).isNotNull()
            assertThat(taskAfterUpdate?.taskName).isEqualTo(updatedTask.taskName)
            assertThat(taskAfterUpdate?.taskDescription).isEqualTo(updatedTask.taskDescription)
        }

    @Test
    fun createNewTask_taskIsValidated_returnToAllTasksScreen_taskIsDisplayed_andSavedToDataSource() =
        runTest {
            // check that list is empty in the start (it is out of composition if it is empty)
            setContent()
            composeTestRule.onNode(
                matcher = hasText(activity.getString(R.string.filter_option_all_no_tasks_message))
            ).assertIsDisplayed()
            val addTaskNode = composeTestRule.onNode(
                matcher = hasClickAction() and hasContentDescription(activity.getString(R.string.common_add_new_task_description))
            )
            // go to add new task screen
            addTaskNode.assertIsDisplayed()
            addTaskNode.performClick()
            // try to add invalid task
            val taskNameTextFieldNode = composeTestRule.onNode(
                matcher = hasSetTextAction() and hasText(activity.getString(R.string.input_task_name_label))
            )

            val taskDescriptionTextFieldNode = composeTestRule.onNode(
                matcher = hasSetTextAction() and hasText(activity.getString(R.string.input_task_description_label))
            )
            val invalidData = "      "
            taskNameTextFieldNode.assertIsDisplayed()
            taskDescriptionTextFieldNode.assertIsDisplayed()
            taskNameTextFieldNode.performTextInput(invalidData)
            taskDescriptionTextFieldNode.performTextInput(invalidData)
            val doneNode = composeTestRule.onNode(
                matcher = hasClickAction() and hasContentDescription(activity.getString(R.string.add_edit_task_done))
            )
            doneNode.assertIsDisplayed()
            doneNode.performClick()
            // check that task is not saved and message is shown
            val messageNode = composeTestRule.onNode(
                matcher = hasText(activity.getString(R.string.user_message_task_title_empty))
            )
            messageNode.assertIsDisplayed()
            assertThat(repository.getAllTasksFlow().first()).isEmpty()
            // perform valid data input
            val inputTask = TaskEntity(
                taskName = "taskName",
                taskDescription = "taskDescription",
                isDone = false
            )
            taskNameTextFieldNode.performTextClearance()
            taskNameTextFieldNode.performTextInput(inputTask.taskName)
            taskDescriptionTextFieldNode.performTextClearance()
            taskDescriptionTextFieldNode.performTextInput(inputTask.taskDescription)
            doneNode.performClick()
            // check that: 1. we returned to all tasks screen 2. task info is displayed correctly 3. task is saved to data source
            val listNode = composeTestRule.onNode(
                matcher = hasScrollToIndexAction()
            )
            listNode.assertIsDisplayed()
            listNode.onChildren().assertCountEquals(1)
            composeTestRule.onNode(
                matcher = hasText(inputTask.taskName)
            ).assertIsDisplayed()

            composeTestRule.onNode(
                matcher = isToggleable()
            ).assertIsOff()

            assertThat(repository.getAllTasksFlow().first()).hasSize(1)
        }

    @Test
    fun deleteTaskFromDetailsScreen_taskIsDeletedFromDataSource_returnToAllTasksScreen_taskIsNotDisplayed() =
        runTest {
            // save tasks to data source
            val testTask = TaskEntity(
                taskName = "taskName",
                taskDescription = "taskDescription",
                isDone = false
            )
            val anotherTask = TaskEntity(
                taskName = "anotherTaskName",
                taskDescription = "anotherTaskDescription",
                isDone = false
            )
            val allTasks = listOf(testTask, anotherTask)
            allTasks.forEach {
                repository.insertTask(it)
            }
            setContent()
            // check that tasks are displayed and go to details screen
            val columnNodeBefore = composeTestRule.onNode(
                matcher = hasScrollToIndexAction()
            )
            columnNodeBefore.onChildren().assertCountEquals(allTasks.size)
            composeTestRule.printSemanticTree()
            val taskNode = composeTestRule.onNode(
                matcher = hasText(testTask.taskName) and hasClickAction()
            )
            taskNode.assertIsDisplayed()
            taskNode.performClick()
            // check that details of the tasks are displayed
            composeTestRule.onNode(
                matcher = hasText(testTask.taskName)
            ).assertIsDisplayed()

            composeTestRule.onNode(
                matcher = hasText(testTask.taskDescription)
            )
            // delete task
            val deleteNode = composeTestRule.onNode(
                matcher = hasClickAction() and hasContentDescription(activity.getString(R.string.detail_task_delete_task))
            )
            deleteNode.assertIsDisplayed()
            deleteNode.performClick()
            val confirmDeletionNode = composeTestRule.onNode(
                matcher = hasClickAction() and hasText(activity.getString(R.string.common_yes_option))
            )
            confirmDeletionNode.assertIsDisplayed()
            confirmDeletionNode.performClick()
            // check that 1. we returned to all tasks screen 2. that task is not displayed
            val columnNodeAfter = composeTestRule.onNode(
                matcher = hasScrollToIndexAction()
            )
            columnNodeAfter.assertIsDisplayed()
            columnNodeAfter.onChildren().assertCountEquals(allTasks.size - 1)
            // check that task was deleted from data source
            assertThat(repository.getTaskById(testTask.taskId)).isNull()
        }

    @Test
    fun taskIsDisplayedCorrectly_goToDetailsMarkTaskAsDone_returnToAllTasksScreen_taskIsMarkedAsDone() =
        runTest {
            val testTask = TaskEntity(
                taskId = "0",
                taskName = "taskName",
                taskDescription = "taskDescription",
                isDone = false
            )
            repository.insertTask(testTask)
            setContent()
            composeTestRule.printSemanticTree()
            // check that task is displayed with correct data
            val taskNode = composeTestRule.onNode(
                matcher = hasText(testTask.taskName)
            )
            taskNode.assertIsDisplayed()

            val taskCheckBox = composeTestRule.onNode(
                matcher = isToggleable() and hasContentDescription(testTask.taskId)
            )
            taskCheckBox.assertIsDisplayed()
            if (testTask.isDone) taskCheckBox.assertIsOn()
            else taskCheckBox.assertIsOff()
            // go to details screen
            taskNode.performClick()
            // check that task's detail info is correct
            composeTestRule.onNode(
                matcher = hasText(testTask.taskName)
            ).assertIsDisplayed()
            composeTestRule.onNode(
                matcher = hasText(testTask.taskDescription)
            ).assertIsDisplayed()
            val detailTaskCheckBox = composeTestRule.onNode(
                matcher = isToggleable() and hasContentDescription(testTask.taskId)
            )
            detailTaskCheckBox.assertIsDisplayed()
            if (testTask.isDone) detailTaskCheckBox.assertIsOn()
            else detailTaskCheckBox.assertIsOff()
            // mark task as 'done'
            detailTaskCheckBox.performClick()
            //navigate back
            composeTestRule.onNode(
                matcher = hasContentDescription(activity.getString(R.string.common_back_description)) and hasClickAction()
            ).performClick()
            // check that now task's 'isDone' state differs from the start
            val taskCheckBoxAfterChanges = composeTestRule.onNode(
                matcher = isToggleable() and hasContentDescription(testTask.taskId)
            )
            taskCheckBoxAfterChanges.assertIsDisplayed()
            if (testTask.isDone) taskCheckBoxAfterChanges.assertIsOff()
            else taskCheckBoxAfterChanges.assertIsOn()
            val testTaskAfterChanges = repository.getTaskById(testTask.taskId)
            assertThat(testTaskAfterChanges).isNotNull()
            assertThat(testTaskAfterChanges?.isDone != testTask.isDone).isTrue()
        }


}