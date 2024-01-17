package nau.android.taskify.ui.category

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nau.android.taskify.FloatingActionButton
import nau.android.taskify.R
import nau.android.taskify.ui.MainDestination
import nau.android.taskify.ui.category.viewModel.CategoriesViewModel
import nau.android.taskify.ui.customElements.NoCategories
import nau.android.taskify.ui.model.Category
import nau.android.taskify.ui.searchBars.TaskifySearchBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesList(
    mainDestination: MainDestination,
    navigateToCategoryTasksList: (Long) -> Unit,
    categoriesViewModel: CategoriesViewModel = hiltViewModel()
) {

    val categoriesState = categoriesViewModel.getCategories().collectAsStateWithLifecycle(
        initialValue = CategoriesListState.Loading
    )

    var categoryBottomSheetOpen by remember {
        mutableStateOf(false)
    }

    var categoryId by remember {
        mutableStateOf<Long>(0L)
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(text = mainDestination.title)
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )
    }, floatingActionButton = {
        FloatingActionButton {
            categoryBottomSheetOpen = true
        }
    }, contentWindowInsets = WindowInsets(bottom = 0.dp)) { paddingValues ->

        if (categoryBottomSheetOpen) {
            CategoryBottomSheet(onDismiss = {
                categoryBottomSheetOpen = false
            }, categoryId = categoryId)
        }

        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            TaskifySearchBar(onValueChange = {

            })
            when (val result = categoriesState.value) {
                is CategoriesListState.Success -> {
                    CategoriesLoaded(
                        categories = result.categories,
                        onCategoryClicked = navigateToCategoryTasksList,
                        onCategoryLongClicked = {
                            categoryId = it
                            categoryBottomSheetOpen = true
                        }
                    )
                }

                is CategoriesListState.Empty -> {
                    NoCategories()
                }

                else -> Unit
            }
        }

    }

}

@Composable
fun CategoriesLoaded(
    categories: List<Category>,
    onCategoryClicked: (Long) -> Unit,
    onCategoryLongClicked: (Long) -> Unit
) {
    LazyColumn(
        content = {
            items(categories, key = {
                it.id
            }) {
                CategoryItem(
                    category = it,
                    onCategoryClicked = onCategoryClicked,
                    onLongClickCategory = onCategoryLongClicked
                )
            }
        },
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.padding(start = 13.dp, end = 13.dp, top = 13.dp)
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryItem(
    category: Category,
    onCategoryClicked: (Long) -> Unit,
    onLongClickCategory: (Long) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    onCategoryClicked(category.id)
                }, onLongClick = {
                    onLongClickCategory(category.id)
                }, indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                }
            ),
        color = MaterialTheme.colorScheme.surface
    ) {

        Row(modifier = Modifier.padding(13.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_category),
                contentDescription = "Category",
                tint = Color(category.color)
            )
            Text(
                text = category.name,
                modifier = Modifier.padding(start = 20.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

    }
}