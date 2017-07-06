package com.tpb.brainfuck

import com.tpb.brainfuck.db.*
import com.tpb.brainfuck.runner.Interpreter
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.LinkedBlockingQueue

/**
 * Created by theo on 05/07/17.
 */
@RunWith(MockitoJUnitRunner::class)
class InterpreterTest : Interpreter.InterpreterIO {

    var inter: Interpreter? = null
    var input = LinkedBlockingQueue<Char>()


    var output: String? = null
    var errorPosition: Int = Integer.MIN_VALUE
    var errorString: String? = null
    var errorResource: Int = 0
    var breakpointReached: Boolean = false
    var inputCalled: Boolean = false
    var complete: Boolean  = false

    @Before
    fun resetIOValues() {
        output = null
        errorPosition = Integer.MIN_VALUE
        errorString = null
        errorResource = 0
        breakpointReached = false
        inputCalled = false
        complete = false
        input.clear()
    }

    @Test
    fun testNonEqualNumberOfLoopStartsAndEnds() {
        runWithSource("[[[]]")

        assertEquals(R.string.error_loop_counts, errorResource)
        assertEquals(-1, errorPosition)
    }

    @Test fun testMismatchedLoops() {
        runWithSource("]]][[[")

        assertEquals(R.string.error_loop_mismatch, errorResource)
        assertEquals(0, errorPosition)
    }

    @Test fun testBreakpointIgnored() {
        runWithSource(">>\\>>", false)

        assertFalse(breakpointReached)
    }

    @Test fun testBreakpointObeyed() {
        runWithSource(">>\\>>", true)

        assertTrue(breakpointReached)
    }

    @Test fun testInputCalled() {
        input.add('i')
        runWithSource(">>>,>>>")

        assertTrue(inputCalled)
    }

    @Test fun testInputCalledAfterInputQueueEmptied() {
        input.add('i')
        inter = Interpreter(this, Program(source = ",,,,,,", input = "1,2,3,4,5"))
        inter?.play()
        assertTrue(inputCalled)
    }

    @Test fun testZeroInputWhenQueueEmptiedAndConfigSet() {
        inter = Interpreter(this, Program(source = ",,,,.", input = "1,2,3", emptyInputBehaviour = EmptyInputBehaviour.ZERO))
        repeat(3, { inter?.performStep() })

        assertFalse(inputCalled)

        inter?.performStep()
        inter?.performStep()
        assertEquals(0.toChar(), output?.first())
    }

    @Test fun testCorrectOutputForInput() {
        input.add('i')
        runWithSource(",.")

        assertTrue(inputCalled)
        assertEquals('i', output?.first())
    }

    @Test fun testDifferentOutputAfterPointerMove() {
        input.add('i')
        runWithSource(",>.")

        assertTrue(inputCalled)
        assertNotEquals('i', output?.first())
    }

    @Test fun testStepCall() {
        input.add('i')
        inter = Interpreter(this, Program(source = "...,"))
        inter?.performStep()
        assertEquals(1, output?.length)
        inter?.performStep()
        assertEquals(2, output?.length)
        inter?.performStep()
        assertEquals(3, output?.length)
        inter?.performStep()
        System.out.println("Input called? $inputCalled")
        assertTrue(inputCalled)
    }

    @Test fun testStepFailsOnProgramEnd() {
        inter = Interpreter(this, Program(source = ">>>>"))
        repeat(4, { inter?.performStep() })
        assertFalse(inter?.performStep() ?: true)
    }

    @Test fun testOutputOfInputData() {
        inter = Interpreter(this, Program(source = ",. ,. ,. ,. ,. ,.", input = "113,119,101,114,116,121"))
        inter?.play()

        assertEquals("qwerty", output)
    }

    @Test fun testErrorOnPointerOverflow() {
        inter = Interpreter(this, Program(source = ">>>>>", memoryCapacity = 4))
        inter?.play()

        assertEquals(R.string.error_pointer_overflow, errorResource)
    }

