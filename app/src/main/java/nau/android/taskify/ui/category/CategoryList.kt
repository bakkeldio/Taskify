package nau.android.taskify.ui.category

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
import nau.android.taskify.ui.extensions.noRippleClickable
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
            })
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
                        onCategoryClicked = navigateToCategoryTasksList
                    )
                }

                else -> Unit
            }
        }

    }

}

@Composable
fun CategoriesLoaded(categories: List<Category>, onCategoryClicked: (Long) -> Unit) {
    LazyColumn(
        content = {
            items(categories, key = {
                it.id
            }) {
                CategoryItem(category = it, onCategoryClicked = onCategoryClicked)
            }
        },
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.padding(start = 13.dp, end = 13.dp, top = 13.dp)
    )
}


@Composable
fun CategoryItem(category: Category, onCategoryClicked: (Long) -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable {
                onCategoryClicked(category.id)
            },
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