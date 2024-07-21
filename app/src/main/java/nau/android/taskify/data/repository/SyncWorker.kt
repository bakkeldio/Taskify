package nau.android.taskify.data.repository

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import nau.android.taskify.data.database.TaskDatabase
import javax.inject.Inject

@HiltWorker
class TaskSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val tasksDatabase: TaskDatabase,
    private val firestore: FirebaseFirestore
) : CoroutineWorker(context, workerParams) {


    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val taskDao = tasksDatabase.taskDao()
            val tasks = taskDao.getAllTasksForTesting()
            // Assuming a "tasks" collection in Firestore
            val collectionRef = firestore.collection("tasks")

            tasks.forEach { task ->
                // Add or update the task on Firestore
                // To ensure idempotency, you can use set() with merge:true or write custom logic with transactions
                collectionRef.document(task.id.toString()).set(task).await()
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}