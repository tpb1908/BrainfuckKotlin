package com.tpb.brainfuck

import android.support.annotation.StringRes
import android.util.SparseIntArray
import com.tpb.brainfuck.db.Program
import java.lang.StringBuilder
import java.util.*

/**
 * Created by theo on 02/07/17.
 */
class Interpreter(val io: InterpreterIO, val program: Program) : Runnable {

    constructor(io: InterpreterIO, program: Program, useBreakPoints: Boolean) : this(io, program) {
        shouldUseBreakpoints = useBreakPoints
    }

    private val mem: Array<Int> = Array(program.memoryCapacity, { 0 })
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
    private var inStream = ArrayList<Int>()

    override fun run() {
        if (checkProgram()) {
            while (pos < program.source.length) {
                if (stopRequested) return
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

    fun setInput(input: ArrayList<Int>) {
        inStream = input
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

    fun input(input: Char) {
        if (waitingForInput) {
            mem[pointer] = input.toInt()
            waitingForInput = false
        }
    }

    fun performStep() {
        if (pos < program.source.length) step()
    }

    private fun step() {
        when (program.source[pos]) {
            '>' -> {
                pointer++
                if (pointer >= mem.size) {
                }
            }
            '<' -> {
                pointer--
            }
            '+' -> {
                mem[pointer]++
            }
            '-' -> {
                mem[pointer]--
            }
            '.' -> {
                io.output(mem[pointer].toChar().toString())
            }
            ',' -> {
                if (inStream.isNotEmpty()) {
                    mem[pointer] = inStream.last()
                    inStream.remove(inStream.size)
                } else {
                    waitingForInput = true
                    io.getInput()
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
                b.append(" = ").append(value).append(", ")
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