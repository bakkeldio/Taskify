package nau.android.taskify.ui.eisenhowerMatrix

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nau.android.taskify.R
import nau.android.taskify.ui.MainDestination
import nau.android.taskify.ui.customElements.NoTasksForEisenhowerMatrix
import nau.android.taskify.ui.customElements.TaskifyCustomCheckBox
import nau.android.taskify.ui.extensions.applyColorForDateTime
import nau.android.taskify.ui.extensions.formatTaskifyDate
import nau.android.taskify.ui.extensions.formatToAmPm
import nau.android.taskify.ui.model.EisenhowerMatrixModel
import nau.android.taskify.ui.model.Task
import java.util.Calendar

internal val LocalDragTargetInfo = compositionLocalOf {
    DragTargetInfo()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EisenhowerMatrix(
    mainDestination: MainDestination, matrixViewModel: EisenhowerViewModel = hiltViewModel(),
    navigateToListDetails: (EisenhowerMatrixQuadrant) -> Unit,
    navigateToTaskDetails: (Task) -> Unit
) {

    val matrixState =
        matrixViewModel.getTasks().collectAsStateWithLifecycle(initialValue = MatrixState.Loading)

    var showEditMatrixBottomSheet by remember {
        mutableStateOf(false)
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(text = mainDestination.title)
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            actions = {
                IconButton(onClick = {
                    showEditMatrixBottomSheet = true
                }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(id = R.string.edit_icon)
                    )
                }
            }
        )
    }, contentWindowInsets = WindowInsets(bottom = 0.dp)) { paddingValues ->


        if (showEditMatrixBottomSheet) {
            MatrixEditBottomSheet(onDismiss = {
                showEditMatrixBottomSheet = false
            })
        }

        DraggableScreen {

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(start = 10.dp, end = 10.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                when (val result = matrixState.value) {
                    is MatrixState.Success -> {
                        MatrixScreen(
                            result.matrixModel,
                            matrixViewModel,
                            navigateToListDetails = navigateToListDetails,
                            navigateToTaskDetails = navigateToTaskDetails
                        )
                    }

                    is MatrixState.Error -> {

                    }

                    is MatrixState.Loading -> {

                    }
                }
            }
        }
    }

}

@Composable
fun ColumnScope.MatrixScreen(
    matrixModel: EisenhowerMatrixModel,
    viewModel: EisenhowerViewModel,
    navigateToTaskDetails: (Task) -> Unit,
    navigateToListDetails: (EisenhowerMatrixQuadrant) -> Unit
) {

    var quadrantContainingDragTarget by remember {
        mutableStateOf<EisenhowerMatrixQuadrant?>(null)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        MatrixQuadrant(
            EisenhowerMatrixQuadrant.IMPORTANT_URGENT,
            tasks = matrixModel.importantUrgentTasks,
            quadrantContainingDragTarget = quadrantContainingDragTarget,
            changeQuadrantContainingDragTarget = {
                quadrantContainingDragTarget = it

            },
            updateTask = { task ->
                viewModel.updateTask(task, EisenhowerMatrixQuadrant.IMPORTANT_URGENT)
            },
            completeTask = {
                viewModel.completeTask(it)
            },
            navigateToListDetails = navigateToListDetails,
            navigateToTaskDetails = navigateToTaskDetails
        )
        MatrixQuadrant(
            EisenhowerMatrixQuadrant.NOT_URGENT_IMPORTANT,
            tasks = matrixModel.importantNotUrgentTasks,
            quadrantContainingDragTarget = quadrantContainingDragTarget,
            changeQuadrantContainingDragTarget = {
                quadrantContainingDragTarget = it
            },
            updateTask = { task ->
                viewModel.updateTask(task, EisenhowerMatrixQuadrant.NOT_URGENT_IMPORTANT)
            },
            completeTask = {
                viewModel.completeTask(it)
            }, navigateToListDetails = navigateToListDetails,
            navigateToTaskDetails = navigateToTaskDetails
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        MatrixQuadrant(
            EisenhowerMatrixQuadrant.URGENT_UNIMPORTANT,
            tasks = matrixModel.urgentNotImportantTasks,
            quadrantContainingDragTarget = quadrantContainingDragTarget,
            changeQuadrantContainingDragTarget = {
                quadrantContainingDragTarget = it
            },
            updateTask = { task ->
                viewModel.updateTask(task, EisenhowerMatrixQuadrant.URGENT_UNIMPORTANT)
            },
            completeTask = {
                viewModel.completeTask(it)
            },
            navigateToListDetails = navigateToListDetails,
            navigateToTaskDetails = navigateToTaskDetails
        )
        MatrixQuadrant(
            EisenhowerMatrixQuadrant.NOT_URGENT_UNIMPORTANT,
            tasks = matrixModel.unImportantNotUrgentTasks,
            quadrantContainingDragTarget = quadrantContainingDragTarget,
            changeQuadrantContainingDragTarget = {
                quadrantContainingDragTarget = it
            },
            updateTask = { task ->
                viewModel.updateTask(task, EisenhowerMatrixQuadrant.NOT_URGENT_UNIMPORTANT)
            },
            completeTask = {
                viewModel.completeTask(it)
            },
            navigateToListDetails = navigateToListDetails,
            navigateToTaskDetails = navigateToTaskDetails
        )
    }
}


@Composable
fun Quadrant(
    width: Dp,
    quadrant: EisenhowerMatrixQuadrant,
    list: List<Task>,
    navigateToListDetails: (EisenhowerMatrixQuadrant) -> Unit,
    navigateToTaskDetails: (Task) -> Unit,
    onCompleteTask: (Task) -> Unit,
    onUnCompleteTask: (Task) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = quadrant.titleR),
            style = MaterialTheme.typography.titleSmall.copy(color = quadrant.color),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.clickable {
                navigateToListDetails(quadrant)
            }
        )
        if (list.isNotEmpty()) {
            LazyColumn(content = {
                items(list, key = {
                    it.id
                }) { task ->
                    DragTarget(
                        dataToDrop = task, eisenhowerMatrixQuadrant = quadrant
                    ) {
                        QuadrantTaskItem(
                            width,
                            task = task,
                            navigateToTaskDetails = navigateToTaskDetails,
                            onCompleteTask = onCompleteTask,
                            onUnCompleteTask = onUnCompleteTask
                        )

                    }

                }
            })
        } else {
            NoTasksForEisenhowerMatrix()
        }
    }
}

