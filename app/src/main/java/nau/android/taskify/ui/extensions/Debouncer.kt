package nau.android.taskify.ui.extensions

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@ViewModelScoped
class Debouncer @Inject constructor() {

    private var job: Job? = null

    operator fun invoke(coroutineScope: CoroutineScope, function: suspend () -> Unit) {
        job?.cancel()
        job = coroutineScope.launch {
            delay(500)
            function()
        }
    }
}