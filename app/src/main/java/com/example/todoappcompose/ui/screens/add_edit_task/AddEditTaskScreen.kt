package com.example.todoappcompose.ui.screens.add_edit_task

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AddEditTaskScreen(
    onBack: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: AddEditTaskViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AddEditTopAppBar(
                onBack = onBack,
                isNewTask = uiState.value.currentTask == null
            )
        },
        floatingActionButton = {
            AddEditFloatingActionButton {
                viewModel.onAddEditTaskFinishedClick()
            }
        }
    ) { paddingValues ->
        LaunchedEffect(uiState.value.shouldNavigateBack) {
            if (uiState.value.shouldNavigateBack) {
                onBack()
            }
        }
        val userMessage = uiState.value.userMessage
        val message = if (userMessage != null) stringResource(id = userMessage) else null
        LaunchedEffect(userMessage) {
            if (message != null) {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = message
                )
                viewModel.onUserMessageShown()
            }
        }
        AddEditScreenContent(
            modifier = Modifier.padding(paddingValues),
            taskTitle = uiState.value.taskTitle,
            taskDescription = uiState.value.taskDescription,
            onTitleChange = {
                viewModel.onTaskTitleChanged(it)
            },
            onDescriptionChange = {
                viewModel.onTaskDescriptionChanged(it)
            }
        )
    }
}

@Composable
fun AddEditScreenContent(
    taskTitle: String,
    onTitleChange: (title: String) -> Unit,
    taskDescription: String,
    onDescriptionChange: (description: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 24.dp, horizontal = 24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        OutlinedTextField(
            value = taskTitle,
            onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Title")
            },
            maxLines = 2,
        )
        Spacer(modifier = Modifier.padding(vertical = 8.dp))
        OutlinedTextField(
            value = taskDescription,
            onValueChange = onDescriptionChange,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Description")
            },
            maxLines = 4
        )
    }
}

@Composable
fun AddEditFloatingActionButton(
    onAddEditClick: () -> Unit
) {
    FloatingActionButton(onClick = onAddEditClick) {
        Icon(Icons.Filled.Done, "done")
    }
}

@Composable
fun AddEditTopAppBar(
    onBack: () -> Unit,
    isNewTask: Boolean
) {
    TopAppBar(
        title = {
            Text(text = if (isNewTask) "New Task" else "Edit task")
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Filled.ArrowBack,
                    "back"
                )
            }
        }
    )
}