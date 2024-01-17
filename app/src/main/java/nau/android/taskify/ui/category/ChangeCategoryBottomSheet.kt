package nau.android.taskify.ui.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import nau.android.taskify.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nau.android.taskify.ui.category.viewModel.CategoriesViewModel
import nau.android.taskify.ui.searchBars.SmallerSearchBar
import nau.android.taskify.ui.model.Category


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeCategoryBottomSheet(
    categoriesViewModel: CategoriesViewModel = hiltViewModel(),
    currentCategoryId: Long? = null,
    onCategoryChanged: (Category) -> Unit,
    sheetState: SheetState,
    hideBottomSheet: () -> Unit
) {

    val categoriesState = categoriesViewModel.getCategories()
        .collectAsStateWithLifecycle(initialValue = CategoriesListState.Loading)


    ModalBottomSheet(onDismissRequest = {
        hideBottomSheet()
    }, sheetState = sheetState, modifier = Modifier.fillMaxSize()) {
        when (val result = categoriesState.value) {
            is CategoriesListState.Success -> CategoriesStateSuccess(
                currentCategoryId,
                result.categories
            ) {
                onCategoryChanged(it)
            }

            is CategoriesListState.Error -> {

            }

            is CategoriesListState.Empty -> {

            }

            is CategoriesListState.Loading -> {

            }
        }
    }
}

@Composable
fun CategoriesStateSuccess(
    currentCategoryId: Long?,
    categories: List<Category>,
    onCategoryChanged: (Category) -> Unit
) {

    var searchQuery by remember {
        mutableStateOf("")
    }

    Column(modifier = Modifier.padding(horizontal = 13.dp)) {
        Text(
            text = "Move to",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )

        SmallerSearchBar(
            modifier = Modifier.padding(top = 15.dp),
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query

                /*
                categoriesState = categories.filter {
                    it.name.contains(searchQuery, ignoreCase = true)
                }
                 */
            })
        LazyColumn(
            modifier = Modifier
                .padding(vertical = 15.dp)
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(10.dp)
                )
        ) {
            items(categories) {
                CategoryItem(
                    category = it,
                    currentCategoryId = currentCategoryId,
                    categoryChanged = onCategoryChanged
                )
            }
        }
    }
}

@Composable
fun CategoryItem(
    category: Category,
    currentCategoryId: Long?,
    categoryChanged: (Category) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp)
    ) {
        Row(modifier = Modifier.align(Alignment.CenterStart)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_category),
                contentDescription = null,
                tint = Color(category.color)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = category.name)
        }
        RadioButton(
            selected = category.id == currentCategoryId, onClick = {
                categoryChanged(category)
            }, modifier = Modifier.align(Alignment.CenterEnd), colors = RadioButtonDefaults.colors(
                unselectedColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}