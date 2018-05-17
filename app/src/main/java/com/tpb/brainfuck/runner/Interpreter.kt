package com.tpb.brainfuck.runner

import android.support.annotation.StringRes
import android.util.SparseIntArray
import com.tpb.brainfuck.R
import com.tpb.brainfuck.db.*
import com.tpb.brainfuck.occurrencesOf
import java.lang.*
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

/**
 * Created by theo on 02/07/17.
 */
class Interpreter(private val io: InterpreterIO, val program: Program) : Runnable {

    constructor(io: InterpreterIO, program: Program, useBreakPoints: Boolean) : this(io, program) {
        shouldUseBreakpoints = useBreakPoints
    }

    var mem: IntArray = kotlin.IntArray(program.memoryCapacity)
        private set
    var pos: Int = 0
        private set
    var pointer: Int = 0
        private set
    private var waitingForInput: Boolean = false
    private var paused: Boolean = true
    private var loopPositions: SparseIntArray = SparseIntArray()
    private var shouldUseBreakpoints = true
    @Volatile private var stopRequested = false
    var complete = false
    var inQueue = LinkedBlockingQueue<Int>()
        private set

    init {
        if (program.input.isNotEmpty()) {
            program.input.split(",").map { it.trim() }.mapTo(inQueue, {
                if (it.length == 1) {
                    it.first().toInt()
                } else {
                    Integer.parseInt(it)
                }
            })
        }
    }

    override fun run() {
        if (checkProgram()) {
            while (pos < program.source.length) {
                if (stopRequested || complete) return
                if (paused || waitingForInput) {
                    try {
                        Thread.sleep(100)
                    } catch (exc: InterruptedException) {
                    }
                } else {
                    step()
                }
            }
            complete = true
            io.complete()
        }
    }

    fun play() {
        paused = false
        run()
    }

    private fun checkProgram(): Boolean {
        val loopStarts = program.source.occurrencesOf('[')
        val loopEnds = program.source.occurrencesOf(']')
        if (loopStarts != loopEnds) {
            io.error(-1, R.string.error_loop_counts)
            return false
        }
        return findLoopPositions()
    }

    private fun findLoopPositions(): Boolean {
        loopPositions.clear()
        val openings = Stack<Int>()
        for (i in 0 until program.source.length) {
            if (program.source[i] == '[') {
                openings.push(i)
            } else if (program.source[i] == ']') {
                if (openings.isEmpty()) {
                    io.error(i, R.string.error_loop_mismatch)
                    return false
                } else {
                    loopPositions.put(openings.peek(), i)
                    loopPositions.put(i, openings.pop())
                }
            }
        }
        return true
    }

    fun stop() {
        stopRequested = true
    }

    fun isPaused(): Boolean {
        return paused
    }

    fun setPaused(isPaused: Boolean) {
        paused = isPaused
    }

    fun isUsingBreakpoints(): Boolean {
        return shouldUseBreakpoints
    }

    fun setUsingBreakpoints(useBreakPoints: Boolean) {
        shouldUseBreakpoints = useBreakPoints
    }

    fun input(input: Char): Boolean {
        if (waitingForInput) {
            mem[pointer] = input.toInt()
            waitingForInput = false
            return true
        }
        return false
    }

    fun performStep(): Boolean {
        if (pos < program.source.length && !waitingForInput) {
            step()
            return true
        }
        return false
    }

    private fun step() {
        when (program.source[pos]) {
            '>' -> {
                pointer++
                if (pointer >= mem.size) {
                    when (program.pointerOverflowBehaviour) {
                        PointerOverflowBehaviour.WRAP -> pointer = 0
                        PointerOverflowBehaviour.EXPAND -> mem += IntArray(program.memoryCapacity)
                        PointerOverflowBehaviour.ERROR -> {
                            complete = true
                            io.error(pos, R.string.error_pointer_overflow)
                        }
                    }
                }
            }
            '<' -> {
                pointer--
                if (pointer < 0) {
                    when (program.pointerUnderflowBehaviour) {
                        PointerUnderflowBehaviour.WRAP -> pointer = mem.size - 1
                        PointerUnderflowBehaviour.ERROR -> {
                            complete = true
                            io.error(pos, R.string.error_pointer_underflow)
                        }
                    }
                }
            }
            '+' -> {
                mem[pointer]++
                if (mem[pointer] > program.maxVal) {
                    when (program.valueOverflowBehaviour) {
                        ValueOverflowBehaviour.CAP -> mem[pointer] = program.maxVal
                        ValueOverflowBehaviour.WRAP -> mem[pointer] = program.minVal
                        ValueOverflowBehaviour.ERROR -> {
                            complete = true
                            io.error(pos, R.string.error_value_overflow)
                        }
                    }
                }
            }
            '-' -> {
                mem[pointer]--
                if (mem[pointer] < program.minVal) {
                    when (program.valueUnderflowBehaviour) {
                        ValueUnderflowBehaviour.CAP -> mem[pointer] = program.minVal
                        ValueUnderflowBehaviour.WRAP -> mem[pointer] = program.maxVal
                        ValueUnderflowBehaviour.ERROR -> {
                            complete = true
                            io.error(pos, R.string.error_value_underflow)
                        }
                    }
                }
            }
            '.' -> {
                io.output(mem[pointer].toChar().toString())
            }
            ',' -> {
                when {
                    inQueue.isNotEmpty() -> mem[pointer] = inQueue.take()
                    program.emptyInputBehaviour == EmptyInputBehaviour.KEYBOARD -> {
                        waitingForInput = true
                        io.getInput()
                    }
                    else -> mem[pointer] = 0
                }
            }
            '[' -> {
                if (mem[pointer] == 0) {
                    pos = loopPositions.get(pos)
                }
            }
            ']' -> {
                if (mem[pointer] != 0) {
                    pos = loopPositions.get(pos)
                }
            }
            '\\' -> {
                if (shouldUseBreakpoints) {
                    paused = true
                    io.breakpoint()
                }
            }
        }
        pos++
    }

    fun getMemoryDump(): String {
        val b = StringBuilder("[")
        var last = 0
        var lastDiffPos = 0
        mem.forEachIndexed { i, value ->
            if (i == mem.size - 1 || mem[i + 1] != value) {
                if (value != last || i == 0) {
                    b.append(i)
                } else {
                    b.append(lastDiffPos).append("..").append(i)
                }
                b.append(" = ").append(value).append("\n")
                lastDiffPos = i + 1
            }
            last = value
        }
        b.setLength(b.length - 2)
        return b.append("]").toString()
    }


    interface InterpreterIO {

        fun output(out: String)

        fun error(pos: Int, error: String)

        fun error(pos: Int, @StringRes error: Int)

        fun breakpoint()

        fun getInput()

        fun complete()

    }

}