@Composable
fun QuadrantTaskItem(
    width: Dp,
    task: Task,
    navigateToTaskDetails: (Task) -> Unit,
    onCompleteTask: (Task) -> Unit,
    onUnCompleteTask: (Task) -> Unit
) {

    Row(
        modifier = Modifier
            .width(width)
            .background(MaterialTheme.colorScheme.surface)
            .shadow(if (LocalDragTargetInfo.current.isDragging && task == LocalDragTargetInfo.current.dataToDrop) 0.2.dp else 0.dp)
            .padding(start = 10.dp, end = 10.dp, top = 10.dp)
            .clickable {
                navigateToTaskDetails(task)
            },
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        TaskifyCustomCheckBox(
            checked = task.completed,
            borderColor = task.priority.color,
            onCheckChanged = {
                if (it) {
                    onCompleteTask(task)
                } else {
                    onUnCompleteTask(task)
                }
            })

        Column {
            Text(
                text = task.name,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            task.dueDate?.run {
                Text(
                    text = applyColorForDateTime(
                        date = formatTaskifyDate(
                            if (task.timeIncluded) Pair(
                                get(Calendar.HOUR_OF_DAY), get(Calendar.MINUTE)
                            ).formatToAmPm() else ""
                        )
                    ),
                    modifier = Modifier.padding(top = 5.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

    }
}

@Composable
fun RowScope.MatrixQuadrant(
    matrixQuadrant: EisenhowerMatrixQuadrant,
    tasks: List<Task>,
    quadrantContainingDragTarget: EisenhowerMatrixQuadrant?,
    changeQuadrantContainingDragTarget: (EisenhowerMatrixQuadrant?) -> Unit,
    navigateToListDetails: (EisenhowerMatrixQuadrant) -> Unit,
    navigateToTaskDetails: (Task) -> Unit,
    updateTask: (Task) -> Unit,
    completeTask: (Task) -> Unit
) {
    val dragTargetInfo = LocalDragTargetInfo.current
    val dragOffset = dragTargetInfo.dragOffset
    val dragPosition = dragTargetInfo.dragPosition
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .weight(1f)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(15.dp))
            .clip(RoundedCornerShape(15.dp))
            .border(
                width = 1.dp,
                if (quadrantContainingDragTarget == matrixQuadrant) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(15.dp)
            )
            .onGloballyPositioned {
                it
                    .boundsInWindow()
                    .let { rect ->
                        val isCurrentDropTarget = rect.contains(dragOffset + dragPosition)
                        if (isCurrentDropTarget && !dragTargetInfo.itemDropped) {
                            changeQuadrantContainingDragTarget(matrixQuadrant)
                        }
                    }
            }, contentAlignment = Alignment.TopCenter
    ) {
        if (!dragTargetInfo.itemDropped && quadrantContainingDragTarget == matrixQuadrant && !dragTargetInfo.isDragging) {
            dragTargetInfo.dataToDrop?.let {
                changeQuadrantContainingDragTarget(null)
                val task = it as Task
                dragTargetInfo.itemDropped = true
                updateTask(task)
            }
        }
        Quadrant(
            this.maxWidth,
            quadrant = matrixQuadrant,
            list = tasks,
            navigateToTaskDetails = navigateToTaskDetails,
            onCompleteTask = {
                completeTask(
                    it
                )
            },
            onUnCompleteTask = {}, navigateToListDetails = navigateToListDetails
        )
    }
}

@Preview(showBackground = true)
@Composable
fun QuadranTaskItemPreview() {

}

enum class EisenhowerMatrixQuadrant(
    val id: Int,
    val titleR: Int,
    val color: Color,
    val icon: Int
) {
    IMPORTANT_URGENT(
        1,
        R.string.urgent_and_important, Color(0xFFEF5350),
        R.drawable.ic_roman_one
    ),
    NOT_URGENT_IMPORTANT(
        2,
        R.string.not_urgent_and_important, Color(0xFFF6BE00),
        R.drawable.ic_roman_2
    ),
    URGENT_UNIMPORTANT(
        3,
        R.string.urgent_and_not_important, Color(0xFF4169E1),
        R.drawable.ic_roman_3
    ),
    NOT_URGENT_UNIMPORTANT(
        4,
        R.string.not_urgent_and_not_important,
        Color(0xFF228B22),
        R.drawable.ic_roman_4
    );

    companion object {
        fun getMatrixQuadrantById(id: Int): EisenhowerMatrixQuadrant {
            return values().find {
                it.id == id
            }!!
        }
    }
}