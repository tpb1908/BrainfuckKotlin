package com.tpb.brainfuck.home


import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.swipeRight
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import android.view.View
import com.tpb.brainfuck.R
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.greaterThan
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SwipeDismissTest {

    @JvmField @Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test fun testSwipeToDelete() {
        val initialCount = activityTestRule.activity.adapter.itemCount
        assertThat("Must have items in adapter", initialCount, greaterThan(0))

        onView(allOf<View>(withId(R.id.program_recycler),
                        withParent(allOf<View>(withId(R.id.coordinator),
                                withParent(withId(android.R.id.content))
                        )),
                        isDisplayed()
                )).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeRight()))

        assertEquals(initialCount - 1, activityTestRule.activity.adapter.itemCount)
    }

    @Test fun testSnackBarRestore() {
        val initialCount = activityTestRule.activity.adapter.itemCount
        assertThat("Must have items in adapter", initialCount, greaterThan(0))

        onView(allOf<View>(withId(R.id.program_recycler),
                        withParent(allOf<View>(withId(R.id.coordinator),
                                withParent(withId(android.R.id.content))
                        )),
                        isDisplayed()
                )).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeRight()))

        onView(allOf<View>(withId(R.id.snackbar_action), withText("UNDO"))).perform(click())

        assertEquals(initialCount, activityTestRule.activity.adapter.itemCount)
    }

}