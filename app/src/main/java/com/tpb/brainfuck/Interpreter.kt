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
    private var pos: Int = 0
    private var pointer: Int = 0
    private var waitingForInput: Boolean = false
    private var paused: Boolean = true
    private var loopPositions: SparseIntArray = SparseIntArray()
    private var shouldUseBreakpoints = false
    @Volatile private var stopRequested = false
    var complete = false


    override fun run() {
        if(checkProgram()) {
            while(pos < program.source.length) {
                if (stopRequested) return
                if (paused || waitingForInput) {
                    try {
                        Thread.sleep(100)
                    } catch (exc : InterruptedException) {}
                } else {
                    step()
                }
            }
            complete = true
            io.complete()
        }
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

    fun checkProgram() : Boolean {
        val loopStarts = program.source.occurrencesOf('[')
        val loopEnds = program.source.occurrencesOf(']')
        if (loopStarts != loopEnds) {
            io.error(-1, R.string.error_loop_counts)
            return false
        }
        return findLoopPositions()
    }

    fun findLoopPositions(): Boolean {
        loopPositions.clear()
        val openings = Stack<Int>()
        for (i in 0 until program.source.length) {
            if(program.source[i] == '[') {
                openings.push(i)
            } else if (program.source[i] == ']') {
                if(openings.isEmpty()) {
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

    fun input(input: Char) {
        if(waitingForInput) {
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
                if(pointer >= mem.size) {
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
                waitingForInput = true
                io.getInput()
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
        val builder = StringBuilder()
        var lastUsedPosition = 0
        builder.append('[')
        for (i in 0 until mem.size) {
            if (mem[i] != 0 || i == mem.size - 1) {
                if (lastUsedPosition < i - 1) {
                    builder.append(lastUsedPosition)
                    builder.append("-")
                    builder.append(i - 1)
                    builder.append(" : ")
                    builder.append(0)
                } else {
                    builder.append(i)
                    builder.append(" : ")
                    builder.append(mem[i])
                }
                builder.append(", ")
                lastUsedPosition = i + 1
            }
        }
        builder.setCharAt(builder.length - 1, ']')
        return builder.toString()
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