package nau.android.taskify.ui.tasksList

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.android.awaitFrame
import nau.android.taskify.R
import nau.android.taskify.ui.DateInfo
import nau.android.taskify.ui.category.CategoriesListState
import nau.android.taskify.ui.category.CategoriesViewModel
import nau.android.taskify.ui.customElements.TaskifyCreateTaskButton
import nau.android.taskify.ui.customElements.TaskifyPrioritySelectionDropdownMenu
import nau.android.taskify.ui.customElements.TaskifyTextField
import nau.android.taskify.ui.extensions.formatTaskifyDate
import nau.android.taskify.ui.extensions.formatToAmPm
import nau.android.taskify.ui.extensions.noRippleClickable
import nau.android.taskify.ui.model.Category
import nau.android.taskify.ui.model.Task
import java.util.Calendar


@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun CreateTaskBottomSheet(
    dateInfo: DateInfo,
    task: Task,
    category: Category?,
    sheetState: SheetState,
    onDismissBottomSheet: () -> Unit,
    openDatePickerDialog: (Task, Category?, DateInfo) -> Unit,
    createTask: (Task) -> Unit
) {

    val priorityDropDownOpen = remember {
        mutableStateOf(false)
    }

    val categoryDropDownOpen = remember {
        mutableStateOf(false)
    }

    val focusRequester = remember {
        FocusRequester()
    }

    val taskTitle = remember {
        mutableStateOf(task.name)
    }

    val taskPriority = remember {
        mutableStateOf(task.priority)
    }

    val taskRepeatInterval = remember {
        task.repeatInterval
    }

    val reminders = remember {
        mutableStateOf(task.reminders)
    }

    val currentCategory = remember {
        mutableStateOf(category)
    }


    LaunchedEffect(focusRequester) {
        awaitFrame()
        focusRequester.requestFocus()
    }

    TaskifyPrioritySelectionDropdownMenu(
        taskPriority = taskPriority.value,
        dropDownMenuOpen = priorityDropDownOpen.value,
        changeTaskPriority = { newPriority ->
            taskPriority.value = newPriority
            priorityDropDownOpen.value = false
        },
        closeDropDown = {
            priorityDropDownOpen.value = false
        },
        showRadioButtons = false
    )


    TaskifyCategorySelectionDropDownMenu(dropDownMenuOpen = categoryDropDownOpen.value,
        onDismissRequest = {
            categoryDropDownOpen.value = false
        },
        onChangeCategory = {
            currentCategory.value = it
            categoryDropDownOpen.value = false
        })

    ModalBottomSheet(
        onDismissRequest = {
            onDismissBottomSheet()
        },
        windowInsets = WindowInsets.ime.union(WindowInsets.navigationBars),
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {

        TaskifyTextField(
            Modifier
                .focusRequester(focusRequester)
                .verticalScroll(rememberScrollState()),
            value = taskTitle.value,
            onValueChange = { newTitle ->
                taskTitle.value = newTitle
            })


        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp, bottom = 10.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Icon(
                    painter = painterResource(id = R.drawable.ic_date),
                    contentDescription = "Choosing date",
                    modifier = Modifier.clickable {
                        openDatePickerDialog(
                            Task(
                                name = taskTitle.value,
                                priority = taskPriority.value,
                                categoryId = currentCategory.value?.id,
                                repeatInterval = taskRepeatInterval,
                                reminders = reminders.value
                            ), currentCategory.value, dateInfo
                        )
                    },
                    tint = MaterialTheme.colorScheme.primary
                )


                if (dateInfo.date != null) {
                    Text(
                        text = dateInfo.date.formatTaskifyDate(
                            if (dateInfo.timeIncluded) Pair(
                                dateInfo.date[Calendar.HOUR_OF_DAY],
                                dateInfo.date[Calendar.MINUTE]
                            ).formatToAmPm() else "", false
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Icon(
                    painter = painterResource(id = R.drawable.ic_flag),
                    contentDescription = "Priority",
                    modifier = Modifier.clickable {
                        priorityDropDownOpen.value = true
                    },
                    tint = taskPriority.value.color
                )

                Text(text = currentCategory.value?.name ?: "No category",
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp)
                        )
                        .noRippleClickable {
                            categoryDropDownOpen.value = true
                        }
                        .padding(8.dp))

            }

            TaskifyCreateTaskButton(isTaskTitleEmpty = taskTitle.value.isEmpty()) {

                if (dateInfo.timeIncluded) {
                    dateInfo.date?.apply {
                        set(Calendar.HOUR_OF_DAY, get(Calendar.HOUR_OF_DAY))
                        set(Calendar.MINUTE, get(Calendar.MINUTE))
                    }
                }

                createTask(
                    Task(
                        name = taskTitle.value,
                        priority = taskPriority.value,
                        dueDate = dateInfo.date,
                        timeIncluded = dateInfo.timeIncluded,
                        categoryId = currentCategory.value?.id,
                        creationDate = Calendar.getInstance(),
                        repeatInterval = taskRepeatInterval,
                        reminders = reminders.value
                    )
                )
            }
        }
    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskifyCategorySelectionDropDownMenu(
    viewModel: CategoriesViewModel = hiltViewModel(),
    dropDownMenuOpen: Boolean,
    onDismissRequest: () -> Unit,
    onChangeCategory: (Category) -> Unit
) {

    val categories =
        viewModel.getCategories().collectAsStateWithLifecycle(CategoriesListState.Loading)

    when (val result = categories.value) {

        is CategoriesListState.Success -> {
            CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
                DropdownMenu(
                    expanded = dropDownMenuOpen,
                    onDismissRequest = onDismissRequest,
                    properties = PopupProperties(false),
                    modifier = Modifier
                        .heightIn(max = 250.dp)
                        .fillMaxWidth(0.5f)
                        .background(MaterialTheme.colorScheme.surface)
                ) {

                    result.categories.forEach { category ->
                        DropdownMenuItem(text = {
                            Text(
                                text = category.name,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }, onClick = {
                            onChangeCategory(category)
                        }, leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_category),
                                contentDescription = "category_icon",
                                tint = Color(category.color)
                            )
                        })

                    }
                }
            }
        }

        else -> {}
    }
}