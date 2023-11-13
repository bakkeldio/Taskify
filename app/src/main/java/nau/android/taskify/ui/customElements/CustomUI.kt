package nau.android.taskify.ui.customElements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import nau.android.taskify.R
import nau.android.taskify.ui.enums.Priority
import nau.android.taskify.ui.extensions.noRippleClickable


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
    onValueChange: (String) -> Unit,
) {
    TextField(value = value, onValueChange = { newValue ->
        onValueChange(newValue)
    }, placeholder = {
        if (placeHolder.isNotEmpty()) {
            Text(text = placeHolder)
        }
    }, colors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent
    ), maxLines = maxLines, modifier = modifier
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
        modifier = modifier,
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
                    tint = MaterialTheme.colorScheme.outline
                )
            })
        }
    }
}

@Composable
fun TaskifyMenuDropDown(
    displayMenu: Boolean,
    showDetails: Boolean,
    showCompleted: Boolean,
    onShowDetailsChanged: (Boolean) -> Unit,
    onShowCompletedChanged: (Boolean) -> Unit,
    displayMenuChanged: (Boolean) -> Unit,
    onShowSortBottomSheet: () -> Unit
) {
    DropdownMenu(
        expanded = displayMenu,
        onDismissRequest = { displayMenuChanged(false) },
        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
    ) {
        DropdownMenuItem(text = { Text(text = "Show details") }, onClick = {
            onShowDetailsChanged(!showDetails)
        }, trailingIcon = {
            Checkbox(checked = showDetails, onCheckedChange = null)
        })


        DropdownMenuItem(text = { Text(text = "Show completed") },
            onClick = { onShowCompletedChanged(!showCompleted) },
            trailingIcon = {
                Checkbox(
                    checked = showCompleted,
                    onCheckedChange = null,
                    modifier = Modifier.padding(0.dp)
                )
            })

        DropdownMenuItem(text = { Text(text = "Sort") }, onClick = {
            onShowSortBottomSheet()
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
