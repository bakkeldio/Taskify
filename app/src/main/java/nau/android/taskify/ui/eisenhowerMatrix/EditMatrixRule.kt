package nau.android.taskify.ui.eisenhowerMatrix

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import nau.android.taskify.R
import nau.android.taskify.ui.category.CategoriesListState
import nau.android.taskify.ui.category.CategoriesViewModel
import nau.android.taskify.ui.enums.Priority
import nau.android.taskify.ui.model.Category
import nau.android.taskify.ui.task.NoRippleInteractionSource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatrixQuadrantRules(
    eisenhowerMatrixQuadrant: EisenhowerMatrixQuadrant,
    configuration: QuadrantConfig,
    categoriesViewModel: CategoriesViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val coroutineScope = rememberCoroutineScope()

    var selectedRuleEnum by remember {
        mutableStateOf(RuleEnum.CATEGORY)
    }

    val categories = categoriesViewModel.getCategories()
        .collectAsStateWithLifecycle(initialValue = CategoriesListState.Loading)


    var selectedCategories by remember {
        mutableStateOf(configuration.categories)
    }

    var selectedPriorities by remember {
        mutableStateOf(configuration.priority)
    }

    var selectedDates by remember {
        mutableStateOf(configuration.date)
    }


    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        modifier = Modifier.fillMaxSize()
    ) {

        Column(modifier = Modifier.padding(start = 15.dp, end = 15.dp)) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    onDismiss()
                }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
                Text(
                    text = stringResource(id = eisenhowerMatrixQuadrant.titleR),
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.Black)
                )
                TextButton(onClick = {
                    coroutineScope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        onDismiss()
                    }
                }) {
                    Text(text = stringResource(id = R.string.done))
                }
            }

            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {

                FilterChip(
                    selected = selectedRuleEnum == RuleEnum.CATEGORY,
                    onClick = {
                        selectedRuleEnum = RuleEnum.CATEGORY
                    },
                    label = { Text(text = stringResource(id = RuleEnum.CATEGORY.titleR)) })

                Spacer(modifier = Modifier.width(15.dp))

                FilterChip(
                    selected = selectedRuleEnum == RuleEnum.PRIORITY,
                    onClick = {
                        selectedRuleEnum = RuleEnum.PRIORITY
                    },
                    label = {
                        Text(text = stringResource(id = RuleEnum.PRIORITY.titleR))
                    })

                Spacer(modifier = Modifier.width(15.dp))

                FilterChip(
                    selected = selectedRuleEnum == RuleEnum.DATE,
                    onClick = {
                        selectedRuleEnum = RuleEnum.DATE
                    },
                    label = {
                        Text(text = stringResource(id = RuleEnum.DATE.titleR))
                    })

            }

            Spacer(modifier = Modifier.height(15.dp))

            Crossfade(targetState = selectedRuleEnum, label = "rules") { rule ->
                when (rule) {
                    RuleEnum.CATEGORY -> CategoriesRule(
                        categoriesState = categories.value,
                        selectedCategories = selectedCategories,
                        onSelectAllChanged = { categoryList, selected ->
                            val list = selectedCategories.toMutableList()
                            list.clear()
                            if (selected){
                                list.addAll(categoryList.map { it.id })
                            } else {
                                list.removeAll(categoryList.map { it.id })
                            }
                            selectedCategories = list
                        },
                        onSelectionChanged = { categoryId, selected ->
                            val list = selectedCategories.toMutableList()
                            if (selected) {
                                list.add(categoryId)
                            } else {
                                list.remove(categoryId)
                            }
                            selectedCategories = list
                        }
                    )

                    RuleEnum.PRIORITY -> PrioritiesRule(
                        selectedPriorities = selectedPriorities,
                        onSelectionChanged = { priority, selected ->
                            val priorities = selectedPriorities.toMutableList()
                            if (selected) {
                                priorities.add(priority)
                            } else {
                                priorities.remove(priority)
                            }
                            selectedPriorities = priorities
                        })

                    RuleEnum.DATE -> DatesRule(
                        selectedDates = selectedDates,
                        onSelectionChanged = { date, selected ->
                            val dates = selectedDates.toMutableList()
                            if (selected) {
                                dates.add(date)
                            } else {
                                dates.remove(date)
                            }
                            selectedDates = dates
                        })
                }
            }
        }
    }
}
@Composable
fun CategoriesRule(
    categoriesState: CategoriesListState,
    selectedCategories: List<Long>,
    onSelectAllChanged: (List<Category>, Boolean) -> Unit,
    onSelectionChanged: (Long, Boolean) -> Unit
) {
    val categories = categoriesState as? CategoriesListState.Success ?: return
    LazyColumn(content = {
        item {
            RuleItem(
                title = "All",
                selected = selectedCategories.containsAll(categories.categories.map {
                    it.id
                }),
                onSelectionChanged = {
                    onSelectAllChanged(categories.categories, it)
                }
            )
        }
        items(categories.categories) { category ->
            RuleItem(
                title = category.name,
                selected = selectedCategories.contains(category.id),
                onSelectionChanged = {
                    onSelectionChanged(category.id, it)
                })
        }
    }, modifier = Modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp)))
}

@Composable
fun PrioritiesRule(
    selectedPriorities: List<Priority>,
    onSelectionChanged: (Priority, Boolean) -> Unit
) {
    LazyColumn(content = {
        items(Priority.values()) { priority ->
            RuleItem(
                title = priority.title,
                selected = selectedPriorities.contains(priority),
                onSelectionChanged = {
                    onSelectionChanged(priority, it)
                })
        }
    }, modifier = Modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp)))
}


@Composable
fun DatesRule(selectedDates: List<Date>, onSelectionChanged: (Date, Boolean) -> Unit) {
    LazyColumn(content = {
        items(Date.values()) { date ->
            RuleItem(
                title = stringResource(id = date.title),
                selected = selectedDates.contains(date),
                onSelectionChanged = {
                    onSelectionChanged(date, it)
                }
            )
        }
    }, modifier = Modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp)))
}


@Composable
fun RuleItem(title: String, selected: Boolean, onSelectionChanged: (Boolean) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontWeight = FontWeight.Medium)


        Checkbox(checked = selected, onCheckedChange = {
            onSelectionChanged(it)
        }, interactionSource = NoRippleInteractionSource())
    }
}

enum class RuleEnum(val titleR: Int) {
    CATEGORY(R.string.category),
    PRIORITY(R.string.priority),
    DATE(R.string.date)
}