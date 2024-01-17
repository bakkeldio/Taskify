package nau.android.taskify.ui.tasksList

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import nau.android.taskify.R
import nau.android.taskify.ui.customElements.SelectionModeTopAppBar
import nau.android.taskify.ui.customElements.TaskifyArrowBack
import nau.android.taskify.ui.customElements.TaskifyMenuIcon
import nau.android.taskify.ui.customElements.TaskifyTasksListMenuDropdown
import nau.android.taskify.ui.model.Task
import nau.android.taskify.ui.task.NoRippleInteractionSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksListTopAppBar(
    title: String, shouldIncludeNavigation: Boolean = false, navigateUp: () -> Unit
) {
    val tasksListState = LocalTasksList.current
    if (tasksListState.inMultiSelection) {
        SelectionModeTopAppBar(selectedTasks = tasksListState.selectedTasks.size) {
            tasksListState.inMultiSelection = false
            tasksListState.selectedTasks = emptySet()
        }
    } else {

        TopAppBar(
            title = {
                Text(
                    text = title, style = MaterialTheme.typography.titleLarge
                )
            },
            navigationIcon = {
                if (shouldIncludeNavigation) {
                    TaskifyArrowBack {
                        navigateUp()
                    }
                }
            },
            actions = {
                TaskifyMenuIcon(displayMenu = tasksListState.displayMenu, onDisplayMenuChanged = {
                    tasksListState.displayMenu = it
                })
                TaskifyTasksListMenuDropdown()
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )
    }
}

@Composable
fun MultiSelectionBottomAppBar(
    deleteSelectedTasks: () -> Unit, markAllSelectedTasksAsDone: () -> Unit
) {
    val listState = LocalTasksList.current
    AnimatedVisibility(visible = listState.inMultiSelection) {
        BottomAppBar {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                IconButton(onClick = {
                    deleteSelectedTasks()
                    listState.inMultiSelection = false
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "Delete tasks"
                    )
                }

                IconButton(onClick = {
                    listState.inMultiSelection = false
                    listState.showCategoriesBottomSheet = true
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_move_task),
                        contentDescription = "Move tasks"
                    )
                }

                IconButton(onClick = {
                    markAllSelectedTasksAsDone()
                    listState.inMultiSelection = false
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_complete),
                        contentDescription = "complete tasks"
                    )
                }
            }
        }
    }
}