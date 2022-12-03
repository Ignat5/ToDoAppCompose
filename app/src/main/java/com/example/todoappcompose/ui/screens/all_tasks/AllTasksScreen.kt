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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoappcompose.R
import com.example.todoappcompose.data.db.entities.TaskEntity
import com.example.todoappcompose.data.repositories.local.FilterOptions

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun AllTasksScreen(
    viewModel: AllTasksViewModel = hiltViewModel<AllTasksViewModel>()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        scaffoldState = rememberScaffoldState(),
        topBar = {
            AllTasksAppBar(
                onFilterOptionClicked = { filterOption ->
                    viewModel.onFilterOptionClick(filterOption)
                },
                onClearCompletedTasks = {
                    viewModel.onClearCompletedTasksClick()
                },
                filterOption = uiState.value.filterOptions
            )
        },
        floatingActionButton = {
            AddTaskFloatingActionButton({
                viewModel.addTestTask()
            })
        }
    ) { paddingValues ->
        AllTasksContent(
            todoList = uiState.value.todos,
            filterOption = uiState.value.filterOptions,
            onCompleteChange = { task ->
                viewModel.onTaskIsDoneUndoneClick(task)
            },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun AllTasksContent(
    todoList: List<TaskEntity>,
    filterOption: FilterOptions,
    onCompleteChange: (task: TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (todoList.isNotEmpty()) {
        TasksContent(
            todoList,
            filterOption,
            onCompleteChange,
            modifier
        )
    } else {
        NoTasksContent(filterOption = filterOption)
    }
}

@Composable
fun NoTasksContent(
    filterOption: FilterOptions,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when (filterOption) {
                FilterOptions.ALL -> "You have no tasks!"
                FilterOptions.ACTIVE -> "You have no active tasks!"
                FilterOptions.COMPLETED -> "You have no completed tasks!"
            },
            style = MaterialTheme.typography.h5
        )
    }
}

@Composable
fun TasksContent(
    todoList: List<TaskEntity>,
    filterOption: FilterOptions,
    onCompleteChange: (task: TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(
                text = when (filterOption) {
                    FilterOptions.ALL -> "All tasks"
                    FilterOptions.ACTIVE -> "Active tasks"
                    FilterOptions.COMPLETED -> "Completed tasks"
                },
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(start = 16.dp)
            )
            Divider()
        }
        LazyColumn(

        ) {
            items(todoList, key = {task -> task.taskId}) { item ->
                TaskItem(
                    item,
                    onCompleteChange = onCompleteChange,
                    onTaskClicked = { id ->

                    }
                )
            }
        }
    }
}

@Composable
fun TaskItem(
    task: TaskEntity,
    onCompleteChange: (task: TaskEntity) -> Unit,
    onTaskClicked: (id: String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onTaskClicked(task.taskId)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isDone,
            onCheckedChange = {
                onCompleteChange(task)
            },
            modifier = Modifier.padding(8.dp)
        )
        Text(text = task.taskName)
    }
}

@Composable
fun AllTasksAppBar(
    onFilterOptionClicked: (filterOption: FilterOptions) -> Unit,
    onClearCompletedTasks: () -> Unit,
    filterOption: FilterOptions,
    modifier: Modifier = Modifier
) {
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
            FilterMenuItem(
                onFilterOptionClicked = onFilterOptionClicked,
                filterOption = filterOption
            )
            MoreMenuItem(
                onClearCompletedTasks = onClearCompletedTasks
            )
        },
        modifier = modifier
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

private fun getMenuItemAlphaByFilter(
    currentFilterOptions: FilterOptions,
    itemFilterOptions: FilterOptions
): Float =
    if (currentFilterOptions == itemFilterOptions) 1.0f else 0.5f


@Composable
fun FilterMenuItem(
    filterOption: FilterOptions,
    onFilterOptionClicked: (filterOption: FilterOptions) -> Unit
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
        isMenuExpanded = isMenuExpanded,
    ) {
        DropdownMenuItem(
            onClick = {
                onFilterOptionClicked(FilterOptions.ALL)
                isMenuExpanded = false
            },
        ) {
            Text(
                text = "All",
                modifier = Modifier.alpha(
                    getMenuItemAlphaByFilter(
                        currentFilterOptions = filterOption,
                        itemFilterOptions = FilterOptions.ALL
                    )
                )
            )
        }
        DropdownMenuItem(
            onClick = {
                onFilterOptionClicked(FilterOptions.ACTIVE)
                isMenuExpanded = false
            }
        ) {
            Text(
                text = "Active",
                modifier = Modifier.alpha(
                    getMenuItemAlphaByFilter(
                        currentFilterOptions = filterOption,
                        itemFilterOptions = FilterOptions.ACTIVE
                    )
                )
            )
        }
        DropdownMenuItem(
            onClick = {
                onFilterOptionClicked(FilterOptions.COMPLETED)
                isMenuExpanded = false
            }
        ) {
            Text(
                text = "Completed",
                modifier = Modifier.alpha(
                    getMenuItemAlphaByFilter(
                        currentFilterOptions = filterOption,
                        itemFilterOptions = FilterOptions.COMPLETED
                    )
                )
            )
        }
    }
}

@Composable
fun TopAppBarMenuItem(
    topBarIcon: @Composable () -> Unit,
    isMenuExpanded: Boolean,
    onShowMenu: () -> Unit,
    onHideMenu: () -> Unit,
    modifier: Modifier = Modifier,
    menuContent: @Composable ColumnScope.() -> Unit,
) {
    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        IconButton(onClick = { onShowMenu() }) {
            topBarIcon()
        }
        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { onHideMenu() },
            modifier = modifier
                .wrapContentSize(Alignment.TopEnd)
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