package nau.android.taskify.ui.tasksList

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import nau.android.taskify.FloatingActionButton
import nau.android.taskify.R
import nau.android.taskify.TaskItem
import nau.android.taskify.ui.DateInfo
import nau.android.taskify.ui.MainDestination
import nau.android.taskify.ui.alarm.permission.AlarmPermission
import nau.android.taskify.ui.alarm.permission.GetGrantedNotificationPermissionState
import nau.android.taskify.ui.customElements.DialogArguments
import nau.android.taskify.ui.customElements.TaskifyDialog
import nau.android.taskify.ui.customElements.TaskifyMenuDropDown
import nau.android.taskify.ui.customElements.TaskifyMenuIcon
import nau.android.taskify.ui.dialogs.TaskifyDatePickerDialog
import nau.android.taskify.ui.model.Task
import nau.android.taskify.ui.model.TaskWithCategory
import nau.android.taskify.ui.searchBars.TaskifySearchBar

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun ListOfTasks(
    section: MainDestination,
    tasksViewModel: TaskListViewModel = hiltViewModel(),
    alarmPermission: AlarmPermission,
    navigateToTaskDetails: (Long) -> Unit
) {

    var displayMenu by remember {
        mutableStateOf(false)
    }

    var showDetails by remember {
        mutableStateOf(false)
    }

    var showCompleted by remember {
        mutableStateOf(false)
    }

    val createTaskBottomSheet = remember {
        mutableStateOf(false)
    }

    var showBottomSheet by remember {
        mutableStateOf(false)
    }

    var groupingType by remember {
        mutableStateOf(GroupingType.None)
    }

    var sortingType by remember {
        mutableStateOf(SortingType.Title)
    }

    var showDatePickerDialog by remember {
        mutableStateOf(false)
    }

    var dateForNewTask = remember {
        DateInfo()
    }

    var newTask = remember<Task?> {
        null
    }

    val notificationPermissionState =
        if (alarmPermission.shouldCheckNotificationPermission()) rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS) else
            GetGrantedNotificationPermissionState.getGrantedPermissionState(Manifest.permission.POST_NOTIFICATIONS)


    val showExactAlarmDialog = remember {
        mutableStateOf(false)
    }

    val showNotificationDialog = remember {
        mutableStateOf(false)
    }

    val showRationalePermissionDialog = remember {
        mutableStateOf(false)
    }

    val tasks = tasksViewModel.getAllTasks(groupingType, sortingType)
        .collectAsStateWithLifecycle(initialValue = TasksListState.Loading)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = section.title, style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    TaskifyMenuIcon(displayMenu = displayMenu, onDisplayMenuChanged = {
                        displayMenu = it
                    })
                    TaskifyMenuDropDown(
                        displayMenu = displayMenu,
                        showDetails = showDetails,
                        showCompleted = showCompleted,
                        onShowDetailsChanged = {
                            showDetails = it
                        },
                        onShowCompletedChanged = {
                            showCompleted = it
                        },
                        displayMenuChanged = {
                            displayMenu = it
                        },
                        onShowSortBottomSheet = {
                            displayMenu = false
                            showBottomSheet = true
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton {
                createTaskBottomSheet.value = true
            }
        },
        contentWindowInsets = WindowInsets(bottom = 0)
    ) { innerPaddings ->


        if (createTaskBottomSheet.value) {
            CreateTaskBottomSheet(
                dateForNewTask,
                newTask ?: Task(name = ""),
                onDismissBottomSheet = {
                    newTask = null
                    createTaskBottomSheet.value = false
                },
                openDatePickerDialog = { task, date ->
                    if (alarmPermission.hasExactAlarmPermission() && notificationPermissionState.status.isGranted) {
                        newTask = task
                        dateForNewTask = date
                        showDatePickerDialog = true
                        createTaskBottomSheet.value = false
                    } else if (notificationPermissionState.status.shouldShowRationale) {
                        showRationalePermissionDialog.value = true
                    } else {
                        showNotificationDialog.value = !notificationPermissionState.status.isGranted
                        showExactAlarmDialog.value = !alarmPermission.hasExactAlarmPermission()
                    }
                },
                createTask = { task ->
                    newTask = null
                    dateForNewTask = DateInfo()
                    tasksViewModel.createTask(task)
                    createTaskBottomSheet.value = false
                })
        }


        if (showBottomSheet) {
            SortBottomSheet(groupingType = groupingType,
                sortingType = sortingType,
                groupingTypeChanged = { newGroupingType ->
                    groupingType = newGroupingType
                },
                sortingTypeChanged = { newSortingType ->
                    sortingType = newSortingType
                }) {
                showBottomSheet = false
            }
        }

        if (showDatePickerDialog) {
            TaskifyDatePickerDialog(dateForNewTask, onDismiss = {
                showDatePickerDialog = false
                createTaskBottomSheet.value = true
            }, onDateChanged = { dateInfo ->
                dateForNewTask = dateInfo
                showDatePickerDialog = false
                createTaskBottomSheet.value = true
            })
        }

        ExactAlarmPermissionDialog(
            context = LocalContext.current,
            isDialogOpen = showExactAlarmDialog.value
        ) {
            showExactAlarmDialog.value = false
        }

        NotificationPermissionDialog(
            permissionState = notificationPermissionState,
            isDialogOpen = showNotificationDialog.value
        ) {
            showNotificationDialog.value = false
        }


        Column(modifier = Modifier.padding(innerPaddings)) {

            TaskifySearchBar(onValueChange = {

            })

            when (val result = tasks.value) {
                is TasksListState.Loading -> {

                }

                is TasksListState.Empty -> {

                }

                is TasksListState.Error -> {

                }

                is TasksListState.Success -> {
                    BuildList(
                        tasks = result.tasks,
                        showDetails = showDetails,
                        navigateToTaskDetails = {
                            navigateToTaskDetails(it)
                        })
                }
            }

        }
    }
    // Text(text = route)
}

