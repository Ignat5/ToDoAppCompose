package com.example.todoappcompose.ui.screens.app

import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todoappcompose.ui.navigation.DrawerDestinations
import com.example.todoappcompose.ui.navigation.NavArg
import com.example.todoappcompose.ui.navigation.NavRoutes
import com.example.todoappcompose.ui.navigation.TodoNavigationManager
import com.example.todoappcompose.ui.other.ToDoAppDrawer
import com.example.todoappcompose.ui.screens.add_edit_task.AddEditTaskScreen
import com.example.todoappcompose.ui.screens.all_tasks.AllTasksScreen
import com.example.todoappcompose.ui.screens.detail_task.DetailTaskScreen
import com.example.todoappcompose.ui.screens.statistics.StatisticsScreen
import kotlinx.coroutines.launch

@Composable
fun TodoAppScreen(
    navController: NavHostController = rememberNavController(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    navigationManager: TodoNavigationManager = remember(navController) {
        TodoNavigationManager(navController)
    }
) {
    val coroutineScope = rememberCoroutineScope()
    NavHost(navController = navController, startDestination = NavRoutes.ALL_TASKS_ROUTE) {
        this.composable(
            route = NavRoutes.ALL_TASKS_ROUTE,
            arguments = emptyList(),
            deepLinks = emptyList()
        ) {
            ToDoAppDrawer(
                drawerState = drawerState,
                currentDrawerDestination = getCurrentDrawerDestination(navController),
                onOptionSelected = { drawerDestination ->
                    coroutineScope.launch {
                        drawerState.close()
                        if (drawerDestination != DrawerDestinations.ALL_TASKS)
                            navigationManager.navigateFromDrawer(drawerDestination)
                    }
                }
            ) {
                AllTasksScreen(
                    onTaskClicked = { taskId ->
                        navigationManager.navigateToDetailScreen(taskId)
                    },
                    onAddTaskClicked = {
                        navigationManager.navigateToAddEditScreen()
                    },
                    onDrawerClick = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    }
                )
            }
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

        this.composable(
            route = NavRoutes.STATISTICS_ROUTE,
            arguments = listOf(),
            deepLinks = listOf()
        ) {
            ToDoAppDrawer(drawerState = drawerState,
                currentDrawerDestination = getCurrentDrawerDestination(navController),
                onOptionSelected = { drawerDestination ->
                    coroutineScope.launch {
                        drawerState.close()
                        if (drawerDestination != DrawerDestinations.STATISTICS)
                            navigationManager.navigateFromDrawer(drawerDestination)
                    }
                }) {
                StatisticsScreen(
                    onDrawerClick = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    }
                )
            }
        }

    }
}

private fun getCurrentDrawerDestination(navController: NavController): DrawerDestinations? =
    when (navController.currentDestination?.route) {
        NavRoutes.ALL_TASKS_ROUTE -> DrawerDestinations.ALL_TASKS
        NavRoutes.STATISTICS_ROUTE -> DrawerDestinations.STATISTICS
        else -> null
    }