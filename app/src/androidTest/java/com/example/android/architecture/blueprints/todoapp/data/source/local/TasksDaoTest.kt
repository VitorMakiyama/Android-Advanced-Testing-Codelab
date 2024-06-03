package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Note that in general, make database tests instrumented tests, meaning they will be in the androidTest source set.
 * This is because if you run these tests locally, they will use whatever version of SQLite you have on your local machine,
 * which could be very different from the version of SQLite that ships with your Android device! Different Android devices
 * also ship with different SQLite versions, so it's helpful as well to be able to run these tests as instrumented tests
 * on different devices.
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest /* This helps you group and choose which size test to run. DAO tests are considered unit tests since you are only
                testing the DAO, thus you can call them small tests. */
class TasksDaoTest {
    // Executes each task synchronously using Architecture Components
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: ToDoDatabase

    @Before
    fun initDb() {
        /* Using an in-memory database so that the information stored here disappears when the process
        * is killed */
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            ToDoDatabase::class.java
        ).build()
    }
    @After
    fun closeDb() = database.close()

    @Test
    fun insertTaskAndGetById() = runTest {
        // GIVEN - Insert task
        val task = Task("title", "description")
        database.taskDao().insertTask(task)

        // WHEN - Get the task by id from the database
        val loaded = database.taskDao().getTaskById(task.id)

        //THEN - The loaded data contains the expected values
        assertThat<Task>(loaded as Task, notNullValue())
        assertThat(loaded.id, `is`(task.id))
        assertThat(loaded.title, `is`(task.title))
        assertThat(loaded.description, `is`(task.description))
        assertThat(loaded.isCompleted, `is`(task.isCompleted))
    }

    @Test
    fun updateTaskAndGetById() = runTest {
        // 1. Insert a task into the DAO.
        val task = Task("title", "description")
        database.taskDao().insertTask(task)

        // 2. Update the task by creating a new task with the same ID but different attributes.
        val taskToBeUpdated = Task("updatedTitle", "updatedDescription", true, task.id)
        database.taskDao().updateTask(taskToBeUpdated)

        // 3. Check that when you get the task by its ID, it has the updated values.
        val updatedTask = database.taskDao().getTaskById(task.id)
        assertThat(updatedTask as Task, notNullValue())
        assertThat(updatedTask.id, `is`(task.id))
        assertThat(updatedTask.title, `is`(taskToBeUpdated.title))
        assertThat(updatedTask.description, `is`(taskToBeUpdated.description))
        assertThat(updatedTask.isCompleted, `is`(taskToBeUpdated.isCompleted))
    }
}