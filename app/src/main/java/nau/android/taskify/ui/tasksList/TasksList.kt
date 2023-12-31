package nau.android.taskify.ui.tasksList

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch
import nau.android.taskify.FloatingActionButton
import nau.android.taskify.LocalSnackbarHost
import nau.android.taskify.R
import nau.android.taskify.TaskItem
import nau.android.taskify.TaskItemInMultiSelection
import nau.android.taskify.ui.DateInfo
import nau.android.taskify.ui.alarm.permission.AlarmPermission
import nau.android.taskify.ui.alarm.permission.GetGrantedNotificationPermissionState
import nau.android.taskify.ui.category.CategoriesViewModel
import nau.android.taskify.ui.customElements.DialogArguments
import nau.android.taskify.ui.customElements.NoTasks
import nau.android.taskify.ui.customElements.SelectionModeTopAppBar
import nau.android.taskify.ui.customElements.TaskifyArrowBack
import nau.android.taskify.ui.customElements.TaskifyDialog
import nau.android.taskify.ui.customElements.TaskifyMenuIcon
import nau.android.taskify.ui.customElements.TaskifyTasksListMenuDropdown
import nau.android.taskify.ui.dialogs.TaskifyDatePickerDialog
import nau.android.taskify.ui.eisenhowerMatrix.EisenhowerMatrixQuadrant
import nau.android.taskify.ui.model.Category
import nau.android.taskify.ui.model.Task
import nau.android.taskify.ui.model.TaskWithCategory
import nau.android.taskify.ui.searchBars.TaskifySearchBar
import nau.android.taskify.ui.task.NoRippleInteractionSource


val LocalTasksList = compositionLocalOf {
    TasksListParameters()
}

