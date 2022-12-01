package com.example.todoappcompose.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todoappcompose.util.AppConstants
import java.util.UUID


@Entity(tableName = AppConstants.TASKS_TABLE)
data class TaskEntity (
    @PrimaryKey
    @ColumnInfo(name = AppConstants.TASK_ID)
    var taskId: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = AppConstants.TASK_NAME)
    val taskName: String,
    @ColumnInfo(name = AppConstants.TASK_DESCRIPTION)
    val taskDescription: String,
    @ColumnInfo(name = AppConstants.TASK_IS_DONE)
    val isDone: Boolean
)
