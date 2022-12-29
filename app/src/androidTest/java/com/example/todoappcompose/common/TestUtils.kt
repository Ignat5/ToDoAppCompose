package com.example.todoappcompose.common

import android.app.Activity
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToString
import androidx.test.ext.junit.rules.ActivityScenarioRule

object TestUtils {

    fun<T: ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<T>, T>.printSemanticTree(
        useUnmergedTree: Boolean = false
    ) {
        Log.d("myTag", this.onRoot(useUnmergedTree).printToString())
    }

}