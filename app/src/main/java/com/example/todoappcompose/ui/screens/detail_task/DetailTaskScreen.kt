package com.example.todoappcompose.ui.screens.detail_task

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DetailTaskScreen(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: DetailTaskViewModel = hiltViewModel()
) {

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {

        },
        floatingActionButton = {

        }
    ) {

    }

}

@Composable
fun DetailScreenTopAppBar() {
    TopAppBar(
        title = {
            Text(text = "Task details")
        },
        navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Filled.ArrowBack, "back")
            }
        },
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Filled.Delete, "delete")
            }
        }
    )
}
}