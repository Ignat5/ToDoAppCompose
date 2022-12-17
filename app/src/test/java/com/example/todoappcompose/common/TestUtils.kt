package com.example.todoappcompose.common

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToString
import androidx.test.ext.junit.rules.ActivityScenarioRule

object TestUtils {

    fun AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, ComponentActivity>.printSemanticTree(
        useUnmergedTree: Boolean = false
    ) {
        println(this.onRoot(useUnmergedTree).printToString())
    }

}