package nau.android.taskify.ui.task

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import nau.android.taskify.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsBottomSheet(
    modalBottomSheetState: SheetState,
    onDismissRequest: () -> Unit = {},
    deleteTask: () -> Unit = {},
    openAttachment: () -> Unit = {},
    share: () -> Unit = {}
) {

    ModalBottomSheet(
        onDismissRequest = {
            onDismissRequest()
        }, sheetState = modalBottomSheetState
    ) {

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 13.dp, end = 13.dp, top = 10.dp)
            .clickable {
                share()
            }) {
            Icon(
                imageVector = Icons.Default.Share, contentDescription = null
            )
            Text(text = "Share with others", modifier = Modifier.padding(start = 10.dp))
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 13.dp, end = 13.dp, top = 20.dp)
            .clickable {
                openAttachment()
            }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_attach), contentDescription = null
            )
            Text(
                text = "Attachment", modifier = Modifier.padding(
                    start = 10.dp
                )
            )
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 13.dp, end = 13.dp, top = 20.dp)
            .clickable {
                deleteTask()
            }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_delete), contentDescription = null
            )
            Text(text = "Delete this task", modifier = Modifier.padding(start = 10.dp))
        }
        TextButton(
            onClick = {
                onDismissRequest()
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 20.dp)
                .fillMaxWidth(0.5f)
        ) {
            Text(text = "Cancel", style = MaterialTheme.typography.titleMedium)
        }

    }
}