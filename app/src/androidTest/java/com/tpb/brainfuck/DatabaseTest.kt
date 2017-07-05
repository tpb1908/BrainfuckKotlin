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
    val dao = Application.db.programDao()


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
        dao.getAllPrograms().subscribe { list ->
//            var foundMatches = true
//            Application.defaults.forEach {
//                var foundMatch = false
//                list.forEach { listItem ->
//                    if (listItem.name == it.name) foundMatch = true
//                }
//                foundMatches = foundMatch
//            }
//            assertEquals(true, foundMatches)
            Application.defaults.forEach { info("Default name: ${it.name}") }
            list.forEach { info("List name: ${it.name}") }
        }
    }

    @Test fun testRestorationOfDefaults() {
        var section = 0
        dao.getAllPrograms().subscribe {
            if (section == 0) {
                assertEquals(Application.defaults.size, it.size)
                section++
                dao.deleteAllPrograms()
            } else if (section == 1) {
                assertEquals(0, it.size)
                section++
                Application.restoreDefaultValues()
            } else {
                assertEquals(Application.defaults.size, it.size)
            }

        }

    }


}