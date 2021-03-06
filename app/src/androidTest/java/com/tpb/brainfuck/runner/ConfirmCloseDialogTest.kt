package com.tpb.brainfuck.runner


import android.content.Intent
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import com.tpb.brainfuck.R
import com.tpb.brainfuck.db.Program
import org.hamcrest.Matchers.allOf
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConfirmCloseDialogTest {

    @JvmField @Rule
    var activityTestRule = ActivityTestRule(Runner::class.java, true, false)

    val program =  Program(source = ">++++++++++>>>+>+[>>>+[-[<<<<<[+<<<<<]>>[[-]>[<<+>+>-]<[>+<-]<[>+<-[>+<-[>\n" +
                    "+<-[>+<-[>+<-[>+<-[>+<-[>+<-[>+<-[>[-]>>>>+>+<<<<<<-[>+<-]]]]]]]]]]]>[<+>-\n" +
                    "]+>>>>>]<<<<<[<<<<<]>>>>>>>[>>>>>]++[-<<<<<]>>>>>>-]+>>>>>]<[>++<-]<<<<[<[\n" +
                    ">+<-]<<<<]>>[->[-]++++++[<++++++++>-]>>>>]<<<<<[<[>+>+<<-]>.<<<<<]>.>>>>]"
    ) //Non-ending program

    val intent: Intent = Intent()

    init {
        intent.putExtra("parcel_program", program)
    }

    @Test fun testConfirmCloseDialogShown() {
        activityTestRule.launchActivity(intent)
        onView(allOf<View>(withId(R.id.play_pause_button), isDisplayed()))
                .perform(click())
                .perform(click())

        pressBack()

        onView(withText("Confirm")).check(ViewAssertions.matches(isDisplayed()))
    }

    @Test fun testConfirmCloseDialogCancel() {
        activityTestRule.launchActivity(intent)
        onView(allOf<View>(withId(R.id.play_pause_button), isDisplayed()))
                .perform(click())
                .perform(click())

        pressBack()

        onView(withText("Confirm")).check(ViewAssertions.matches(isDisplayed()))

        onView(withText("Cancel")).perform(click())


        assertFalse(activityTestRule.activity.isFinishing)
    }

    @Test fun testConfirmCloseDialogOK() {
        activityTestRule.launchActivity(intent)
        onView(allOf<View>(withId(R.id.play_pause_button), isDisplayed()))
                .perform(click())
                .perform(click())

        pressBack()

        onView(withText("Confirm")).check(ViewAssertions.matches(isDisplayed()))

        onView(withText("OK")).perform(click())

        assertTrue(activityTestRule.activity.isFinishing)
    }


}
