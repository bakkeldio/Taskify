package nau.android.taskify.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import nau.android.taskify.data.FakeTasksData
import java.util.logging.Filter


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SortBottomSheet(
    groupingType: GroupingType,
    sortingType: SortingType,
    groupingTypeChanged: (GroupingType) -> Unit,
    sortingTypeChanged: (SortingType) -> Unit,
    fakeTasksData: FakeTasksData,
    dismissRequest: () -> Unit
) {


    val modalBottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        modifier = Modifier.fillMaxHeight(0.35f),
        onDismissRequest = { dismissRequest() },
        sheetState = modalBottomSheetState
    ) {

        val groupingList = fakeTasksData.getGroupingTypes()

        val sortingTypes = fakeTasksData.getSortingTypes()


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
        ) {
            Text(text = "Group by")

            LazyVerticalGrid(columns = GridCells.Fixed(3), content = {
                items(groupingList) { type ->
                    FilterChip(selected = groupingType == type, onClick = {
                        groupingTypeChanged(type)
                    }, label = {
                        Text(
                            text = type.name,
                            modifier = Modifier.fillMaxWidth(0.9f),
                            textAlign = TextAlign.Center
                        )
                    }
                    )
                }
            }, horizontalArrangement = Arrangement.spacedBy(25.dp))

            Text(text = "Sort by", modifier = Modifier.padding(top = 15.dp))

            LazyVerticalGrid(columns = GridCells.Fixed(3), content = {
                items(sortingTypes) { type ->
                    FilterChip(selected = sortingType == type, onClick = {
                        sortingTypeChanged(type)
                    }, label = {
                        Text(
                            text = type.name,
                            modifier = Modifier.fillMaxWidth(0.9f),
                            textAlign = TextAlign.Center
                        )
                    })
                }
            }, horizontalArrangement = Arrangement.spacedBy(25.dp))
        }
    }
}

enum class GroupingType {
    Priority, Category, Date, None
}

enum class SortingType {
    Date, Title, Priority
}