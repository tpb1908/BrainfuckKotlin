package com.tpb.brainfuck

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.tpb.brainfuck.db.PointerOverflowBehaviour
import com.tpb.brainfuck.db.Program
import com.tpb.brainfuck.home.MainActivity
import io.reactivex.rxkotlin.subscribeBy
import org.junit.Assert.*
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import kotlin.concurrent.thread

/**
 * Created by theo on 05/07/17.
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
class DatabaseTest {



    val context: Context = InstrumentationRegistry.getTargetContext()
    val dao = Application.db.programDao()


    @Rule @JvmField
    val activityRule : ActivityTestRule<MainActivity> = ActivityTestRule<MainActivity>(MainActivity::class.java)


    @Test fun stage1_useAppContext() {
        System.out.println("Use app context is running")
        info("Use app context is running")
        assertEquals("com.tpb.brainfuck", context.packageName)
    }

    @Test fun stage2_testDatabaseCreated() {
        assertNotNull(Application.db)
    }

    @Test fun stage3_testDefaultValuesContained() {
        dao.getAllPrograms().take(1).subscribe { list ->
            var foundMatches = true
            Application.defaults.forEach {
                var foundMatch = false
                list.forEach { listItem ->
                    if (listItem.name == it.name) {
                        foundMatch = true
                    }
                }
                foundMatches = foundMatch
            }
            assertEquals(true, foundMatches)

        }
    }

    @Test fun stage4_testResetDatabase() {
        thread {
            dao.deleteAllPrograms()
            dao.getAllPrograms().take(1).subscribe {
                assertEquals(0, it.size)

            }
        }
    }

    @Test fun stage5_testRestorationOfDefaults() {
        thread {
            var count = 0
            Application.restoreDefaultValues()
            dao.getAllPrograms().take(Application.defaults.size.toLong()).subscribeBy(
                    onNext = {count++},
                    onComplete = { assertEquals(Application.defaults.size, count) }
            )
        }
    }

    @Test fun stage6_testInsertAndRetrieve() {
        val p = Program(name = "Test")
        thread {
            p.uid = dao.insert(p)
            assertEquals(p, dao.getProgram(p.uid))
        }
    }

    @Test fun stage7_testFindByNameReturnsExistingProgram() {
        val p = Program(name = "Program to find", source = "Some source")
        thread {
            p.uid = dao.insert(p)
            assertEquals(p, dao.getProgramIfExists(p.name, p.source))
        }
    }

    @Test fun stage8_testFindByNameDoesNotReturnNonExistentProgram() {
        thread {
            assertNull(dao.getProgramIfExists("nothing to find", "++>>"))
        }
    }

    @Test fun stage9_testUpdateOfProgram() {
        val p = Program(name = "Initial name", source = "Some source", memoryCapacity= 5000, pointerOverflowBehaviour = PointerOverflowBehaviour.WRAP)
        thread {
            p.uid = dao.insert(p)
            with(p) {
                name = "New name"
                source = "Modified source"
                minVal = 5300
                pointerOverflowBehaviour = PointerOverflowBehaviour.ERROR
            }
            dao.update(p)
            assertEquals(p, dao.getProgram(p.uid))
        }
    }

}
