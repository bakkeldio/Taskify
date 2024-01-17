package nau.android.taskify.ui.tasksList

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.*
import kotlinx.coroutines.launch
import nau.android.taskify.*
import nau.android.taskify.R
import nau.android.taskify.ui.permissions.ExactAlarmPermissionDialog
import nau.android.taskify.ui.permissions.NotificationPermissionDialog
import nau.android.taskify.ui.model.DateInfo
import nau.android.taskify.ui.alarm.permission.AlarmPermission
import nau.android.taskify.ui.alarm.permission.GetGrantedNotificationPermissionState
import nau.android.taskify.ui.category.viewModel.CategoriesViewModel
import nau.android.taskify.ui.category.ChangeCategoryBottomSheet
import nau.android.taskify.ui.customElements.*
import nau.android.taskify.ui.dialogs.TaskifyDatePickerDialog
import nau.android.taskify.ui.eisenhowerMatrix.EisenhowerMatrixQuadrant
import nau.android.taskify.ui.extensions.keyboardAsState
import nau.android.taskify.ui.model.Category
import nau.android.taskify.ui.model.Task
import nau.android.taskify.ui.model.TaskWithCategory
import nau.android.taskify.ui.searchBars.TaskifySearchBar
import nau.android.taskify.ui.task.NoRippleInteractionSource
import nau.android.taskify.ui.tasksList.viewModel.TaskListViewModel


val LocalTasksList = compositionLocalOf {
    TasksListParameters()
}

val LocalContentWindowInsets = compositionLocalOf {
    WindowInsets(bottom = 0)
}


/**
 * Composable which is responsible for displaying all tasks of the user
 * @param title for this list
 * @param alarmPermission Alarm Permission interface to work with permissions
 * @param tasksViewModel ViewModel to be used in this composable
 * @param onMainBottomBarVisibilityChanged lambda expression that is called when list in multi selection mode
 * @param navigateToTaskDetails labmda expression that is called when user navigates to task details
 * @param navigateUp lambda expression that is called when user presses back button
 *
 */
@Composable
fun AllTasksList(
    title: String,
    alarmPermission: AlarmPermission,
    tasksViewModel: TaskListViewModel = hiltViewModel(),
    onMainBottomBarVisibilityChanged: (Boolean) -> Unit,
    navigateToTaskDetails: (Long) -> Unit,
    navigateUp: () -> Unit
) {

    val focusManager = LocalFocusManager.current

    val snackbarHostState = LocalSnackbarHost.current

    val localState = remember {
        TasksListParameters()
    }

    val inMultiSelection = localState.inMultiSelection

    var searchQuery by remember {
        mutableStateOf("")
    }

    val keyboardState = keyboardAsState()

    if (!keyboardState.value) {
        focusManager.clearFocus()
    }

    val coroutineScope = rememberCoroutineScope()


    /*
       LaunchEffect is used for launching the tasks at the beginning and every time if grouping type or
       sorting type is changing
     */

    LaunchedEffect(
        key1 = localState.groupingType,
        key2 = localState.sortingType
    ) {
        tasksViewModel.launchAllTasks(
            searchQuery,
            localState.groupingType, localState.sortingType
        )
    }

    LaunchedEffect(key1 = localState.showCompleted) {
        if (localState.showCompleted) {
            tasksViewModel.getCompletedTasks()
        }
    }

    val tasksState = tasksViewModel.tasksWithCategoriesState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = inMultiSelection) {
        onMainBottomBarVisibilityChanged(inMultiSelection)
    }
    
    val completedTasks = tasksViewModel.completedTasks.collectAsStateWithLifecycle()

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

                    TaskifySearchBar(value = searchQuery, onValueChange = {
                        searchQuery = it

                        tasksViewModel.searchThroughAllTasks(
                            searchQuery,
                            localState.groupingType,
                            localState.sortingType
                        )
                    })

                    when (val result = tasksState.value) {
                        is TasksListState.Success -> {
                            TasksWithCategories(
                                currentTasks = { result.tasks },
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
                categoryId = categoryId,
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
        tasksViewModel.getEisenhowerQuadrantTasks(
            groupingType = groupingType,
            sortingType = sortingType,
            eisenhowerMatrixQuadrant = quadrant
        )
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
                                currentTasks = { result.tasks },
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
                                }
                            )
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

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TasksListCommon(
    title: String,
    tasksListViewModel: TaskListViewModel,
    alarmPermission: AlarmPermission,
    shouldIncludeNavigation: Boolean,
    categoryId: Long? = null,
    navigateUp: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {

    val tasksListState = LocalTasksList.current

    val snackBarState = LocalSnackbarHost.current

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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

    val coroutineScope = rememberCoroutineScope()

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

    if (tasksListState.showCategoriesBottomSheet) {
        ChangeCategoryBottomSheet(onCategoryChanged = {
            tasksListViewModel.moveTasks(tasksListState.selectedTasks.toList(), it.id)
            tasksListState.selectedTasks = emptySet()
            coroutineScope.launch {
                sheetState.hide()
            }.invokeOnCompletion {
                tasksListState.showCategoriesBottomSheet = false
            }
        }, sheetState = sheetState) {
            tasksListState.showCategoriesBottomSheet = false
        }
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
            tasksListViewModel.putTasksOnDeletion(tasksListState.selectedTasks.toList(), categoryId)

            coroutineScope.launch {
                val snackbarResult = snackBarState.showSnackbar(
                    "You have deleted ${tasksListState.selectedTasks.size}",
                    "Undo",
                    duration = SnackbarDuration.Short
                )
                when (snackbarResult) {

                    SnackbarResult.ActionPerformed -> tasksListViewModel.undoTasksDeletion(
                        tasksListState.groupingType,
                        tasksListState.sortingType
                    )

                    SnackbarResult.Dismissed -> tasksListViewModel.deleteTasks(tasksListState.selectedTasks)
                }
                tasksListState.selectedTasks = emptySet()
            }
        }, markAllSelectedTasksAsDone = {
            tasksListViewModel.completeTasks(tasksListState.selectedTasks.toList())
        })
    }, contentWindowInsets = LocalContentWindowInsets.current) { innerPaddings ->
        content(innerPaddings)
    }
}

