package nau.android.taskify.ui.profile

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import nau.android.taskify.R
import nau.android.taskify.ui.customElements.TaskifyArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserTaskStatistics() {


    Scaffold(topBar =
    {
        CenterAlignedTopAppBar(title = {
            Text(text = stringResource(id = R.string.profile_info))
        }, navigationIcon = {
            TaskifyArrowBack {

            }
        })
    }) {  paddingValues ->

    }
}