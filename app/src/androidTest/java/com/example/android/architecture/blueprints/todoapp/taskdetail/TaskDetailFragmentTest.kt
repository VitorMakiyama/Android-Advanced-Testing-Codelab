package com.example.android.architecture.blueprints.todoapp.taskdetail

import FakeAndroidTestRepository
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.MediumTest
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.ServiceLocator
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import kotlinx.coroutines.test.runTest
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** Fragments (at least the ones you'll be testing) are visual and make up the user interface. Because of this, when testing them,
 * it helps to render them on a screen, as they would when the app is running. Thus when testing fragments, you usually write
 * instrumented tests, which live in the androidTest source set.
 * As a general rule of thumb, if you are testing something visual, run it as an instrumented test.
 */

@MediumTest /* Marks the test as a "medium run-time" integration test (versus @SmallTest unit tests and @LargeTest end-to-end tests).
               This helps you group and choose which size of test to run. */
@RunWith(JUnit4::class)
class TaskDetailFragmentTest {

    lateinit var repository: TasksRepository

    @Before
    fun initRepository() {
        repository = FakeAndroidTestRepository()
        ServiceLocator.tasksRepository = repository
    }

    @After
    fun cleanUpDb() = runTest {
        ServiceLocator.resetRepository()
    }

    @Test
    fun activeTaskDetail_DisplayedInUi() = runTest {
        // GIVEN - Add active (incomplete) task to the DB
        val activeTask = Task("Active Task", "AndroidX Rocks", false)
        repository.saveTask(activeTask)

        // WHEN - Details fragment launched to display task
        val bundle = TaskDetailFragmentArgs(activeTask.id).toBundle() // represents the fragment arguments for the task that get passed into the fragment
        /* Supplying the theme is necessary because fragments usually get their theming from their parent activity. When using FragmentScenario, your fragment
         is launched inside a generic empty activity so that it's properly isolated from activity code (you are just testing the fragment code,
         not the associated activity). The theme parameter allows you to supply the correct theme. */
        launchFragmentInContainer<TaskDetailFragment>(bundle, R.style.AppTheme) // function creates a FragmentScenario, with this bundle and a theme.

        // THEN - Task details are displayed on the screen
        // make sure that the title/description are both shown and correct
        onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_title_text)).check(matches(withText("Active Task")))
        onView(withId(R.id.task_detail_description_text)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText("AndroidX Rocks")))
        // and make sure the "active" checkbox is shown unchecked
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(not(isChecked())))
    }

    @Test
    fun completedTaskDetails_DisplayedInUni() = runTest {
        // GIVEN - Add completed task to the DB
        val titleString = "Completed Task"
        val descriptionString = "Completed Task is completed"
        val completedTask = Task(titleString, descriptionString, true)
        repository.saveTask(completedTask)

        // WHEN - Details fragment launched to display task
        val bundle = TaskDetailFragmentArgs(completedTask.id).toBundle()
        launchFragmentInContainer<TaskDetailFragment>(bundle, R.style.AppTheme)

        // THEN - Task details are displayed on the screen
        // make sure that the title/description are both shown and correct
        onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_title_text)).check(matches(withText(titleString)))
        onView(withId(R.id.task_detail_description_text)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText(descriptionString)))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(isChecked()))
    }
}
