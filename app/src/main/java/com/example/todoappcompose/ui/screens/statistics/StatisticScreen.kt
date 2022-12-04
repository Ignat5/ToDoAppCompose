package com.example.todoappcompose.ui.screens.statistics

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.todoappcompose.R

@Composable
fun StatisticsScreen(
    onDrawerClick: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            StatisticsTopAppBar(onDrawerClick)
        }
    ) { paddingValues ->
        StatisticsScreenContent(
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun StatisticsScreenContent(modifier: Modifier = Modifier) {

}

@Composable
fun StatisticsTopAppBar(
    onDrawerClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = "Statistics")
        },
        navigationIcon = {
            IconButton(onClick = onDrawerClick) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "menu")
            }
        }
    )
}