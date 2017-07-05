package com.tpb.brainfuck

import android.support.test.InstrumentationRegistry
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.tpb.brainfuck.home.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by theo on 05/07/17.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class DatabaseTest {

    val context = InstrumentationRegistry.getTargetContext()

    @Rule @JvmField
    val activityRule : ActivityTestRule<MainActivity> = ActivityTestRule<MainActivity>(MainActivity::class.java)


    @Test fun useAppContext() {
        System.out.println("Use app context is running")
        info("Use app context is running")
        assertEquals("com.tpb.brainfuck", context.packageName)
    }

    @Test fun testDatabaseCreated() {
        assertNotNull(Application.db)
    }

    @Test fun testDefaultValuesContained() {

    }


}