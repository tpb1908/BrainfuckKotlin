package com.tpb.brainfuck.runner

import android.content.Intent
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import com.tpb.brainfuck.R
import com.tpb.brainfuck.db.Program
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import org.junit.Rule
import org.junit.Test
import java.util.*

/**
 * Created by theo on 07/07/17.
 */
class InputTest {

    @JvmField @Rule
    var activityTestRule = ActivityTestRule(Runner::class.java, true, false)

    val program = Program(source = ",.")

    val intent: Intent = Intent()

    init {
        intent.putExtra("parcel_program", program)
        intent.putExtra("start_immediately", true)
    }

    @Test fun testCharacterInput() {
        activityTestRule.launchActivity(intent)

        val char = (65 + Random().nextInt(62)).toChar()

        onView(withId(R.id.input_edittext)).perform(typeText(char.toString()))
        onView(withId(R.id.input_button)).perform(click())
        onView(allOf(withId(R.id.output), withText(containsString(char.toString())))).check(matches(isDisplayed()))
    }

    @Test fun testIntegerInput() {
        activityTestRule.launchActivity(intent)

        val num = 65 + Random().nextInt(62)
        val char = num.toChar()

        onView(withId(R.id.input_edittext)).perform(typeText(num.toString()))
        onView(withId(R.id.input_button)).perform(click())
        onView(allOf(withId(R.id.output), withText(containsString(char.toString())))).check(matches(isDisplayed()))
    }

}