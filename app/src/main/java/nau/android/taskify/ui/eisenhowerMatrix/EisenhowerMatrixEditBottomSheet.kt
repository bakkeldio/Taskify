package nau.android.taskify.ui.eisenhowerMatrix

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import nau.android.taskify.R
import nau.android.taskify.ui.extensions.noRippleClickable
import nau.android.taskify.ui.theme.TaskifyTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatrixEditBottomSheet(
    editEisenhowerMatrixEditViewModel: EisenhowerMatrixEditViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {

    LaunchedEffect(Unit) {
        editEisenhowerMatrixEditViewModel.getAllConfigurations()
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val coroutineScope = rememberCoroutineScope()


    val configurations = editEisenhowerMatrixEditViewModel.matrixConfigurations.observeAsState()

    var selectedEisenhowerMatrixQuadrant by remember {
        mutableStateOf(EisenhowerMatrixQuadrant.IMPORTANT_URGENT)
    }

    var showMatrixQuadrantRulesBottomSheet by remember {
        mutableStateOf(false)
    }

    if (showMatrixQuadrantRulesBottomSheet) {
        val configuration = configurations.value?.get(selectedEisenhowerMatrixQuadrant) ?: return
        MatrixQuadrantRules(
            configuration = configuration,
            eisenhowerMatrixQuadrant = selectedEisenhowerMatrixQuadrant
        ) {
            showMatrixQuadrantRulesBottomSheet = false
        }
    }
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxHeight()
    ) {
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
                text = stringResource(id = R.string.maxtix_edit),
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

        Spacer(modifier = Modifier.height(5.dp))

        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            modifier = Modifier.padding(start = 10.dp, end = 10.dp)
        ) {

            Spacer(modifier = Modifier.height(15.dp))

            MatrixItem(eisenhowerMatrixQuadrant = EisenhowerMatrixQuadrant.IMPORTANT_URGENT,
                onMatrixItemClick = {
                    selectedEisenhowerMatrixQuadrant = EisenhowerMatrixQuadrant.IMPORTANT_URGENT
                    showMatrixQuadrantRulesBottomSheet = true
                })

            Spacer(modifier = Modifier.height(20.dp))
            MatrixItem(eisenhowerMatrixQuadrant = EisenhowerMatrixQuadrant.NOT_URGENT_IMPORTANT,
                onMatrixItemClick = {
                    selectedEisenhowerMatrixQuadrant = EisenhowerMatrixQuadrant.NOT_URGENT_IMPORTANT
                    showMatrixQuadrantRulesBottomSheet = true
                })

            Spacer(modifier = Modifier.height(20.dp))

            MatrixItem(eisenhowerMatrixQuadrant = EisenhowerMatrixQuadrant.URGENT_UNIMPORTANT,
                onMatrixItemClick = {
                    selectedEisenhowerMatrixQuadrant = EisenhowerMatrixQuadrant.URGENT_UNIMPORTANT
                    showMatrixQuadrantRulesBottomSheet = true
                })

            Spacer(modifier = Modifier.height(20.dp))

            MatrixItem(eisenhowerMatrixQuadrant = EisenhowerMatrixQuadrant.NOT_URGENT_UNIMPORTANT,
                onMatrixItemClick = {
                    selectedEisenhowerMatrixQuadrant =
                        EisenhowerMatrixQuadrant.NOT_URGENT_UNIMPORTANT
                    showMatrixQuadrantRulesBottomSheet = true
                })
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}

@Composable
fun MatrixItem(eisenhowerMatrixQuadrant: EisenhowerMatrixQuadrant, onMatrixItemClick: () -> Unit) {
    Row(modifier = Modifier
        .padding(horizontal = 15.dp)
        .fillMaxWidth()
        .noRippleClickable {
            onMatrixItemClick()
        }) {

        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    eisenhowerMatrixQuadrant.color, RoundedCornerShape(50.dp)
                )
                .padding(5.dp), contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = eisenhowerMatrixQuadrant.icon),
                contentDescription = null,
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = stringResource(id = eisenhowerMatrixQuadrant.titleR),
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterVertically),
            tint = MaterialTheme.colorScheme.outline
        )
    }
}


@Composable
@Preview(showBackground = true)
fun MatrixItemPreview() {
    TaskifyTheme {
        MatrixItem(eisenhowerMatrixQuadrant = EisenhowerMatrixQuadrant.URGENT_UNIMPORTANT, {})
    }
}