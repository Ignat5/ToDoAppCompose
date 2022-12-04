package com.example.todoappcompose.ui.navigation

import androidx.navigation.NavController
import com.example.todoappcompose.ui.navigation.NavArg.ARG_TASK_ID
import com.example.todoappcompose.ui.navigation.NavScreens.ALL_TASKS_SCREEN_DESTINATION
import com.example.todoappcompose.ui.navigation.NavScreens.DETAIL_TASK_SCREEN_DESTINATION

//destinations
private object NavScreens {
    const val ALL_TASKS_SCREEN_DESTINATION = "all_tasks"
    const val DETAIL_TASK_SCREEN_DESTINATION = "detail_task"
}
//arg names
object NavArg {
    const val ARG_TASK_ID = "task_id"
}

object NavRoutes {
    const val ALL_TASKS_DESTINATION = "$ALL_TASKS_SCREEN_DESTINATION"
    const val DETAIL_TASK_DESTINATION = "${DETAIL_TASK_SCREEN_DESTINATION}/{$ARG_TASK_ID}"
}

class TodoNavigationManager(private val navController: NavController) {
    fun navigateToDetailScreen(taskId: String) {
        navController.navigate(
            route = "${DETAIL_TASK_SCREEN_DESTINATION}/$taskId"
        )
    }

    fun navigateBack() {
        navController.popBackStack()
    }
}