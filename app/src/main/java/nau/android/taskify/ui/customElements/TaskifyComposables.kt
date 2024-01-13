package nau.android.taskify.ui.customElements

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import nau.android.taskify.R
import nau.android.taskify.ui.enums.Priority
import nau.android.taskify.ui.extensions.noRippleClickable
import nau.android.taskify.ui.tasksList.LocalTasksList


@Composable
fun TaskifyCreateTaskButton(isTaskTitleEmpty: Boolean, onCreate: () -> Unit) {

    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .wrapContentSize()
            .background(
                if (isTaskTitleEmpty) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                RoundedCornerShape(20.dp)
            )
            .noRippleClickable {
                if (!isTaskTitleEmpty) {
                    onCreate()
                }
            }

    ) {
        Icon(
            imageVector = Icons.Default.Send,
            "Create",
            tint = if (isTaskTitleEmpty) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp, start = 13.dp, end = 13.dp)

        )
    }
}

@Composable
fun TaskifyTextField(
    modifier: Modifier = Modifier,
    value: String,
    placeHolder: String = "",
    maxLines: Int = 2,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    onValueChange: (String) -> Unit,
) {
    TextField(value = value,
        onValueChange = { newValue ->
            onValueChange(newValue)
        },
        placeholder = {
            if (placeHolder.isNotEmpty()) {
                Text(text = placeHolder)
            }
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent
        ),
        maxLines = maxLines,
        modifier = modifier,
        textStyle = textStyle
    )
}

@Composable
fun TaskifyPrioritySelectionDropdownMenu(
    modifier: Modifier = Modifier,
    taskPriority: Priority,
    dropDownMenuOpen: Boolean,
    showRadioButtons: Boolean = true,
    changeTaskPriority: (Priority) -> Unit,
    closeDropDown: () -> Unit
) {
    DropdownMenu(
        expanded = dropDownMenuOpen,
        onDismissRequest = {
            closeDropDown()
        },
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        properties = PopupProperties(focusable = false)
    ) {
        Priority.values().forEach { priority ->
            DropdownMenuItem(text = {
                Text(text = priority.title)
            }, onClick = {
                changeTaskPriority(priority)
            }, trailingIcon = {
                if (showRadioButtons) {
                    RadioButton(selected = taskPriority == priority, onClick = {
                        changeTaskPriority(priority)
                    })
                }
            }, leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_flag),
                    contentDescription = "Priority",
                    tint = priority.color
                )
            })
        }
    }
}

@Composable
fun TaskifyTasksListMenuDropdown() {

    val taskListState = LocalTasksList.current

    DropdownMenu(
        expanded = taskListState.displayMenu,
        onDismissRequest = { taskListState.displayMenu = false },
        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
    ) {
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.show_detail)) },
            onClick = {
                taskListState.showDetails = !taskListState.showDetails
                taskListState.displayMenu = false
            },
            trailingIcon = {
                Checkbox(checked = taskListState.showDetails, onCheckedChange = null)
            })


        DropdownMenuItem(text = { Text(text = stringResource(id = R.string.show_completed)) },
            onClick = {
                taskListState.showCompleted = !taskListState.showCompleted
                taskListState.displayMenu = false
            },
            trailingIcon = {
                Checkbox(
                    checked = taskListState.showCompleted,
                    onCheckedChange = null,
                    modifier = Modifier.padding(0.dp)
                )
            })

        DropdownMenuItem(text = { Text(text = stringResource(id = R.string.sort)) }, onClick = {
            taskListState.showSortTasksBottomSheet = true
            taskListState.displayMenu = false
        }, leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_sort),
                contentDescription = null
            )
        })

        DropdownMenuItem(text = { Text(text = stringResource(id = R.string.select)) },
            onClick = {
                taskListState.inMultiSelection = true
                taskListState.displayMenu = false
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_selection),
                    contentDescription = null
                )
            })


    }
}

@Composable
fun TaskifyMenuIcon(displayMenu: Boolean, onDisplayMenuChanged: (Boolean) -> Unit) {
    Icon(imageVector = Icons.Default.Menu,
        contentDescription = null,
        modifier = Modifier
            .padding(end = 13.dp)
            .size(24.dp)
            .clickable {
                onDisplayMenuChanged(!displayMenu)
            })

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionModeTopAppBar(
    categoryName: String = "All",
    selectedTasks: Int,
    hideMultiSelection: () -> Unit
) {

    CenterAlignedTopAppBar(
        title = {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = categoryName)
                Text(text = "$selectedTasks Selected", style = MaterialTheme.typography.bodySmall)
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                hideMultiSelection()
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_cross),
                    contentDescription = "Exit from multi-selection"
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

@Composable
fun TaskifyCustomCheckBox(
    checked: Boolean,
    size: Int = 13,
    borderColor: Color,
    onCheckChanged: (Boolean) -> Unit
) {

    var checkedState by remember {
        mutableStateOf(checked)
    }

    Box(
        modifier = Modifier
            .size(size = size.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(
                if (checkedState) MaterialTheme.colorScheme.outline else Color.Transparent,
                RoundedCornerShape(3.dp)
            )
            .border(
                width = 1.dp,
                if (checkedState) MaterialTheme.colorScheme.outline else borderColor,
                RoundedCornerShape(3.dp)
            )
            .noRippleClickable {
                checkedState = !checkedState
                onCheckChanged(checkedState)
            }, contentAlignment = Alignment.Center
    ) {
        if (checkedState) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.padding(1.dp)
            )
        }
    }
}

@Composable
fun TaskifyArrowBack(onClick: () -> Unit) {
    IconButton(onClick = {
        onClick()
    }) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
    }
}

@Composable
fun LoginEmail(email: String, placeHolder: String, onEmailChange: (String) -> Unit) {

    TextField(
        value = email,
        onValueChange = onEmailChange,
        modifier = Modifier.fillMaxWidth(0.8f),
        placeholder = {
            Text(text = placeHolder)
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(5.dp),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = stringResource(id = R.string.email_icon)
            )
        },
        maxLines = 1
    )
}

@Composable
fun LoginPassword(password: String, placeHolder: String, onPasswordChange: (String) -> Unit) {
    TextField(
        value = password,
        onValueChange = onPasswordChange,
        modifier = Modifier.fillMaxWidth(0.8f),
        placeholder = {
            Text(text = placeHolder)
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(5.dp),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = stringResource(id = R.string.password)
            )
        },
        maxLines = 1
    )
}

@Composable
fun TaskifyLoginErrorMessage(message: String) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error),
        modifier = Modifier
            .padding(top = 10.dp)
            .fillMaxWidth(0.8f)
    )
}

@Composable
fun NoTasks(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.no_tasks_placeholder),
                modifier = Modifier
                    .background(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                    .size(200.dp),
                contentDescription = stringResource(id = R.string.no_tasks_on_the_day_message)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.titleSmall.copy(color = Color.Black)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(id = R.string.ready_for_new_tasks),
                style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.outline)
            )
        }
    }
}


@Composable
fun NoTasksForEisenhowerMatrix() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(id = R.string.no_tasks_for_matrix_quadrant),
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.outline),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun NoCategories() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(id = R.string.no_categories),
            style = MaterialTheme.typography.titleSmall.copy(
                color = MaterialTheme.colorScheme.outline
            )
        )
    }
}