package com.example.todoappcompose.ui.screens.all_tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoappcompose.data.db.entities.TaskEntity
import com.example.todoappcompose.data.repositories.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllTasksViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<AllTasksScreenState> = tasksRepository.getAllTasksFlow().mapLatest {
        AllTasksScreenState(it)
    }
        .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AllTasksScreenState(emptyList())
    )

    fun addTestTask() {
        val task = TaskEntity(
            taskName = "task + ${System.currentTimeMillis().toString()}",
            taskDescription = "task descr",
            isDone = true
        )
        viewModelScope.launch {
            tasksRepository.insertTask(task)
        }
    }

//    val uiState: StateFlow<AllTasksScreenState> = flow<AllTasksScreenState> {
//        emit(
//            AllTasksScreenState(
//                listOf(
//                    TestItem(
//                        id = 1,
//                        title = "title1",
//                        isCompleted = false
//                    ),
//                    TestItem(
//                        id = 2,
//                        title = "title2",
//                        isCompleted = false
//                    ),
//                    TestItem(
//                        id = 3,
//                        title = "title3",
//                        isCompleted = true
//                    ),
//                    TestItem(
//                        id = 4,
//                        title = "title4",
//                        isCompleted = true
//                    )
//                )
//            )
//        )
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5_000),
//        initialValue = AllTasksScreenState(emptyList())
//    )

}

data class AllTasksScreenState(
    val todos: List<TaskEntity>
)