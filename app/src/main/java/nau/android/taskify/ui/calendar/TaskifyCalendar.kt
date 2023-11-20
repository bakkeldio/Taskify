package nau.android.taskify.ui.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nau.android.taskify.FloatingActionButton
import nau.android.taskify.ui.dialogs.CalendarGrid
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskifyCalendar() {


    val selectedDate by remember {
        mutableStateOf(Calendar.getInstance())
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "arrow left",
                        Modifier.size(28.dp)
                    )
                    Text(text = "Date")
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "arrow right",
                        Modifier.size(28.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )
    }, floatingActionButton = {
        FloatingActionButton {

        }
    }, contentWindowInsets = WindowInsets(bottom = 0.dp)) {
        Column(Modifier.padding(it)) {
            CalendarGrid(Calendar.getInstance(), {

            })
        }
    }

}