@Composable
private fun Tasks(
    tasksMap: Map<HeaderType, List<Task>>,
    onCompleteTask: (Task) -> Unit,
    deleteTask: (Task) -> Unit,
    navigateToTaskDetails: (Long) -> Unit
) {
    val tasksListState = LocalTasksList.current

    val selectedTasks = tasksListState.selectedTasks.toHashSet()


    LazyColumn(content = {

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }

        tasksMap.forEach { map ->

            item {
                if (map.key != HeaderType.NoHeader) {
                    TaskHeader(
                        inMultiSelection = tasksListState.inMultiSelection,
                        headerTitle = map.key.title,
                        tasks = map.value,
                        selectedTasks = selectedTasks
                    ) {
                        tasksListState.selectedTasks = it
                    }
                }
            }
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
                Spacer(modifier = Modifier.height(20.dp))
            }

        }
    })
}

@Composable
private fun TasksWithCategories(
    currentTasks: () -> Map<HeaderType, List<TaskWithCategory>>,
    navigateToTaskDetails: (Long) -> Unit,
    onCompleteTask: (Task) -> Unit,
    deleteTask: (Task) -> Unit
) {

    val tasksListState = LocalTasksList.current

    val selectedTasks = tasksListState.selectedTasks.toHashSet()

    LazyColumn(content = {

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }

        currentTasks().forEach { map ->

            val hasHeader = map.key != HeaderType.NoHeader

            item {
                if (hasHeader) {
                    TaskHeader(
                        inMultiSelection = tasksListState.inMultiSelection,
                        headerTitle = map.key.title,
                        tasks = map.value.map {
                            it.task
                        },
                        selectedTasks = selectedTasks
                    ) {
                        tasksListState.selectedTasks = it
                    }
                }
            }

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
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    })

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskHeader(
    inMultiSelection: Boolean,
    headerTitle: String,
    tasks: List<Task>,
    selectedTasks: HashSet<Task>,
    updateSelected: (HashSet<Task>) -> Unit
) {

    Box(
        modifier = Modifier
            .padding(start = 13.dp, end = 13.dp, bottom = 10.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.align(Alignment.CenterStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (inMultiSelection) {

                val selected = selectedTasks.containsAll(tasks)

                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    Checkbox(
                        checked = selected,
                        onCheckedChange = {
                            if (selected) {
                                selectedTasks.removeAll(tasks.toSet())
                            } else {
                                selectedTasks.addAll(tasks)
                            }
                            updateSelected(selectedTasks)
                        },
                        interactionSource = NoRippleInteractionSource(),
                        modifier = Modifier.padding(start = 15.dp, end = 15.dp)
                    )
                }
            }
            Text(
                text = headerTitle, style = MaterialTheme.typography.titleSmall
            )
        }
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "${tasks.size}",
                style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.outline)
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Arrow down",
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
}