package nau.android.taskify.ui.task

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import nau.android.taskify.LocalTaskifyColors
import nau.android.taskify.R
import nau.android.taskify.ui.extensions.formatTaskifyDate
import nau.android.taskify.ui.model.Category
import nau.android.taskify.ui.model.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItemContent(
    isInMultiSelection: Boolean,
    selected: Boolean,
    task: Task,
    taskCategory: Category? = null,
    showDetails: Boolean,
    onSelectChange: (Boolean) -> Unit,
    navigateToTaskDetail: ((Long) -> Unit)? = null
) {

    val passedDate = false

    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .background(if (isInMultiSelection && selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
            .clickable {
                if (!isInMultiSelection) {
                    if (navigateToTaskDetail != null) {
                        navigateToTaskDetail(task.id)
                    }
                } else {
                    onSelectChange(!selected)
                }
            }
            .padding(horizontal = 15.dp, vertical = 15.dp)
    ) {
        val (checkBox, title, date, description, category) = createRefs()

        val barrier = createBottomBarrier(checkBox, title, description, margin = 3.dp)

        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {

            Checkbox(checked = selected,
                onCheckedChange = if (isInMultiSelection) null else onSelectChange,
                modifier = Modifier
                    .constrainAs(checkBox) {
                        top.linkTo(parent.top)
                    }
                    .wrapContentSize(Alignment.TopStart),
                interactionSource = NoRippleInteractionSource(), colors = CheckboxDefaults.colors(
                    checkedColor = LocalTaskifyColors.current.completedTaskColor,
                    uncheckedColor = task.priority.color
                ))

        }
        Text(
            text = task.name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .constrainAs(title) {
                    top.linkTo(checkBox.top)
                    bottom.linkTo(checkBox.bottom)
                    start.linkTo(checkBox.end, 15.dp)
                    if (!showDetails) {
                        end.linkTo(date.start)
                    }
                    width = Dimension.fillToConstraints
                },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )


        if (showDetails) {

            if (task.description != null) {
                Text(text = task.description,
                    modifier = Modifier
                        .constrainAs(description) {
                            start.linkTo(title.start)
                            end.linkTo(category.start)
                            top.linkTo(title.bottom, 5.dp)
                            width = Dimension.fillToConstraints
                        },
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis)
            }

            Text(
                text = taskCategory?.name ?: "No category",
                modifier = Modifier
                    .constrainAs(category) {
                        end.linkTo(parent.end)
                        top.linkTo(barrier)
                    },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Medium
                )
            )
        }

        Text(
            text = task.dueDate?.formatTaskifyDate() ?: "",
            modifier = Modifier
                .constrainAs(date) {
                    if (showDetails) {
                        top.linkTo(barrier)
                        start.linkTo(title.start)
                    } else {
                        end.linkTo(parent.end)
                        top.linkTo(checkBox.top)
                        bottom.linkTo(checkBox.bottom)
                    }
                },
            style = MaterialTheme.typography.bodyMedium.copy(color = if (passedDate) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary),
            fontWeight = FontWeight.Medium
        )


    }


}


@Preview(showBackground = true)
@Composable
fun TaskItemDesignPreview() {

    //TaskItemDesign(Task(), false)

}