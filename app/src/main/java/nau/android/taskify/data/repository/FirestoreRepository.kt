package nau.android.taskify.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import nau.android.taskify.data.model.Task

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun syncTasks(tasks: List<Task>) {

        val tasksCollection = db.collection("tasks")

        try {
            tasks.forEach { task ->
                tasksCollection.document(task.id.toString()).set(task).await()
            }
        } catch (e: Exception) {
            println(e.localizedMessage)
        }
    }
    /*

    suspend fun fetchTasks(): List<Task> {
        // Logic to fetch tasks from Firestore

    }

     */
}