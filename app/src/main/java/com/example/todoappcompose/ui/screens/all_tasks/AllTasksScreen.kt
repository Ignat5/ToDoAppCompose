package com.example.todoappcompose.ui.screens.all_tasks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoappcompose.R
import com.example.todoappcompose.data.db.entities.TaskEntity
import java.util.logging.Filter

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun AllTasksScreen(
    viewModel: AllTasksViewModel = hiltViewModel<AllTasksViewModel>()
) {
    Scaffold(
        scaffoldState = rememberScaffoldState(),
        topBar = {
            AllTasksAppBar()
        },
        floatingActionButton = {
            AddTaskFloatingActionButton({
                viewModel.addTestTask()
            })
        }
    ) { paddingValues ->
        val uiState = viewModel.uiState.collectAsStateWithLifecycle()
        AllTasksContent(
            todoList = uiState.value.todos,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

data class TestItem(
    val id: Int,
    val title: String,
    val isCompleted: Boolean
)

@Composable
fun AllTasksContent(todoList: List<TaskEntity>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(
                text = "All tasks",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(start = 16.dp)
            )
            Divider()
        }
        LazyColumn() {
            items(todoList) { item ->
                TaskItem(
                    id = item.taskId,
                    title = item.taskName,
                    isCompleted = item.isDone,
                    onCompleteChange = { id, isChecked ->

                    },
                    onTaskClicked = { id ->

                    }
                )
            }
        }
    }
}

@Composable
fun TaskItem(
    id: String,
    title: String,
    isCompleted: Boolean,
    onCompleteChange: (id: String, isChecked: Boolean) -> Unit,
    onTaskClicked: (id: String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onTaskClicked(id)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isCompleted,
            onCheckedChange = { isChecked ->
                onCompleteChange(id, isChecked)
            },
            modifier = Modifier.padding(8.dp)
        )
        Text(text = title)
    }
}

@Composable
fun AllTasksAppBar(modifier: Modifier = Modifier) {
    TopAppBar(
        title = {
            Text(text = "Todo")
        },
        navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "menu")
            }
        },
        actions = {
            FilterMenuItem()
            MoreMenuItem()
        },
        modifier = Modifier
    )
}

@Composable
fun MoreMenuItem(
    onClearCompletedTasks: () -> Unit = {}
) {
    var isMenuExpanded by remember {
        mutableStateOf(false)
    }
    TopAppBarMenuItem(
        topBarIcon = {
            Icon(Icons.Filled.MoreVert, "more")
        },
        onShowMenu = {
            isMenuExpanded = true
        },
        onHideMenu = {
            isMenuExpanded = false
        },
        isMenuExpanded = isMenuExpanded
    ) {
        DropdownMenuItem(onClick = {
            onClearCompletedTasks()
            isMenuExpanded = false
        }) {
            Text(text = "Clear completed")
        }
    }
}

@Composable
fun FilterMenuItem(
    onFilterAllTasks: () -> Unit = {},
    onFilterActive: () -> Unit = {},
    onFilterCompleted: () -> Unit = {},
) {
    var isMenuExpanded by remember {
        mutableStateOf(false)
    }
    TopAppBarMenuItem(
        topBarIcon = {
            Icon(painterResource(id = R.drawable.ic_filter_list), "filter")
        },
        onShowMenu = {
            isMenuExpanded = true
        },
        onHideMenu = {
            isMenuExpanded = false
        },
        isMenuExpanded = isMenuExpanded
    ) {
        DropdownMenuItem(onClick = {
            onFilterAllTasks()
            isMenuExpanded = false
        }) {
            Text(text = "All")
        }
        DropdownMenuItem(onClick = {
            onFilterActive()
            isMenuExpanded = false
        }) {
            Text(text = "Active")
        }
        DropdownMenuItem(onClick = {
            onFilterCompleted()
            isMenuExpanded = false
        }) {
            Text(text = "Completed")
        }
    }
}

@Composable
fun TopAppBarMenuItem(
    topBarIcon: @Composable () -> Unit,
    isMenuExpanded: Boolean,
    onShowMenu: () -> Unit,
    onHideMenu: () -> Unit,
    menuContent: @Composable ColumnScope.() -> Unit,
) {
    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        IconButton(onClick = { onShowMenu() }) {
            topBarIcon()
        }
        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { onHideMenu() },
            modifier = Modifier.wrapContentSize(Alignment.TopEnd)
        ) {
            menuContent()
        }
    }
}

@Composable
fun AddTaskFloatingActionButton(onAddToDoClick: () -> Unit) {
    FloatingActionButton(
        onClick = onAddToDoClick,
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
    }
}