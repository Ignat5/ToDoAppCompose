package com.example.todoappcompose.ui.navigation

import androidx.navigation.NavController
import com.example.todoappcompose.ui.navigation.NavArg.ARG_TASK_ID
import com.example.todoappcompose.ui.navigation.NavRoutes.ALL_TASKS_ROUTE
import com.example.todoappcompose.ui.navigation.NavRoutes.STATISTICS_ROUTE
import com.example.todoappcompose.ui.navigation.NavScreens.ADD_EDIT_TASK_SCREEN_DESTINATION
import com.example.todoappcompose.ui.navigation.NavScreens.ALL_TASKS_SCREEN_DESTINATION
import com.example.todoappcompose.ui.navigation.NavScreens.DETAIL_TASK_SCREEN_DESTINATION
import com.example.todoappcompose.ui.navigation.NavScreens.STATISTICS_DESTINATION

//destinations
private object NavScreens {
    const val ALL_TASKS_SCREEN_DESTINATION = "all_tasks"
    const val DETAIL_TASK_SCREEN_DESTINATION = "detail_task"
    const val ADD_EDIT_TASK_SCREEN_DESTINATION = "add_edit_task"
    const val STATISTICS_DESTINATION = "statistics"
}

//arg names
object NavArg {
    const val ARG_TASK_ID = "task_id"
}

object NavRoutes {
    const val ALL_TASKS_ROUTE = "$ALL_TASKS_SCREEN_DESTINATION"
    const val DETAIL_TASK_ROUTE = "${DETAIL_TASK_SCREEN_DESTINATION}/{$ARG_TASK_ID}"
    const val ADD_EDIT_TASK_ROUTE = "$ADD_EDIT_TASK_SCREEN_DESTINATION?$ARG_TASK_ID={$ARG_TASK_ID}"
    const val STATISTICS_ROUTE = "$STATISTICS_DESTINATION"
}

class TodoNavigationManager(private val navController: NavController) {
    fun navigateToDetailScreen(taskId: String) {
        navController.navigate(
            route = "${DETAIL_TASK_SCREEN_DESTINATION}/$taskId"
        )
    }

    fun navigateToAddEditScreen(taskId: String? = null) {
        navController.navigate(
            route = "$ADD_EDIT_TASK_SCREEN_DESTINATION?$ARG_TASK_ID=$taskId"
        )
    }

    fun navigateFromDrawer(drawerDestination: DrawerDestinations) {
        navController.currentDestination?.route?.let { currentRoute ->
            val route = when (drawerDestination) {
                DrawerDestinations.ALL_TASKS -> ALL_TASKS_ROUTE
                DrawerDestinations.STATISTICS -> STATISTICS_ROUTE
            }
            navController.navigate(route) {
                this.launchSingleTop = true
                this.popUpTo(route = currentRoute) {
                    this.inclusive = true
                }
            }
        }
    }

    fun navigateBack() {
        navController.popBackStack()
    }
}

enum class DrawerDestinations {
    ALL_TASKS,
    STATISTICS
}