val LocalContentWindowInsets = compositionLocalOf {
    WindowInsets(bottom = 0)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Tasks(
    tasksMap: Map<HeaderType, List<Task>>,
    onCompleteTask: (Task) -> Unit,
    deleteTask: (Task) -> Unit,
    navigateToTaskDetails: (Long) -> Unit
) {
    val tasksListState = LocalTasksList.current

    val selectedTasks = tasksListState.selectedTasks.toHashSet()

    tasksMap.forEach { map ->

        Column(modifier = Modifier.padding(13.dp)) {
            if (map.key != HeaderType.NoHeader) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.align(Alignment.CenterStart),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (tasksListState.inMultiSelection) {
                            val tasks = map.value.map { it }
                            val selected = tasksListState.selectedTasks.containsAll(tasks)

                            CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                                Checkbox(
                                    checked = selected,
                                    onCheckedChange = {
                                        if (selected) {
                                            selectedTasks.addAll(tasks)
                                        } else {
                                            selectedTasks.removeAll(tasks)
                                        }
                                        tasksListState.selectedTasks = selectedTasks
                                    },
                                    interactionSource = NoRippleInteractionSource(),
                                    modifier = Modifier.padding(start = 15.dp, end = 15.dp)
                                )
                            }
                        }
                        Text(
                            text = map.key.title, style = MaterialTheme.typography.titleSmall
                        )
                    }
                    Row(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "${map.value.size}",
                            style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.outline)
                        )

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Arrow down",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
            LazyColumn(content = {
                items(map.value) { task ->
                    if (tasksListState.inMultiSelection) {
                        TaskItemInMultiSelection(selected = selectedTasks.contains(task),
                            task = task,
                            showDetails = tasksListState.showDetails,
                            onSelectChange = { selected ->
                                if (selected) {
                                    selectedTasks.add(task)
                                } else {
                                    selectedTasks.remove(task)
                                }
                                tasksListState.selectedTasks = selectedTasks
                            })
                    } else {
                        TaskItem(
                            task, showDetails = tasksListState.showDetails, onComplete = {
                                onCompleteTask(task)
                            }, deleteTask = deleteTask
                        ) { taskId ->
                            navigateToTaskDetails(taskId)
                        }
                    }
                }
            }, verticalArrangement = Arrangement.spacedBy(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TasksWithCategories(
    currentTasks: Map<HeaderType, List<TaskWithCategory>>,
    //completedTasks: List<TaskWithCategory>,
    navigateToTaskDetails: (Long) -> Unit,
    onCompleteTask: (Task) -> Unit,
    deleteTask: (Task) -> Unit
) {

    val tasksListState = LocalTasksList.current

    val selectedTasks = tasksListState.selectedTasks.toHashSet()


    currentTasks.forEach { map ->

        Column(modifier = Modifier.padding(13.dp)) {
            if (map.key != HeaderType.NoHeader) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.align(Alignment.CenterStart),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (tasksListState.inMultiSelection) {
                            val tasks = map.value.map { it.task }
                            val selected = tasksListState.selectedTasks.containsAll(tasks)

                            CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                                Checkbox(
                                    checked = selected,
                                    onCheckedChange = {
                                        if (selected) {
                                            selectedTasks.removeAll(tasks)
                                        } else {
                                            selectedTasks.addAll(tasks)
                                        }
                                        tasksListState.selectedTasks = selectedTasks
                                    },
                                    interactionSource = NoRippleInteractionSource(),
                                    modifier = Modifier.padding(start = 15.dp, end = 15.dp)
                                )
                            }
                        }
                        Text(
                            text = map.key.title, style = MaterialTheme.typography.titleSmall
                        )
                    }
                    Row(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "${map.value.size}",
                            style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.outline)
                        )

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Arrow down",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
            LazyColumn(content = {
                items(map.value, key = {
                    it.task.id
                }) { taskWithCategory ->
                    if (tasksListState.inMultiSelection) {
                        TaskItemInMultiSelection(selected = selectedTasks.contains(
                            taskWithCategory.task
                        ),
                            task = taskWithCategory.task,
                            category = taskWithCategory.category,
                            showDetails = tasksListState.showDetails,
                            onSelectChange = { selected ->
                                if (selected) {
                                    selectedTasks.add(taskWithCategory.task)
                                } else {
                                    selectedTasks.remove(taskWithCategory.task)
                                }
                                tasksListState.selectedTasks = selectedTasks
                            })
                    } else {
                        TaskItem(
                            task = taskWithCategory.task,
                            category = taskWithCategory.category,
                            showDetails = tasksListState.showDetails,
                            onComplete = {
                                onCompleteTask(taskWithCategory.task)
                            },
                            deleteTask = deleteTask
                        ) { taskId ->
                            navigateToTaskDetails(taskId)
                        }
                    }
                }
            }, verticalArrangement = Arrangement.spacedBy(20.dp))
        }
    }

}

