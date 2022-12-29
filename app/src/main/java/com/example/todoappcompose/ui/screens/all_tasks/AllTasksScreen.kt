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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
    onTaskClicked: (taskId: String) -> Unit,
    onAddTaskClicked: () -> Unit,
    onDrawerClick: () -> Unit,
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
                onDrawerClick = onDrawerClick,
                filterOption = uiState.value.filterOptions
            )
        },
        floatingActionButton = {
            AddTaskFloatingActionButton(
                onAddToDoClick = onAddTaskClicked
            )
        }
    ) { paddingValues ->
        AllTasksContent(
            todoList = uiState.value.todos,
            filterOption = uiState.value.filterOptions,
            onCompleteChange = { task ->
                viewModel.onTaskIsDoneUndoneClick(task)
            },
            onTaskClicked = onTaskClicked,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun AllTasksContent(
    todoList: List<TaskEntity>,
    filterOption: FilterOptions,
    onCompleteChange: (task: TaskEntity) -> Unit,
    onTaskClicked: (taskId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (todoList.isNotEmpty()) {
        TasksContent(
            todoList,
            filterOption,
            onCompleteChange,
            onTaskClicked = onTaskClicked,
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
                FilterOptions.ALL -> stringResource(id = R.string.filter_option_all_no_tasks_message)
                FilterOptions.ACTIVE -> stringResource(id = R.string.filter_option_active_no_active_tasks_message)
                FilterOptions.COMPLETED -> stringResource(id = R.string.filter_option_completed_no_completed_tasks_message)
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
    onTaskClicked: (taskId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(
                text = when (filterOption) {
                    FilterOptions.ALL -> stringResource(id = R.string.filter_option_all_title)
                    FilterOptions.ACTIVE -> stringResource(id = R.string.filter_option_active_title)
                    FilterOptions.COMPLETED -> stringResource(id = R.string.filter_option_completed_title)
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
                    onTaskClicked = onTaskClicked
                )
            }
        }
    }
}

@Composable
fun TaskItem(
    task: TaskEntity,
    onCompleteChange: (task: TaskEntity) -> Unit,
    onTaskClicked: (taskId: String) -> Unit
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
            modifier = Modifier
                .padding(8.dp)
                .semantics {
                    contentDescription = task.taskId
                }
        )
        Text(text = task.taskName)
    }
}

@Composable
fun AllTasksAppBar(
    onFilterOptionClicked: (filterOption: FilterOptions) -> Unit,
    onClearCompletedTasks: () -> Unit,
    onDrawerClick: () -> Unit,
    filterOption: FilterOptions,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(text = "Todo")
        },
        navigationIcon = {
            IconButton(onClick = onDrawerClick) {
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
            Icon(Icons.Filled.MoreVert, stringResource(id = R.string.all_tasks_clear_completed_option))
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
            Text(text = stringResource(id = R.string.all_tasks_clear_completed_option_title))
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
            Icon(painterResource(id = R.drawable.ic_filter_list), stringResource(id = R.string.all_tasks_filter_option))
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
                text = stringResource(id = R.string.filter_option_all),
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
                text = stringResource(id = R.string.filter_option_active),
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
                text = stringResource(id = R.string.filter_option_completed),
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
        Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.common_add_new_task_description))
    }
}