package com.example.todoappcompose.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todoappcompose.data.db.entities.TaskEntity
import com.example.todoappcompose.util.AppConstants
import kotlinx.coroutines.flow.Flow

@Dao
interface TasksDao {

    @Query("SELECT * FROM ${AppConstants.TASKS_TABLE}")
    fun getTasksFlow(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM ${AppConstants.TASKS_TABLE} WHERE ${AppConstants.TASK_ID} = :taskId")
    suspend fun getTaskById(taskId: Int): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTask(task: TaskEntity)

    @Query("DELETE FROM ${AppConstants.TASKS_TABLE} WHERE ${AppConstants.TASK_IS_DONE} = 1")
    suspend fun deleteCompletedTasks()

}