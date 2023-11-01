package nau.android.taskify.ui.tasksList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import nau.android.taskify.FloatingActionButton
import nau.android.taskify.R
import nau.android.taskify.TaskItem
import nau.android.taskify.data.FakeTasksData
import nau.android.taskify.data.generateTasks
import nau.android.taskify.ui.MainDestination
import nau.android.taskify.ui.task.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListOfTasks(section: MainDestination, navigateToTaskDetails: (String) -> Unit) {

    var displayMenu by remember {
        mutableStateOf(false)
    }

    var showDetails by remember {
        mutableStateOf(false)
    }

    var showCompleted by remember {
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

    val fakeTasksData = FakeTasksData(generateTasks())

    if (showBottomSheet) {
        SortBottomSheet(groupingType = groupingType,
            sortingType = sortingType,
            fakeTasksData = fakeTasksData,
            groupingTypeChanged = { newGroupingType ->
                groupingType = newGroupingType
            },
            sortingTypeChanged = { newSortingType ->
                sortingType = newSortingType
            }) {
            showBottomSheet = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = section.title, style = MaterialTheme.typography.titleLarge
                    )

                },
                actions = {
                    Icon(imageVector = Icons.Default.Menu,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 13.dp)
                            .size(24.dp)
                            .clickable {
                                displayMenu = !displayMenu
                            })

                    DropdownMenu(
                        expanded = displayMenu,
                        onDismissRequest = { displayMenu = false }) {
                        DropdownMenuItem(text = { Text(text = "Show details") }, onClick = {
                            showDetails = !showDetails
                        }, trailingIcon = {
                            Checkbox(checked = showDetails, onCheckedChange = {})
                        })

                        DropdownMenuItem(text = { Text(text = "Show completed") },
                            onClick = { showCompleted = !showCompleted },
                            trailingIcon = {
                                Checkbox(
                                    checked = showCompleted,
                                    onCheckedChange = {},
                                    modifier = Modifier.padding(0.dp)
                                )
                            })

                        DropdownMenuItem(text = { Text(text = "Sort") }, onClick = {
                            displayMenu = false
                            showBottomSheet = true
                        }, leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_sort),
                                contentDescription = null
                            )
                        })

                        DropdownMenuItem(text = { Text(text = "Select") },
                            onClick = { /*TODO*/ },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_selection),
                                    contentDescription = null
                                )
                            })


                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = { FloatingActionButton() },
        contentWindowInsets = WindowInsets(bottom = 0)
    ) { innerPaddings ->
        Column(modifier = Modifier.padding(innerPaddings)) {
            TextField(
                value = "",
                onValueChange = {},
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search, contentDescription = null
                    )
                },
                placeholder = { Text(text = "Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 13.dp),
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )

            )
            GroupTasks(
                fakeTasksData,
                groupingType = groupingType,
                sortingType = sortingType,
                showDetails = showDetails,
                navigateToTaskDetails
            )
        }
    }
    // Text(text = route)
}

@Composable
private fun GroupTasks(
    fakeTasksData: FakeTasksData,
    groupingType: GroupingType,
    sortingType: SortingType,
    showDetails: Boolean,
    navigateToTaskDetails: (String) -> Unit
) {

    val groupedTasks = fakeTasksData.getTasksInGroups(groupingType)

    val sortedMap = groupedTasks.mapValues { map ->

        when (sortingType) {
            SortingType.Date -> map.value.sortedBy {
                it.taskDate
            }

            SortingType.Priority -> map.value.sortedBy {
                it.taskPriority.priorityNumber
            }

            SortingType.Title -> map.value.sortedBy {
                it.name
            }
        }

    }

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


    BuildList(sortedHeaders, showDetails, navigateToTaskDetails)

}

@Composable
private fun BuildList(
    list: Map<HeaderType, List<Task>>, showDetails: Boolean, navigateToTaskDetails: (String) -> Unit
) {

    list.forEach { map ->

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
                    TaskItem(task, showDetails = showDetails, navigateToTaskDetails)
                }
            }, verticalArrangement = Arrangement.spacedBy(20.dp))
        }

    }
}