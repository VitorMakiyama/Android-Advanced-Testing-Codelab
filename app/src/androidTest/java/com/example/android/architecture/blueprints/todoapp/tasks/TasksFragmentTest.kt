package com.example.android.architecture.blueprints.todoapp.tasks

import FakeAndroidTestRepository
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.ServiceLocator
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
class TasksFragmentTest {

    private lateinit var repository: TasksRepository

    @Before
    fun initRepository() {
        repository = FakeAndroidTestRepository()
        ServiceLocator.tasksRepository = repository
    }

    @After
    fun cleanupDb() = runTest {
        /** Differences between runBlocking and runTest:
         * runTest:
         *  1 - It skips delay, so your tests run faster.
         *  2 -It adds testing related assertions to the end of the coroutine.These assertions fail if you
         *  launch a coroutine and it continues running after the end of the runBlocking lambda (which is a
         *  possible coroutine leak) or if you have an uncaught exception.
         *  3 - It gives you timing control over the coroutine execution.
         *  runBlocking: should be used to block the current thread, for example: to create test doubles
         *  that will be used in test cases
        * */
        ServiceLocator.resetRepository()
    }

    @Test
    fun cliclTask_navigateToDetailFragmentOne() = runTest {
        repository.saveTask(Task("TITLE1", "DESCRIPTION1", false, "id1"))
        repository.saveTask(Task("TITLE2", "DESCRIPTION2", true, "id2"))
        val navController = mock(NavController::class.java) // mocks the navController

        //GIVEN - On the home screen
        val scenario = launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)
        scenario.onFragment { // sets the navController to the mocked one
            Navigation.setViewNavController(it.view!!, navController)
        }

        //WHEN - Click on the first list item
        onView(withId(R.id.tasks_list))
            .perform(RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>( // RecyclerViewActions is part of the espresso-contrib library and lets you perform Espresso actions on a RecyclerView.
                hasDescendant(withText("TITLE1")), click()))

        //THEN - Verify that we navigate tro the first detail screen
        verify(navController).navigate(
            TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment("id1")
        )
    }

    @Test
    fun clickAddTaskButton_navigateToEditFragment() {
        val navController = mock(NavController::class.java) // mocks the navController

        //GIVEN - On the home screen
        val scenario = launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)
        scenario.onFragment { // sets the navController to the mocked one
            Navigation.setViewNavController(it.view!!, navController)
        }

        //WHEN - Click on the first list item
        onView(withId(R.id.add_task_fab)).perform(click())

        //THEN - Verify that we navigate tro the first detail screen
        verify(navController).navigate(
            TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(null, getApplicationContext<Context>().getString(R.string.add_task))
        )
    }
}