@Composable
private fun BuildList(
    tasks: Map<HeaderType, List<TaskWithCategory>>,
    showDetails: Boolean,
    navigateToTaskDetails: (Long) -> Unit
) {

    /*

  val sortedHeaders = when (groupingType) {
      GroupingType.Priority -> {
          sortedMap.entries.sortedBy {
              (it.key as HeaderType.Priority).priorityNumber
          }.associate {
              it.toPair()
          }
      }

      GroupingType.Category -> {
          sortedMap.entries.sortedBy {
              it.key.title
          }.associate {
              it.toPair()
          }
      }

      GroupingType.Date -> {
          sortedMap.entries.sortedBy {
              (it.key as HeaderType.Date).date
          }.associate {
              it.toPair()
          }
      }

      GroupingType.None -> {
          sortedMap
      }
  }

   */

    tasks.forEach { map ->

        Column(modifier = Modifier.padding(13.dp)) {
            if (map.key != HeaderType.NoHeader) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = map.key.title, modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Text(
                        text = "${map.value.size}", modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
            LazyColumn(content = {
                items(map.value) { task ->
                    TaskItem(task, showDetails = showDetails) { taskId ->
                        navigateToTaskDetails(taskId)
                    }
                }
            }, verticalArrangement = Arrangement.spacedBy(20.dp))
        }
    }
}

@Composable
fun ExactAlarmPermissionDialog(
    context: Context,
    isDialogOpen: Boolean,
    onCloseDialog: () -> Unit
) {
    val arguments = DialogArguments(
        title = stringResource(id = R.string.task_alarm_permission_dialog_title),
        text = stringResource(id = R.string.task_alarm_permission_dialog_text),
        confirmText = stringResource(id = R.string.task_alarm_permission_dialog_confirm),
        dismissText = stringResource(id = R.string.task_alarm_permission_dialog_cancel),
        onConfirmAction = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val intent = Intent().apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                }
                context.startActivity(intent)
                onCloseDialog()
            }
        }
    )
    TaskifyDialog(
        arguments = arguments,
        isDialogOpen = isDialogOpen,
        onDismissRequest = onCloseDialog
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationPermissionDialog(
    permissionState: PermissionState,
    isDialogOpen: Boolean,
    onCloseDialog: () -> Unit
) {
    val arguments = DialogArguments(
        title = stringResource(id = R.string.task_notification_permission_dialog_title),
        text = stringResource(id = R.string.task_notification_permission_dialog_text),
        confirmText = stringResource(id = R.string.task_notification_permission_dialog_confirm),
        dismissText = stringResource(id = R.string.task_notification_permission_dialog_cancel),
        onConfirmAction = {
            permissionState.launchPermissionRequest()
            onCloseDialog()
        }
    )
    TaskifyDialog(
        arguments = arguments,
        isDialogOpen = isDialogOpen,
        onDismissRequest = onCloseDialog
    )
}