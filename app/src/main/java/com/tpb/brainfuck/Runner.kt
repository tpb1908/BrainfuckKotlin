package com.tpb.brainfuck

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.tpb.brainfuck.db.Program
import kotlinx.android.synthetic.main.activity_runner.*

/**
 * Created by theo on 01/07/17.
 */
class Runner : AppCompatActivity(), Interpreter.InterpreterIO {
    lateinit var program: Program
    lateinit var interpreter: Interpreter
    lateinit var thread: Thread


    companion object {

        fun createIntent(context: Context, id: Long, startImmediately: Boolean = false): Intent {
            val intent = Intent(context, Runner::class.java)
            intent.putExtra(context.getString(R.string.extra_program_id), id)
            intent.putExtra(context.getString(R.string.extra_start_immediately), startImmediately)
            return intent
        }

        fun createIntent(context: Context, program: Program, startImmediately: Boolean = false): Intent {
            val intent = Intent(context, Runner::class.java)
            intent.putExtra(context.getString(R.string.parcel_program), program)
            intent.putExtra(context.getString(R.string.extra_start_immediately), startImmediately)
            return intent
        }

        fun isValidIntent(context: Context, intent: Intent?): Boolean {
            return intent != null && (
                            intent.extras.containsKey(context.getString(R.string.extra_program_id))
                                    ||
                            intent.extras.containsKey(context.getString(R.string.parcel_program))
                    )
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_runner)
        if (!isValidIntent(this, intent)) {
            finish()
        } else if (intent.extras.containsKey(getString((R.string.parcel_program)))){
            program = intent.extras.getParcelable(getString(R.string.parcel_program))
            interpreter = Interpreter(this, program, true)
            thread = Thread(interpreter)
            setTitle()
            setup()
        } else if (intent.extras.containsKey(getString(R.string.extra_program_id))) {
            val id = intent.getLongExtra(getString(R.string.extra_program_id), -1)
            kotlin.concurrent.thread {
                program = Application.db.programDao().getProgram(id)
                runOnUiThread { setTitle(); setup() }
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setTitle() {
        if(program.name.isBlank()) {
            setTitle(R.string.title_runner)
        } else {
            title = String.format(getString(R.string.title_running), program.name)

        }
    }

    private fun startProgram() {
        thread.start()
        play()
        output.addSimpleTextChangedListener {
            output_scrollview.post { output_scrollview.fullScroll(View.FOCUS_DOWN) }
        }
    }

    private fun setup() {
        play_pause_button.setOnClickListener {
            if (thread.isAlive) {
                togglePause()
            } else {
                startProgram()
            }

        }

        step_button.setOnClickListener {
            interpreter.performStep()
        }

        restart_button.setOnClickListener {
            interpreter.stop()
            interpreter = Interpreter(this, program)
            thread = Thread(interpreter)
            startProgram()
            output.text = ""
        }

        dump_button.setOnClickListener {
            output.append("\n")
            output.append(interpreter.getMemoryDump())
        }

        breakpoint_button.setOnClickListener {
            if (interpreter.isUsingBreakpoints()) {
                interpreter.setUsingBreakpoints(false)
                breakpoint_label.setText(R.string.label_breakpoint_break_button)
            } else {
                interpreter.setUsingBreakpoints(true)
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

        if (intent?.extras?.getBoolean(getString(R.string.extra_start_immediately), false) ?: false) {
            Handler().postDelayed( { startProgram() }, 60)
        }

    }

    override fun output(out: String) {
        runOnUiThread { output.append(out + program.outSuffix) }
    }

    override fun error(pos: Int, error: String) {
        interpreter.stop()
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
        if (interpreter.isPaused()) {
            play()
        } else {
           pause()
        }
    }

    fun pause() {
        interpreter.setPaused(true)
        play_pause_button.setImageResource(R.drawable.ic_play_arrow_white)
        play_pause_label.setText(R.string.label_play)
        val sp = SpannableString(getString(R.string.text_paused))
        sp.setSpan(ForegroundColorSpan(Color.YELLOW), 0, sp.length, 0)
        output.append(sp)
        output.append("\n")
    }

    fun play() {
        interpreter.setPaused(false)
        play_pause_button.setImageResource(R.drawable.ic_pause_white)
        play_pause_label.setText(R.string.label_pause)
        val sp = SpannableString(getString(R.string.text_unpaused))
        sp.setSpan(ForegroundColorSpan(Color.GREEN), 0, sp.length, 0)
        output.append(sp)
        output.append("\n")
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
        Log.i("Runner", "Interrupting thread")
        interpreter.stop()
        super.finish()
    }
}
