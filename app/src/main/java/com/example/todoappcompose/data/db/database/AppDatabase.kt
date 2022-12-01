package com.example.todoappcompose.data.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todoappcompose.data.db.dao.TasksDao
import com.example.todoappcompose.data.db.entities.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = 1
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun tasksDao(): TasksDao
}