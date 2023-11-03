package nau.android.taskify.ui.tasksList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nau.android.taskify.R
import nau.android.taskify.data.generateCategories
import nau.android.taskify.ui.category.Category
import nau.android.taskify.ui.customElements.TaskifyCategorySelectionDropDownMenu
import nau.android.taskify.ui.customElements.TaskifyCreateTaskButton
import nau.android.taskify.ui.customElements.TaskifyPrioritySelectionDropdownMenu
import nau.android.taskify.ui.customElements.TaskifyTextField
import nau.android.taskify.ui.dialogs.TaskifyDatePickerDialog
import nau.android.taskify.ui.task.TaskPriority


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CreateTaskBottomSheet(onDismissBottomSheet: () -> Unit, openDatePickerDialog: () -> Unit) {

    val taskTitle = remember {
        mutableStateOf("")
    }

    val taskPriority = remember {
        mutableStateOf(TaskPriority.NoPriority)
    }

    val priorityDropDownOpen = remember {
        mutableStateOf(false)
    }

    val categoryDropDownOpen = remember {
        mutableStateOf(false)
    }

    val focusRequester = remember {
        FocusRequester()
    }

    val category = remember {
        mutableStateOf(Category("uid", "No category"))
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

    TaskifyCategorySelectionDropDownMenu(
        categories = generateCategories(),
        dropDownMenuOpen = categoryDropDownOpen.value,
        onDismissRequest = {
            categoryDropDownOpen.value = false
        },
        onChangeCategory = {
            category.value = it
            categoryDropDownOpen.value = false
        }
    )

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

                Icon(
                    painter = painterResource(id = R.drawable.ic_date),
                    contentDescription = "Choosing date",
                    modifier = Modifier.clickable {
                        openDatePickerDialog()
                    }, tint = MaterialTheme.colorScheme.primary
                )


                Icon(
                    painter = painterResource(id = R.drawable.ic_flag),
                    contentDescription = "Priority",
                    modifier = Modifier
                        .clickable {
                            priorityDropDownOpen.value = true
                        },
                    tint = taskPriority.value.color
                )

                Text(
                    text = category.value.name,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable(role = Role.Button) {
                            categoryDropDownOpen.value = true
                        }
                        .padding(8.dp)

                )

            }

            TaskifyCreateTaskButton(isTaskTitleEmpty = taskTitle.value.isEmpty()) {

            }
        }
    }


}