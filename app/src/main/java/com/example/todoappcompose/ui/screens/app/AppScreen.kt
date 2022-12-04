package com.example.todoappcompose.ui.screens.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todoappcompose.ui.navigation.NavArg
import com.example.todoappcompose.ui.navigation.NavRoutes
import com.example.todoappcompose.ui.navigation.TodoNavigationManager
import com.example.todoappcompose.ui.screens.add_edit_task.AddEditTaskScreen
import com.example.todoappcompose.ui.screens.all_tasks.AllTasksScreen
import com.example.todoappcompose.ui.screens.detail_task.DetailTaskScreen

@Composable
fun TodoAppScreen(
    navController: NavHostController = rememberNavController(),
    navigationManager: TodoNavigationManager = remember(navController) {
        TodoNavigationManager(navController)
    }
) {
    NavHost(navController = navController, startDestination = NavRoutes.ALL_TASKS_ROUTE) {
        this.composable(
            route = NavRoutes.ALL_TASKS_ROUTE,
            arguments = emptyList(),
            deepLinks = emptyList()
        ) {
            AllTasksScreen(
                onTaskClicked = { taskId ->
                    navigationManager.navigateToDetailScreen(taskId)
                },
                onAddTaskClicked = {
                    navigationManager.navigateToAddEditScreen()
                }
            )
        }

        this.composable(
            route = NavRoutes.DETAIL_TASK_ROUTE,
            arguments = listOf(
                navArgument(
                    name = NavArg.ARG_TASK_ID,
                ) {
                    this.type = NavType.StringType
                }
            ),
            deepLinks = emptyList()
        ) {
            DetailTaskScreen(
                onEditClicked = { taskId ->
                    navigationManager.navigateToAddEditScreen(taskId)
                },
                onBackPressed = {
                    navigationManager.navigateBack()
                }
            )
        }

        this.composable(
            route = NavRoutes.ADD_EDIT_TASK_ROUTE,
            arguments = listOf(
                navArgument(NavArg.ARG_TASK_ID) {
                    this.type = NavType.StringType
                    this.nullable = true
                    this.defaultValue = null
                }
            ),
            deepLinks = listOf()
        ) {
            AddEditTaskScreen(
                onBack = {
                    navigationManager.navigateBack()
                }
            )
        }

    }
}