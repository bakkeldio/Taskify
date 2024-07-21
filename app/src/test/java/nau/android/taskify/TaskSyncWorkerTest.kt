package nau.android.taskify

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import nau.android.taskify.data.database.TaskDatabase
import nau.android.taskify.data.model.Task
import nau.android.taskify.data.repository.TaskSyncWorker
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import javax.inject.Inject

@RunWith(JUnit4::class)
@HiltAndroidTest
class TaskSyncWorkerTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var workerParams: WorkerParameters

    @Inject
    lateinit var tasksDatabase: TaskDatabase

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testDoWork_success() = runBlocking {
        // Set up test data
        val taskDao = tasksDatabase.taskDao()
        val task = Task(1, "Finish the project", "1")
        taskDao.insertTask(task)

        // Mock Firestore
        val collectionRef = mock(CollectionReference::class.java)
        `when`(firestore.collection("tasks")).thenReturn(collectionRef)
        `when`(collectionRef.document(task.id.toString()).set(task)).thenReturn(mock())

        // Test doWork()
        val result = TaskSyncWorker(context, workerParams, tasksDatabase, firestore).doWork()

        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun testDoWork_failure() = runBlocking {
        // Set up test data
        val taskDao = tasksDatabase.taskDao()
        val task = Task(2, "name", "description")
        taskDao.insertTask(task)

        // Mock Firestore to throw an exception
        val collectionRef = mock(CollectionReference::class.java)
        `when`(firestore.collection("tasks")).thenReturn(collectionRef)
        `when`(collectionRef.document(task.id.toString()).set(task)).thenThrow(RuntimeException("Test exception"))

        // Test doWork()
        val result = TaskSyncWorker(context, workerParams, tasksDatabase, firestore).doWork()

        assertEquals(ListenableWorker.Result.retry(), result)
    }
}

