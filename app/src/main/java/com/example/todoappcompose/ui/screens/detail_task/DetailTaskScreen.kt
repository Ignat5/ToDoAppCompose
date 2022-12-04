package com.example.todoappcompose.ui.screens.detail_task

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoappcompose.data.db.entities.TaskEntity

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun DetailTaskScreen(
    onBackPressed: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: DetailTaskViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            DetailScreenTopAppBar(
                onDeleteTaskClick = {
                    viewModel.onDeleteTaskClick()
                },
                onBackPressed = onBackPressed
            )
        },
        floatingActionButton = {

        }
    ) { paddingValues ->
        LaunchedEffect(uiState.value.shouldNavigateBack) {
            if (uiState.value.shouldNavigateBack) {
                onBackPressed()
            }
        }
        uiState.value.currentTask?.let { task ->
            if (uiState.value.showDeleteDialog) {
                ConfirmDeletionDialog(
                    onDismissDialog = {
                        viewModel.onHideDeleteDialog()
                    },
                    onDeleteConfirmed = {
                        viewModel.onConfirmTaskDeletion()
                    }
                )
            }
            DetailScreenContent(
                task = task,
                onCheckUncheckClick = {
                    viewModel.onTaskDoneUndoneClick()
                },
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun ConfirmDeletionDialog(
    onDismissDialog: () -> Unit,
    onDeleteConfirmed: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissDialog,
        title = {
            Text(text = "Delete task")
        },
        text = {
            Text(text = "Are you sure you want to delete this task?")
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(onClick = onDeleteConfirmed) {
                    Text(
                        text = "Yes",
                        modifier = Modifier
                    )
                }
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                Button(onClick = onDismissDialog) {
                    Text(text = "No")
                }
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            }
        }
    )
}

@Composable
fun DetailScreenTopAppBar(
    onDeleteTaskClick: () -> Unit,
    onBackPressed: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = "Task details")
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Filled.ArrowBack, "back")
            }
        },
        actions = {
            IconButton(onClick = onDeleteTaskClick) {
                Icon(Icons.Filled.Delete, "delete")
            }
        }
    )
}

@Composable
fun DetailScreenContent(
    task: TaskEntity,
    onCheckUncheckClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Checkbox(checked = task.isDone, onCheckedChange = { onCheckUncheckClick() })
        Column {
            Text(text = task.taskName)
            Text(text = task.taskDescription)
        }
    }
}