package nau.android.taskify.ui.customElements

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import nau.android.taskify.R
import nau.android.taskify.ui.category.Category
import nau.android.taskify.ui.extensions.noRippleClickable
import nau.android.taskify.ui.task.TaskPriority


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
    taskPriority: TaskPriority,
    dropDownMenuOpen: Boolean,
    showRadioButtons: Boolean = true,
    changeTaskPriority: (TaskPriority) -> Unit,
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
        TaskPriority.values().forEach { priority ->
            DropdownMenuItem(text = {
                Text(text = "${priority.name} priority")
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskifyCategorySelectionDropDownMenu(
    categories: List<Category>,
    dropDownMenuOpen: Boolean,
    onDismissRequest: () -> Unit,
    onChangeCategory: (Category) -> Unit
) {
    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        DropdownMenu(
            expanded = dropDownMenuOpen,
            onDismissRequest = onDismissRequest,
            properties = PopupProperties(false),
            modifier = Modifier
                .heightIn(max = 250.dp)
                .fillMaxWidth(0.5f)
        ) {
            categories.forEach { category ->
                DropdownMenuItem(text = {
                    Text(text = category.name, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }, onClick = {
                    onChangeCategory(category)
                }, leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.MailOutline,
                        contentDescription = "category_icon"
                    )
                })
            }
        }
    }

}