package com.example.todoappcompose.ui.other

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.todoappcompose.R
import com.example.todoappcompose.ui.navigation.DrawerDestinations
import com.example.todoappcompose.ui.navigation.NavRoutes

@Composable
fun ToDoAppDrawer(
    drawerState: DrawerState,
    currentDrawerDestination: DrawerDestinations?,
    onOptionSelected: (drawerDestination: DrawerDestinations) -> Unit,
    content: @Composable () -> Unit,
) {
    ModalDrawer(
        drawerContent = {
            ToDoAppDrawerContent(
                onOptionSelected = onOptionSelected,
                currentDrawerDestination = currentDrawerDestination
            )
        },
        drawerState = drawerState,
        gesturesEnabled = true
    ) {
        content()
    }
}

@Composable
fun ToDoAppDrawerContent(
    onOptionSelected: (drawerDestination: DrawerDestinations) -> Unit,
    currentDrawerDestination: DrawerDestinations?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_to_do_app),
                contentDescription = "icon",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(200.dp)
            )
            Text(
                text = "ToDo App",
                style = MaterialTheme.typography.h6.copy(color = Color.Black),
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
    Column(
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
    ) {
        DrawerOption(
            iconResource = R.drawable.ic_list,
            optionText = "Tasks",
            isCurrentOption = currentDrawerDestination == DrawerDestinations.ALL_TASKS,
            onOptionSelected = {
                onOptionSelected(DrawerDestinations.ALL_TASKS)
            }
        )
        DrawerOption(
            iconResource = R.drawable.ic_statistics,
            optionText = "Statistics",
            isCurrentOption = currentDrawerDestination == DrawerDestinations.STATISTICS,
            onOptionSelected = {
                onOptionSelected(DrawerDestinations.STATISTICS)
            }
        )
    }
}

@Composable
fun DrawerOption(
    modifier: Modifier = Modifier,
    @DrawableRes iconResource: Int,
    optionText: String,
    isCurrentOption: Boolean,
    iconDescription: String = "",
    onOptionSelected: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onOptionSelected)
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            painter = painterResource(id = iconResource),
            contentDescription = iconDescription,
            tint = if (isCurrentOption) MaterialTheme.colors.primary else Color.Black.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
        Text(
            text = optionText,
            color = if (isCurrentOption) MaterialTheme.colors.primary else Color.Black.copy(alpha = 0.7f)
        )
    }
}