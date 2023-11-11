package nau.android.taskify.ui.tasksList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.android.awaitFrame
import nau.android.taskify.R
import nau.android.taskify.ui.DateInfo
import nau.android.taskify.ui.customElements.TaskifyCategorySelectionDropDownMenu
import nau.android.taskify.ui.customElements.TaskifyCreateTaskButton
import nau.android.taskify.ui.customElements.TaskifyPrioritySelectionDropdownMenu
import nau.android.taskify.ui.customElements.TaskifyTextField
import nau.android.taskify.ui.extensions.formatTaskifyDate
import nau.android.taskify.ui.extensions.formatToAmPm
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
    onDismissBottomSheet: () -> Unit,
    openDatePickerDialog: (Task, DateInfo) -> Unit,
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
        mutableStateOf<Category?>(null)
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
        }, windowInsets = WindowInsets.ime.union(WindowInsets.navigationBars)
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
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {

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
                                ), dateInfo
                            )
                        },
                        tint = MaterialTheme.colorScheme.primary
                    )


                    if (dateInfo.date != null) {
                        Text(
                            text = dateInfo.date.formatTaskifyDate(
                                dateInfo.time?.formatToAmPm() ?: "", false
                            ),
                            modifier = Modifier.padding(start = 10.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }


                Icon(
                    painter = painterResource(id = R.drawable.ic_flag),
                    contentDescription = "Priority",
                    modifier = Modifier.clickable {
                        priorityDropDownOpen.value = true
                    },
                    tint = MaterialTheme.colorScheme.outline
                )

                Text(text = currentCategory.value?.name ?: "No category",
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp)
                        )
                        .clickable(role = Role.Button) {
                            categoryDropDownOpen.value = true
                        }
                        .padding(8.dp))

            }

            TaskifyCreateTaskButton(isTaskTitleEmpty = taskTitle.value.isEmpty()) {

                dateInfo.time?.also { time ->
                    dateInfo.date?.set(Calendar.HOUR_OF_DAY, time.first)
                    dateInfo.date?.set(Calendar.MINUTE, time.second)
                }

                createTask(
                    Task(
                        name = taskTitle.value,
                        priority = taskPriority.value,
                        dueDate = dateInfo.date,
                        timeIncluded = dateInfo.time != null,
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