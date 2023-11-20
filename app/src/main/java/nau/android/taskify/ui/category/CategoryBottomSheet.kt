package nau.android.taskify.ui.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.android.awaitFrame
import nau.android.taskify.R
import nau.android.taskify.ui.extensions.noRippleClickable
import nau.android.taskify.ui.model.Category
import kotlin.math.min


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryBottomSheet(categoryId: Long = 0L, onDismiss: () -> Unit) {

    ModalBottomSheet(onDismissRequest = {
        onDismiss()
    }, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        val colors = CategoryColors.values().map { it.value }
        if (categoryId == 0L) {
            NewCategory(colorsList = colors, onDismiss = onDismiss)
        } else {
            ExistingCategory(categoryId = categoryId, colorsList = colors, onDismiss = onDismiss)
        }
    }
}

@Composable
private fun NewCategory(
    colorsList: List<Color>,
    categoryCreateViewModel: CategoryCreateViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {

    CommonContent(
        category = createEmptyCategory(), action = Action.CREATE, colorsList = colorsList, save = {
            categoryCreateViewModel.createCategory(it)
        }, onDismiss = onDismiss
    )
}

@Composable
private fun ExistingCategory(
    categoryId: Long,
    colorsList: List<Color>,
    categoryEditViewModel: CategoryEditViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    LaunchedEffect(true) {
        categoryEditViewModel.getCategoryById(categoryId)
    }
    val categoryState = categoryEditViewModel.categoryLiveData.observeAsState()

    when (val result = categoryState.value) {

        is CategoryState.Success -> {
            CommonContent(category = result.category, action = Action.UPDATE, colorsList, save = {
                categoryEditViewModel.editCategory(it)
            }, onDismiss = onDismiss)
        }

        is CategoryState.Empty -> {}

        else -> Unit
    }

}

@Composable
private fun CommonContent(
    category: Category,
    action: Action,
    colorsList: List<Color>,
    save: (Category) -> Unit,
    onDismiss: () -> Unit
) {

    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(focusRequester) {
        awaitFrame()
        focusRequester.requestFocus()
    }

    var categoryName by remember {
        mutableStateOf(category.name)
    }

    var categoryColor by remember {
        mutableIntStateOf(category.color)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(start = 13.dp, end = 13.dp, bottom = 25.dp)
            .verticalScroll(
                rememberScrollState()
            )
    ) {
        Text(
            text = stringResource(id = R.string.create_new_category),
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(value = categoryName,
            onValueChange = {
                categoryName = it
            },
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth()
                .padding(top = 20.dp),
            placeholder = {
                Text(text = stringResource(id = R.string.please_input_category))
            },
            label = { Text(text = stringResource(id = R.string.name)) })

        Column(
            modifier = Modifier
                .padding(top = 20.dp)
                .background(color = MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
                .padding(10.dp)
        ) {

            Text(text = stringResource(id = R.string.color), fontWeight = FontWeight.Medium)

            CategoryColorSelector(colorList = colorsList,
                value = Color(categoryColor),
                onColorChange = {
                    categoryColor = it.toArgb()
                })


            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.icon), fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                    Text(text = stringResource(id = R.string.none))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Arrow to the right"
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        ) {
            OutlinedButton(onClick = {
                onDismiss()
            }, shape = RoundedCornerShape(10.dp), modifier = Modifier.widthIn(min = 150.dp)) {
                Text(text = stringResource(id = R.string.cancel))
            }
            Button(onClick = {
                save(Category(category.id, categoryName, categoryColor))
            }, shape = RoundedCornerShape(10.dp), modifier = Modifier.widthIn(min = 150.dp)) {
                Text(
                    text = stringResource(
                        id = when (action) {
                            Action.CREATE -> R.string.create_button
                            Action.UPDATE -> R.string.update_button
                        }
                    )
                )
            }
        }

    }
}


@Composable
private fun CategoryColorSelector(
    colorList: List<Color>, value: Color, onColorChange: (Color) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        items(items = colorList, itemContent = { color ->
            val optionSelected = color == value
            CategoryColorItem(color, optionSelected, onClick = { onColorChange(color) })
        })
    }
}

enum class Action {
    UPDATE, CREATE
}

@Composable
private fun CategoryColorItem(
    color: Color, isSelected: Boolean, onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(color = color)
                .selectable(
                    role = Role.RadioButton, selected = isSelected, onClick = onClick
                )
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background)
            )
        }
    }
}

private fun createEmptyCategory(): Category {
    return Category(name = "", color = CategoryColors.values()[0].value.toArgb())
}