package com.tpb.brainfuck

import android.support.annotation.StringRes
import android.util.Log
import android.util.SparseIntArray
import com.tpb.brainfuck.db.Program
import java.util.*

/**
 * Created by theo on 02/07/17.
 */
class Interpreter(val io: InterpreterIO, val program: Program) : Runnable {

    constructor(io: InterpreterIO, program: Program, useBreakPoints: Boolean) : this(io, program) {
        shouldUseBreakpoints = useBreakPoints
    }

    val mem: Array<Int> = Array(program.memoryCapacity, { 0 })
    var pos: Int = 0
    var pointer: Int = 0
    var waitingForInput: Boolean = false
    var paused: Boolean = true
    var loopPositions: SparseIntArray = SparseIntArray()
    var shouldUseBreakpoints = false
    @Volatile var stopRequested = false

    override fun run() {
        Log.i("Interpreter", "Running " + paused)
        if(checkProgram()) {
            while(pos < program.source.length) {
                Log.i("Interpreter", "Interrupted? " + stopRequested)
                if (stopRequested) return

                if (paused || waitingForInput) {
                    try {
                        Thread.sleep(100)
                    } catch (exc : InterruptedException) {}
                } else {
                    step()
                }
            }
        }
    }

    fun stop() {
        stopRequested = true
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

    fun step() {
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

    interface InterpreterIO {

        fun output(out: String)

        fun error(pos: Int, error: String)

        fun error(pos: Int, @StringRes error: Int)

        fun breakpoint()

        fun getInput()


    }

}