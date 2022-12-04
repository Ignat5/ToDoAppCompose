package com.example.todoappcompose.ui.screens.statistics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todoappcompose.R
import com.example.todoappcompose.util.Utils

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun StatisticsScreen(
    onDrawerClick: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            StatisticsTopAppBar(onDrawerClick)
        }
    ) { paddingValues ->
        StatisticsScreenContent(
            modifier = Modifier.padding(paddingValues),
            activeTasksStatistics = uiState.value.activeTasksStatistics,
            completedTasksStatistics = uiState.value.completedTasksStatistics
        )
    }
}

@Composable
fun StatisticsScreenContent(
    modifier: Modifier = Modifier,
    activeTasksStatistics: Float,
    completedTasksStatistics: Float,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            PercentText(statisticsName = "Active tasks", activeTasksStatistics)
            PercentText(statisticsName = "Completed tasks", completedTasksStatistics)
        }
    }
}

@Composable
fun PercentText(statisticsName: String, percent: Float) {
    Text(
        text = "$statisticsName: ${Utils.getRoundedPercent(percent, 2)}%",
        style = MaterialTheme.typography.h6,
        fontWeight = FontWeight.Medium
    )
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