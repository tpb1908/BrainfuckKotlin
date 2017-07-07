package com.tpb.brainfuck.home


import android.content.Context
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import com.tpb.brainfuck.R
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matchers.allOf
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ToggleThemeTest {

    @JvmField @Rule
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testThemeTogglePersistence() {
        val c = getInstrumentation().targetContext
        val prefs = c.getSharedPreferences(c.packageName, Context.MODE_PRIVATE)
        val initialThemeFlag = prefs.getBoolean("darkTheme", false)

        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)

        val appCompatTextView = onView(
                allOf<View>(withId(R.id.title), withText(object : BaseMatcher<String>() {
                    override fun matches(item: Any): Boolean {
                        return c.getString(R.string.menu_item_dark_theme) == item || c.getString(R.string.menu_item_light_theme) == item
                    }

                    override fun describeTo(description: Description) {

                    }
                }), isDisplayed()))
        appCompatTextView.perform(click())

        val themeFlagAfterToggle = prefs.getBoolean("darkTheme", false)

        Assert.assertNotEquals(initialThemeFlag, themeFlagAfterToggle)


    }

}
