package com.tpb.brainfuck

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.SparseIntArray
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.tpb.brainfuck.db.Program
import kotlinx.android.synthetic.main.activity_runner.*
import java.util.*

/**
 * Created by theo on 01/07/17.
 */
class Runner : AppCompatActivity(), InterpreterIO {
    lateinit var program: Program
    lateinit var interpreter: Interpreter
    lateinit var thread: Thread


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.extras == null || !intent.extras.containsKey(getString(R.string.parcel_program))) {
            finish()
        } else {
            program = intent.extras.getParcelable(getString(R.string.parcel_program))
            interpreter = Interpreter(this, program, true)
            thread = Thread(interpreter)
            if(program.name.isBlank()) {
                setTitle(R.string.title_runner)
            } else {
                title = String.format(getString(R.string.title_running), program.name)
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                supportActionBar!!.setDisplayShowHomeEnabled(true)
            }
            addListeners()
        }
    }

    fun startProgram() {
        play()
        thread.start()
        output.addSimpleTextChangedListener {
            output_scrollview.post { output_scrollview.fullScroll(View.FOCUS_DOWN) }
        }
    }

    fun addListeners() {
        play_pause_button.setOnClickListener {
            if (thread.isAlive) {
                togglePause()
            } else {
                startProgram()
            }

        }

        step_button.setOnClickListener {
            if (interpreter.pos < program.source.length) interpreter.step()
        }

        restart_button.setOnClickListener {
            thread.interrupt()
            thread = Thread(Interpreter(this, program))
            startProgram()
            output.text = ""
        }

        dump_button.setOnClickListener {

        }

        breakpoint_button.setOnClickListener {
            if (interpreter.shouldUseBreakpoints) {
                interpreter.shouldUseBreakpoints = false
                breakpoint_label.setText(R.string.label_breakpoint_break_button)
            } else {
                interpreter.shouldUseBreakpoints = true
                breakpoint_label.setText(R.string.label_breakpoint_ignore_button)
            }
        }

        input_button.setOnClickListener {
            val input = input_edittext.text.toString()
            if (input.isNotEmpty()) {
                input_edittext.error = null
                val inChar = input.first()
                output.append(inChar + "\n")
                interpreter.input(inChar)
                input_edittext.setText(null)
            } else {
                input_edittext.error = getString(R.string.error_input_a_character)
            }
        }

    }

    override fun output(out: String) {
        runOnUiThread { output.append(out + program.outSuffix) }
    }

    override fun error(pos: Int, error: String) {
        thread.interrupt()
        runOnUiThread {
            val message = SpannableString(String.format(getString(R.string.message_error, pos, error)))
            message.setSpan(ForegroundColorSpan(Color.RED), 0, message.length, 0)
            output.append(message)
        }
    }

    override fun error(pos: Int, @StringRes error: Int) {
        error(pos, getString(error))
    }

    override fun breakpoint() {
        runOnUiThread {
            pause()
            val message = SpannableString(getString(R.string.message_hit_breakpoint))
            message.setSpan(ForegroundColorSpan(Color.YELLOW), 0, message.length, 0)
            output.append(message)
        }
    }

    override fun getInput() {
        runOnUiThread {
            val prompt = SpannableString(getString(R.string.prompt_input))
            prompt.setSpan(ForegroundColorSpan(Color.GREEN), 0, prompt.length, 0)
            output.append(prompt)
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInputFromWindow(input_edittext.applicationWindowToken, InputMethodManager.SHOW_FORCED, 0)
            input_edittext.requestFocus()
        }
    }

    fun togglePause() {
        if (interpreter.paused) {
            play()
        } else {
           pause()
        }
    }

    fun pause() {
        interpreter.paused = true
        play_pause_button.setImageResource(R.drawable.ic_play_arrow_white)
        play_pause_label.setText(R.string.label_play)
        val sp = SpannableString(getString(R.string.text_paused))
        sp.setSpan(ForegroundColorSpan(Color.YELLOW), 1, sp.length-1, 0)
        output.append(sp)
    }

    fun play() {
        interpreter.paused = false
        play_pause_button.setImageResource(R.drawable.ic_pause_white)
        play_pause_label.setText(R.string.label_pause)
        val sp = SpannableString(getString(R.string.text_unpaused))
        sp.setSpan(ForegroundColorSpan(Color.GREEN), 1, sp.length-1, 0)
        output.append(sp)
    }

    override fun onBackPressed() {
        if (thread.isAlive) {
            AlertDialog.Builder(this)
                    .setTitle(R.string.title_exit_dialog)
                    .setMessage(R.string.message_exit_running_program_dialog)
                    .setPositiveButton(android.R.string.ok) { _, _ -> finish() }
                    .setNegativeButton(android.R.string.cancel) { di, _ -> di.dismiss() }
                    .create().show()
        } else {
            super.onBackPressed()
        }
    }

    override fun finish() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        thread.interrupt()
        super.finish()
    }
}

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

    override fun run() {
        pos = 0
        pointer = 0
        waitingForInput = false
        paused = true

        if(checkProgram()) {
            while(!Thread.currentThread().isInterrupted && pos < program.source.length) {
                if(paused || waitingForInput) {
                    try {
                        Thread.sleep(50)
                    } catch (exc : InterruptedException) {}
                } else {
                    step()
                }
            }
        }
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
        for (i in 0..program.source.length) {
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

}

interface InterpreterIO {

    fun output(out: String)

    fun error(pos: Int, error: String)

    fun error(pos: Int, @StringRes error: Int)

    fun breakpoint()

    fun getInput()


}