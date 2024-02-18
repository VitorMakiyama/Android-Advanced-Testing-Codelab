package com.example.android.architecture.blueprints.todoapp.statistics

import com.example.android.architecture.blueprints.todoapp.data.Task
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class StatisticsUtilsTest {
    @Test
    fun getActiveAndCompletedStats_noCompleted_returnsHundredZero() {
        // create an active task
        val tasks = listOf<Task>(
            Task("title", "desc", isCompleted = false)
        )
        // call your function
        val result = getActiveAndCompletedStats(tasks)
        // check the result
        assertThat(result.completedTasksPercent, `is`(0f))
        assertThat(result.activeTasksPercent, `is`(100f))
    }

    @Test
    fun getActiveAndCompletedStats_noActive_returnsZeroHundred() {
        // Given 1 completed (not active) tasks
        val tasks = listOf<Task>(
            Task("title", "desc", isCompleted = true)
        )
        // When the list of tasks is computed
        val result = getActiveAndCompletedStats(tasks)
        // Then the result is 100-0
        assertThat(result.completedTasksPercent, `is`(100f))
        assertThat(result.activeTasksPercent, `is`(0f))
    }

    @Test
    fun getActiveAndCompletedStats_twoCompletedThreeActive_returnsFortySixty() {
        // Given 2 completed tasks and 2 active tasks
        val tasks = listOf<Task>(
            Task("title", "desc", isCompleted = true),
            Task("title", "desc", isCompleted = true),
            Task("title", "desc", isCompleted = false),
            Task("title", "desc", isCompleted = false),
            Task("title", "desc", isCompleted = false),
        )
        // When the list of tasks is computed
        val result = getActiveAndCompletedStats(tasks)
        // Then the results is 40-60
        assertThat(result.completedTasksPercent, `is`(40f))
        assertThat(result.activeTasksPercent, `is`(60f))
    }

    @Test
    fun getActiveAndCompletedStats_empty_returnsZeros() {
        // When there's an empty list
        val result = getActiveAndCompletedStats(emptyList())
        // Both active and completed tasks are 0
        assertThat(result.completedTasksPercent, `is`(0f))
        assertThat(result.activeTasksPercent, `is`(0f))
    }

    @Test
    fun getActiveAndCompletedStats_error_returnsZeros() {
        // When there's an error loading tasks
        val result = getActiveAndCompletedStats(null)
        // Both active and completed tasks are 0
        assertThat(result.completedTasksPercent, `is`(0f))
        assertThat(result.activeTasksPercent, `is`(0f))
    }
}
