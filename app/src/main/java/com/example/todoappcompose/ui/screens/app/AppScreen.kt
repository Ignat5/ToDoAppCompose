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
import com.example.todoappcompose.ui.screens.all_tasks.AllTasksScreen
import com.example.todoappcompose.ui.screens.detail_task.DetailTaskScreen

@Composable
fun TodoAppScreen(
    navController: NavHostController = rememberNavController(),
    navigationManager: TodoNavigationManager = remember(navController) {
        TodoNavigationManager(navController)
    }
) {
    NavHost(navController = navController, startDestination = NavRoutes.ALL_TASKS_DESTINATION) {
        this.composable(
            route = NavRoutes.ALL_TASKS_DESTINATION,
            arguments = emptyList(),
            deepLinks = emptyList()
        ) {
            AllTasksScreen(
                onTaskClicked = { taskId ->
                    navigationManager.navigateToDetailScreen(taskId)
                }
            )
        }

        this.composable(
            route = NavRoutes.DETAIL_TASK_DESTINATION,
            arguments = listOf(
                navArgument(
                    name = NavArg.ARG_TASK_ID,
                ) {
                    this.type = NavType.StringType
                }
            ),
            deepLinks = emptyList()
        ) {
            DetailTaskScreen()
        }

    }
}