@Composable
fun ExactAlarmPermissionDialog(
    context: Context, isDialogOpen: Boolean, onCloseDialog: () -> Unit
) {
    val arguments =
        DialogArguments(title = stringResource(id = R.string.task_alarm_permission_dialog_title),
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
            })
    TaskifyDialog(
        arguments = arguments, isDialogOpen = isDialogOpen, onDismissRequest = onCloseDialog
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationPermissionDialog(
    permissionState: PermissionState, isDialogOpen: Boolean, onCloseDialog: () -> Unit
) {
    val arguments =
        DialogArguments(title = stringResource(id = R.string.task_notification_permission_dialog_title),
            text = stringResource(id = R.string.task_notification_permission_dialog_text),
            confirmText = stringResource(id = R.string.task_notification_permission_dialog_confirm),
            dismissText = stringResource(id = R.string.task_notification_permission_dialog_cancel),
            onConfirmAction = {
                permissionState.launchPermissionRequest()
                onCloseDialog()
            })
    TaskifyDialog(
        arguments = arguments, isDialogOpen = isDialogOpen, onDismissRequest = onCloseDialog
    )
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
                    listState.selectedTasks = emptySet()
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

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TasksListCommon(
    title: String,
    tasksListViewModel: TaskListViewModel,
    alarmPermission: AlarmPermission,
    shouldIncludeNavigation: Boolean,
    navigateUp: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {

    val tasksListState = LocalTasksList.current

    val dateForNewTask = remember {
        mutableStateOf(DateInfo())
    }

    var showDataPickerDialog by remember {
        mutableStateOf(false)
    }
    var newTask by remember {
        mutableStateOf<Task?>(null)
    }

    var taskCategory by remember {
        mutableStateOf<Category?>(null)
    }

    val notificationPermissionState =
        if (alarmPermission.shouldCheckNotificationPermission()) rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS) else GetGrantedNotificationPermissionState.getGrantedPermissionState(
            Manifest.permission.POST_NOTIFICATIONS
        )

    val showExactAlarmDialog = remember {
        mutableStateOf(false)
    }

    val showNotificationDialog = remember {
        mutableStateOf(false)
    }

    val showRationalePermissionDialog = remember {
        mutableStateOf(false)
    }

    val createTaskBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (tasksListState.showCreateTaskBottomSheet) {
        CreateTaskBottomSheet(
            dateForNewTask.value,
            newTask ?: Task(name = ""),
            sheetState = createTaskBottomSheetState,
            onDismissBottomSheet = {
                newTask = null
                dateForNewTask.value = DateInfo()
                tasksListState.showCreateTaskBottomSheet = false
            },
            openDatePickerDialog = { task, category, date ->
                if (alarmPermission.hasExactAlarmPermission() && notificationPermissionState.status.isGranted) {
                    newTask = task
                    taskCategory = category
                    dateForNewTask.value = date
                    showDataPickerDialog = true
                    tasksListState.showCreateTaskBottomSheet = false
                } else if (notificationPermissionState.status.shouldShowRationale) {
                    showRationalePermissionDialog.value = true
                } else {
                    showNotificationDialog.value = !notificationPermissionState.status.isGranted
                    showExactAlarmDialog.value = !alarmPermission.hasExactAlarmPermission()
                }
            },
            createTask = { task ->
                newTask = null
                dateForNewTask.value = DateInfo()
                tasksListViewModel.createTask(task)
                tasksListState.showCreateTaskBottomSheet = false
            },
            category = taskCategory
        )
    }

    if (tasksListState.showSortTasksBottomSheet) {
        SortBottomSheet(groupingType = tasksListState.groupingType,
            sortingType = tasksListState.sortingType,
            groupingTypeChanged = { newGroupingType ->
                tasksListState.groupingType = newGroupingType
            },
            sortingTypeChanged = { newSortingType ->
                tasksListState.sortingType = newSortingType
            }) {
            tasksListState.showSortTasksBottomSheet = false
        }
    }

    if (showDataPickerDialog) {
        TaskifyDatePickerDialog(dateForNewTask.value, onDismiss = {
            showDataPickerDialog = false
            tasksListState.showCreateTaskBottomSheet = true
        }, onDateChanged = { dateInfo ->
            dateForNewTask.value = dateInfo
            showDataPickerDialog = false
            tasksListState.showCreateTaskBottomSheet = true
        })
    }

    ExactAlarmPermissionDialog(
        context = LocalContext.current, isDialogOpen = showExactAlarmDialog.value
    ) {
        showExactAlarmDialog.value = false
    }

    NotificationPermissionDialog(
        permissionState = notificationPermissionState, isDialogOpen = showNotificationDialog.value
    ) {
        showNotificationDialog.value = false
    }
    Scaffold(topBar = {
        TasksListTopAppBar(title = title, shouldIncludeNavigation, navigateUp)
    }, floatingActionButton = {
        if (!tasksListState.inMultiSelection) {
            FloatingActionButton {
                tasksListState.showCreateTaskBottomSheet = true
            }
        }
    }, bottomBar = {
        MultiSelectionBottomAppBar(deleteSelectedTasks = {
            tasksListViewModel.putTasksOnDeletion(tasksListState.selectedTasks.toList())
        }, markAllSelectedTasksAsDone = {

        })
    }, contentWindowInsets = LocalContentWindowInsets.current) { innerPaddings ->
        content(innerPaddings)
    }
}

@Composable
fun QuadrantTasksList(
    quadrantId: Int?,
    alarmPermission: AlarmPermission,
    tasksViewModel: TaskListViewModel = hiltViewModel(),
    navigateToTaskDetails: (Long) -> Unit,
    navigateUp: () -> Unit
) {

    val tasksListState = remember {
        TasksListParameters()
    }

    val snackbarState = LocalSnackbarHost.current

    val coroutineScope = rememberCoroutineScope()

    val groupingType = tasksListState.groupingType

    val sortingType = tasksListState.sortingType

    quadrantId ?: return

    val quadrant = EisenhowerMatrixQuadrant.getMatrixQuadrantById(quadrantId)

    LaunchedEffect(key1 = groupingType, key2 = sortingType) {
        tasksViewModel.getEisenhowerQuadrantTasks(groupingType, sortingType, quadrant)
    }

    val tasks =
        tasksViewModel.tasksWithCategoriesState.collectAsStateWithLifecycle(initialValue = TasksListState.Loading)

    CompositionLocalProvider(
        LocalContentWindowInsets provides WindowInsets.navigationBars.add(
            WindowInsets(bottom = 20.dp)
        )
    ) {

        CompositionLocalProvider(LocalTasksList provides tasksListState) {

            TasksListCommon(
                title = stringResource(id = quadrant.titleR),
                tasksListViewModel = tasksViewModel,
                alarmPermission = alarmPermission,
                shouldIncludeNavigation = true,
                navigateUp = navigateUp
            ) { paddingValues ->
                Column(
                    modifier = Modifier.padding(paddingValues)
                ) {

                    TaskifySearchBar(onValueChange = {

                    })

                    when (val result = tasks.value) {
                        is TasksListState.Success -> {
                            TasksWithCategories(
                                currentTasks = result.tasks,
                                onCompleteTask = { task ->
                                    tasksViewModel.completeTask(task)
                                },
                                navigateToTaskDetails = navigateToTaskDetails,
                                deleteTask = {
                                    tasksViewModel.putTaskOnDeletion(it)
                                    coroutineScope.launch {

                                        val snackbarResult = snackbarState.showSnackbar(
                                            "You have deleted ${it.name}",
                                            "Undo",
                                            duration = SnackbarDuration.Short
                                        )
                                        when (snackbarResult) {
                                            SnackbarResult.ActionPerformed -> tasksViewModel.undoTasksDeletion(
                                                groupingType,
                                                sortingType
                                            )

                                            SnackbarResult.Dismissed -> tasksViewModel.deleteTask()
                                        }

                                    }
                                })
                        }

                        is TasksListState.Empty -> {
                            NoTasks(message = stringResource(id = R.string.completed_all_tasks_message))
                        }

                        is TasksListState.Error -> {

                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryTasksList(
    categoryId: Long?,
    alarmPermission: AlarmPermission,
    tasksViewModel: TaskListViewModel = hiltViewModel(),
    categoryViewModel: CategoriesViewModel = hiltViewModel(),
    navigateUp: () -> Unit,
    navigateToTaskDetails: (Long) -> Unit
) {

    val tasksListParameters = remember {
        TasksListParameters()
    }

    val snackbarState = LocalSnackbarHost.current

    categoryId ?: return

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(categoryId) {
        categoryViewModel.getCategoryById(categoryId)
    }

    LaunchedEffect(
        key1 = categoryId,
        key2 = tasksListParameters.groupingType,
        key3 = tasksListParameters.sortingType
    ) {
        tasksViewModel.getCategoryTasks(
            categoryId,
            tasksListParameters.groupingType,
            tasksListParameters.sortingType
        )
    }

    val categoryTasksState = tasksViewModel.categoryTasksState.collectAsStateWithLifecycle()

    val category = categoryViewModel.categoryLiveData.observeAsState()

    CompositionLocalProvider(
        LocalContentWindowInsets provides WindowInsets.navigationBars.add(
            WindowInsets(bottom = 20.dp)
        )
    ) {
        CompositionLocalProvider(LocalTasksList provides tasksListParameters) {


            TasksListCommon(
                title = category.value?.name ?: "",
                tasksListViewModel = tasksViewModel,
                alarmPermission = alarmPermission,
                shouldIncludeNavigation = true,
                navigateUp = navigateUp
            ) { paddingValues ->
                Column(modifier = Modifier.padding(paddingValues)) {

                    TaskifySearchBar(onValueChange = {

                    })

                    when (val result = categoryTasksState.value) {
                        is CategoryTasksListState.Success -> {
                            Tasks(tasksMap = result.tasks, onCompleteTask = { task ->
                                tasksViewModel.completeTask(task)
                            }, navigateToTaskDetails = navigateToTaskDetails, deleteTask = {
                                tasksViewModel.putTaskOnDeletion(it, categoryId)
                                coroutineScope.launch {
                                    val snackbarResult = snackbarState.showSnackbar(
                                        "You have deleted ${it.name}",
                                        actionLabel = "Undo",
                                        duration = SnackbarDuration.Short
                                    )
                                    when (snackbarResult) {
                                        SnackbarResult.ActionPerformed -> tasksViewModel.undoCategoryTasksDeletion(
                                            categoryId,
                                            tasksListParameters.groupingType,
                                            tasksListParameters.sortingType
                                        )

                                        SnackbarResult.Dismissed -> tasksViewModel.deleteTask()
                                    }
                                }
                            })
                        }

                        is CategoryTasksListState.Error -> {

                        }

                        is CategoryTasksListState.Loading -> {

                        }

                        is CategoryTasksListState.Empty -> NoTasks(message = stringResource(id = R.string.completed_all_tasks_message))

                    }
                }
            }
        }
    }
}

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
fun AllTasksList(
    title: String,
    alarmPermission: AlarmPermission,
    tasksViewModel: TaskListViewModel = hiltViewModel(),
    onMainBottomBarVisibilityChanged: (Boolean) -> Unit,
    navigateToTaskDetails: (Long) -> Unit,
    navigateUp: () -> Unit
) {

    val localState = remember {
        TasksListParameters()
    }

    val snackbarHostState = LocalSnackbarHost.current

    val coroutineScope = rememberCoroutineScope()

    val inMultiSelection = localState.inMultiSelection

    LaunchedEffect(key1 = localState.groupingType, key2 = localState.sortingType) {
        tasksViewModel.getAllTasks(
            localState.groupingType, localState.sortingType
        )
    }

    val tasksState = tasksViewModel.tasksWithCategoriesState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = inMultiSelection) {
        onMainBottomBarVisibilityChanged(inMultiSelection)
    }

    CompositionLocalProvider(LocalContentWindowInsets provides WindowInsets(bottom = 0.dp)) {

        CompositionLocalProvider(LocalTasksList provides localState) {

            TasksListCommon(
                title = title,
                tasksListViewModel = tasksViewModel,
                alarmPermission = alarmPermission,
                shouldIncludeNavigation = false,
                navigateUp = navigateUp
            ) { paddingValues ->
                Column(modifier = Modifier.padding(paddingValues)) {

                    TaskifySearchBar(onValueChange = {

                    })

                    when (val result = tasksState.value) {
                        is TasksListState.Success -> {
                            TasksWithCategories(
                                currentTasks = result.tasks,
                                onCompleteTask = { task ->
                                    tasksViewModel.completeTask(task)
                                },
                                navigateToTaskDetails = navigateToTaskDetails,
                                deleteTask = {
                                    tasksViewModel.putTaskOnDeletion(it)
                                    coroutineScope.launch {
                                        val snacbarResult = snackbarHostState.showSnackbar(
                                            "You have deleted ${it.name}",
                                            actionLabel = "Undo",
                                            duration = SnackbarDuration.Short
                                        )
                                        when (snacbarResult) {
                                            SnackbarResult.Dismissed -> tasksViewModel.deleteTask()
                                            SnackbarResult.ActionPerformed -> tasksViewModel.undoTasksDeletion(
                                                localState.groupingType,
                                                localState.sortingType
                                            )
                                        }

                                    }
                                })
                        }

                        is TasksListState.Empty -> {
                            NoTasks(stringResource(id = R.string.completed_all_tasks_message))
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}