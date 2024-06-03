package com.example.android.architecture.blueprints.todoapp

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.tasks.TasksActivity
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** An E2E test:
 * End to end tests mimic how the complete app runs and simulate real usage.  */
@RunWith(AndroidJUnit4::class)
@LargeTest // which signifies these are end-to-end tests, testing a large portion of the code.
class TasksActivityTest {

    private lateinit var repository: TasksRepository

    @Before
    fun setup() {
        repository = ServiceLocator.provideTasksRepository(getApplicationContext())
        runTest {
            repository.deleteAllTasks()
        }
    }
    @After
    fun reset() {
        ServiceLocator.resetRepository()
    }

    /*
    basic E2E test structure of an activity
    @Test
fun editTask() = runBlocking {
    // Set initial state.
    repository.saveTask(Task("TITLE1", "DESCRIPTION"))

    // Start up Tasks screen.
    val activityScenario = ActivityScenario.launch(TasksActivity::class.java)


    // Espresso code will go here.


    // Make sure the activity is closed before resetting the db:
    activityScenario.close()
} */

    /** Note that in this end-to-end test, you don't verify integration with the repository, the navigation controller, or any other components at all.
     * This is what's known as a black box test. The test is not supposed to know how things are implemented internally, only the outcome for a given
     * input. */
    @Test
    fun editTask() = runTest { // is used to wait for all suspend functions to finish before continuing with the execution in the block
        // Set initial state.
        // You must set the initial state of the data layer (such as adding tasks to the repository) before calling ActivityScenario.launch().
        val descriptionText = "DESCRIPTION"
        val titleText = "TITLE1"
        repository.saveTask(Task(titleText, descriptionText))

        // Start up Tasks screen.
        /* ActivityScenario is an AndroidX Testing library class that wraps around an activity and gives you direct control over the activity's lifecycle
         for testing. It is similar to FragmentScenario. */
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)


        // Click on the task on the list and verify that all the data is correct
        onView(withText(titleText)).perform(click())
        onView(withId(R.id.task_detail_title_text)).check(matches(withText(titleText)))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText(descriptionText)))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(not(isChecked())))

        // Click on the edit button, edit and save
        onView(withId(R.id.edit_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text)).perform(replaceText("NEW TITLE"))
        onView(withId(R.id.add_task_description_edit_text)).perform(replaceText("NEW DESCRIPTION"))
        onView(withId(R.id.save_task_fab)).perform(click())

        // Verify task is displayed on screen in the task list
        onView(withText("NEW TITLE")).check(matches(isDisplayed()))
        // Verify previous task is not displayed
        onView(withText(titleText)).check(doesNotExist())

        // Make sure the activity is closed before resetting the db:
        activityScenario.close()
    }
}