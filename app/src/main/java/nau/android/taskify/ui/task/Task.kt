package nau.android.taskify.ui.task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import nau.android.taskify.R

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import nau.android.taskify.ui.category.Category
import nau.android.taskify.ui.extensions.formatTaskifyDate
import nau.android.taskify.ui.model.TaskWithCategory


class Task(
    val name: String,
    val description: String,
    val category: Category?,
    val taskDate: String,
    val taskPriority: TaskPriority,
    val completed: Boolean
) {
    constructor() : this("", "", null, "", TaskPriority.NoPriority, false)
}

enum class TaskPriority(val color: Color, val priorityNumber: Int) {
    High(Color.Red, 1), Medium(Color.Yellow, 2), Low(Color.Blue, 3), NoPriority(Color.Gray, 4);

    fun getAllPriorities() = values()
}

@Composable
fun TaskItemDesign(taskWithCategory: TaskWithCategory, showDetails: Boolean) {

    val passedDate = false

    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        val (radioButton, title, date, description, category) = createRefs()

        val barrier = createBottomBarrier(radioButton, title, description, margin = 3.dp)


        RadioButton(selected = false,
            onClick = {

            },
            modifier = Modifier
                .constrainAs(radioButton) {
                    top.linkTo(parent.top)
                }
                .wrapContentSize(Alignment.TopStart),
            colors = RadioButtonDefaults.colors(
                unselectedColor = MaterialTheme.colorScheme.outline, selectedColor = Color.Gray
            ))


        Text(
            text = taskWithCategory.task.name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(radioButton.top)
                bottom.linkTo(radioButton.bottom)
                start.linkTo(radioButton.end)
                if (!showDetails) {
                    end.linkTo(date.start)
                }
                width = Dimension.fillToConstraints
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )


        if (showDetails) {
            if (taskWithCategory.task.description != null) {
                Text(text = taskWithCategory.task.description,
                    modifier = Modifier
                        .constrainAs(description) {
                            start.linkTo(title.start)
                            end.linkTo(category.start)
                            top.linkTo(title.bottom)
                            width = Dimension.fillToConstraints
                        }
                        .padding(top = 5.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis)
            }

            Row(modifier = Modifier
                .constrainAs(category) {
                    end.linkTo(parent.end)
                    top.linkTo(barrier)
                }
                .padding(start = 13.dp, end = 13.dp, bottom = 8.dp)
            ) {

                Icon(
                    painter = painterResource(id = R.drawable.ic_work_category),
                    contentDescription = null
                )
                Text(
                    text = taskWithCategory.category?.name ?: "No category",
                    modifier = Modifier.padding(start = 5.dp),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Medium
                    )
                )

            }
        }

        Text(text = taskWithCategory.task.dueDate?.formatTaskifyDate() ?: "",
            modifier = Modifier
                .constrainAs(date) {

                    if (showDetails) {
                        top.linkTo(barrier)
                        start.linkTo(title.start)
                    } else {
                        end.linkTo(parent.end)
                        top.linkTo(radioButton.top)
                        bottom.linkTo(radioButton.bottom)

                    }

                }
                .padding(horizontal = if (showDetails) 0.dp else 13.dp)
                .padding(bottom = if (showDetails) 8.dp else 0.dp),
            style = MaterialTheme.typography.bodySmall.copy(color = if (passedDate) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary))


    }


}


@Preview(showBackground = true)
@Composable
fun TaskItemDesignPreview() {

    //TaskItemDesign(Task(), false)

}