package nau.android.taskify.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskifyTimePickerDialog(
    initialMinute: Int,
    initialHour: Int,
    onDismiss: () -> Unit,
    onConfirm: (Triple<Int, Int, Boolean>) -> Unit
) {
    val timePickerState =
        rememberTimePickerState(initialHour = initialHour, initialMinute = initialMinute)

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = {

        }, modifier = Modifier
            .fillMaxWidth(0.75f)
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(5.dp)
            )
    ) {

        Column(Modifier.padding(20.dp)) {
            TimeInput(
                state = timePickerState,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextButton(onClick = {
                    onDismiss()
                }) {
                    Text(text = "Cancel", fontSize = 16.sp)
                }
                TextButton(onClick = {
                    onConfirm(Triple(timePickerState.hour, timePickerState.minute, true))
                }) {
                    Text(text = "Done", fontSize = 16.sp)
                }

            }
        }
    }
}
