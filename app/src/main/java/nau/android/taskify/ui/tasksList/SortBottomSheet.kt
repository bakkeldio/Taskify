package nau.android.taskify.ui.tasksList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import nau.android.taskify.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    groupingType: GroupingType,
    sortingType: SortingType,
    groupingTypeChanged: (GroupingType) -> Unit,
    sortingTypeChanged: (SortingType) -> Unit,
    dismissRequest: () -> Unit
) {

    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { dismissRequest() },
        sheetState = modalBottomSheetState
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
        ) {
            Text(
                text = stringResource(id = R.string.group_by),
                fontWeight = FontWeight.Medium
            )

            LazyVerticalGrid(columns = GridCells.Fixed(3), content = {
                items(GroupingType.values()) { type ->
                    FilterChip(selected = groupingType == type, onClick = {
                        groupingTypeChanged(type)
                    }, label = {
                        Text(
                            text = type.name,
                            modifier = Modifier.fillMaxWidth(0.9f),
                            textAlign = TextAlign.Center
                        )
                    })
                }
            }, horizontalArrangement = Arrangement.spacedBy(25.dp))

            Text(
                text = stringResource(id = R.string.sort_by),
                modifier = Modifier.padding(top = 15.dp),
                fontWeight = FontWeight.Medium
            )

            LazyVerticalGrid(columns = GridCells.Fixed(3), content = {
                items(SortingType.values()) { type ->
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