    @Test fun testExpandOnPointerOverflow() {
        inter = Interpreter(this, Program(source = ">>>>>", memoryCapacity = 4, pointerOverflowBehaviour = PointerOverflowBehaviour.EXPAND))
        inter?.play()

        assertEquals(8, inter?.mem?.size)
    }

    @Test fun testWrapOnPointerOverflow() {
        inter = Interpreter(this, Program(source = ">>>>>>", memoryCapacity = 4, pointerOverflowBehaviour = PointerOverflowBehaviour.WRAP))
        inter?.play()

        assertEquals(2, inter?.pointer)
    }

    @Test fun testErrorOnPointerUnderflow() {
        runWithSource("<")

        assertEquals(R.string.error_pointer_underflow, errorResource)
    }

    @Test fun testWrapOnPointerUnderflow() {
        inter = Interpreter(this, Program(source = "<", pointerUnderflowBehaviour = PointerUnderflowBehaviour.WRAP))
        inter?.play()

        assertEquals((inter?.mem?.size ?: 0) - 1 , inter?.pointer)
    }

    @Test fun testValueOverflowError() {
        inter = Interpreter(this, Program(source = "+++++", maxVal = 3))
        inter?.play()

        assertEquals(R.string.error_value_overflow, errorResource)
    }

    @Test fun testValueOverflowCap() {
        inter = Interpreter(this, Program(source = "+++++", maxVal = 3, valueOverflowBehaviour = ValueOverflowBehaviour.CAP))
        inter?.play()

        assertEquals(inter?.program?.maxVal, inter?.mem?.first())
    }

    @Test fun testValueOverflowWrap() {
        inter = Interpreter(this, Program(source = "++++", maxVal = 3, valueOverflowBehaviour = ValueOverflowBehaviour.WRAP))
        inter?.play()

        assertEquals(inter?.program?.minVal, inter?.mem?.first())
    }

    @Test fun testValueUnderflowError() {
        inter = Interpreter(this, Program(source = "-----", minVal = -1))
        inter?.play()

        assertEquals(R.string.error_value_underflow, errorResource)
    }

    @Test fun testValueUnderflowCap() {
        inter = Interpreter(this, Program(source = "--", minVal = -1, valueUnderflowBehaviour = ValueUnderflowBehaviour.CAP))
        inter?.play()

        assertEquals(inter?.program?.minVal, inter?.mem?.first())
    }

    @Test fun testValueUnderflowWrap() {
        inter = Interpreter(this, Program(source = "--", minVal = -1, valueUnderflowBehaviour = ValueUnderflowBehaviour.WRAP))
        inter?.play()

        assertEquals(inter?.program?.maxVal, inter?.mem?.first())
    }



    private fun runWithSource(source: String) {
        inter = Interpreter(this, Program(source = source))
        inter?.play()
    }

    private fun runWithSource(source: String, useBreakPoints: Boolean) {
        inter = Interpreter(this, Program(source = source), useBreakPoints)
        inter?.play()
    }


    //Interface methods for Interpreter

    override fun output(out: String) {
        System.out.println("Output: $out")
        if (output == null) {
            output = out
        } else {
            output += out
        }
    }

    override fun error(pos: Int, error: String) {
        System.out.println("Error at $pos with message $error")
        errorPosition = pos
        errorString = error
        inter?.stop()
    }

    override fun error(pos: Int, error: Int) {
        System.out.println("Error at $pos with resId $error")
        errorPosition = pos
        errorResource = error
        inter?.stop()
    }

    override fun breakpoint() {
        System.out.println("Breakpoint reached")
        breakpointReached = true
        inter?.stop()
    }

    override fun getInput() {
        System.out.println("Input called. Inputting ${input.peek()}")
        inputCalled = true
        inter?.input(input.poll())
    }

    override fun complete() {
        System.out.println("Complete")
        complete = true
    }
}