package com.tpb.brainfuck.home


import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.test.suitebuilder.annotation.LargeTest
import android.view.View
import android.widget.EditText
import com.tpb.brainfuck.R
import com.tpb.brainfuck.editor.Editor
import org.hamcrest.Matchers.allOf
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class EditorKeyboardLockTest {

    @JvmField @Rule
    var mActivityTestRule = ActivityTestRule(
            Editor::class.java)

    @Test
    fun editorKeyboardLockTest() {

        val editor = mActivityTestRule.activity.findViewById<EditText>(R.id.editor)
        val initialHeight = editor.height

        val appCompatEditText = onView(
                allOf<View>(withId(R.id.editor), isDisplayed()))
        appCompatEditText.perform(click())
        val heightAfterClick = editor.height

        assertTrue(heightAfterClick < initialHeight)


        val appCompatImageButton = onView(
                allOf<View>(withId(R.id.lock_keyboard_button), isDisplayed()))
        appCompatImageButton.perform(click())

        val heightAfterLock = editor.height

        assertTrue(heightAfterLock == initialHeight)

    }


}
