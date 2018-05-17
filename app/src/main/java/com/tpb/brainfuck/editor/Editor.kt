package com.tpb.brainfuck.editor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.text.style.ForegroundColorSpan
import android.view.KeyEvent
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import com.tpb.brainfuck.Application
import com.tpb.brainfuck.R
import com.tpb.brainfuck.addSimpleTextChangedListener
import com.tpb.brainfuck.db.Program
import com.tpb.brainfuck.db.ProgramDao
import com.tpb.brainfuck.runner.Formatter
import com.tpb.brainfuck.runner.Runner
import kotlinx.android.synthetic.main.activity_editor.*
import kotlin.concurrent.thread

/**
 * Created by theo on 01/07/17.
 */
class Editor : AppCompatActivity(), ConfigDialog.ConfigDialogListener {

    private var isKeyBoardLocked: Boolean = false
    private var hasProgramChanged: Boolean = false
    lateinit var program: Program
    private lateinit var dao: ProgramDao
    private var startBrace: ForegroundColorSpan? = null
    private var endBrace: ForegroundColorSpan? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(Application.themeId)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        dao = Application.db.programDao()
        val id = intent?.extras?.getLong(getString(R.string.parcel_program), -1) ?: -1
        if (id != -1L) {
            thread {
                program = dao.getProgram(id)
                editor.setText(program.source)
                title = String.format(getString(R.string.title_editing), program.name)
            }
        } else {
            setTitle(R.string.title_editor)
            program = Program()
        }

        addListeners()

    }

    private fun addListeners() {
        editor.setOnTouchListener { v, me ->
            v.onTouchEvent(me)
            if(isKeyBoardLocked) {
                (v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(v.windowToken, 0)
            }
            true
        }

        editor.addSimpleTextChangedListener {
            hasProgramChanged.or(editor.text.toString() != program.source)
        }

        editor.selectionChangeListener = object: CursorWatchingEditText.SelectionChangeListener {
            override fun onSelectionChanged(start: Int, end: Int) {
                if (start == end && start < editor.text.length) { matchBraces(start) }
            }
        }

        lock_keyboard_button.setOnClickListener { v ->
            isKeyBoardLocked = !isKeyBoardLocked
            if (isKeyBoardLocked) {
                lock_keyboard_button.setImageResource(R.drawable.ic_lock_outline_white)
                (v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(v.windowToken, 0)
            } else {
                lock_keyboard_button.setImageResource(R.drawable.ic_lock_open_white)
            }
        }

        format_code_button.setOnClickListener {
            editor.setText(Formatter.format(editor.text.toString()))
        }

        quick_run_button.setOnClickListener {
            program.source = editor.text.toString()
            startActivity(Runner.createIntent(this, program, true))
        }


        save_button.setOnClickListener {
            program.source = editor.text.toString()
            ConfigDialog.Builder(program)
                    .setType(ConfigDialog.ConfigDialogType.SAVE)
                    .setListener(this)
                    .build()
                    .show(supportFragmentManager, this::class.java.simpleName)
        }

        run_button.setOnClickListener {
            program.source = editor.text.toString()
            ConfigDialog.Builder(program)
                    .setType(ConfigDialog.ConfigDialogType.RUN)
                    .setListener(this)
                    .build()
                    .show(supportFragmentManager, this::class.java.simpleName)
        }



        increment_button.setOnClickListener { dispatchKeyEvent(KeyEvent(0, ">", 0, 0)) }
        decrement_button.setOnClickListener { dispatchKeyEvent(KeyEvent(0, "<", 0, 0)) }
        plus_button.setOnClickListener { insertChar(KeyEvent.KEYCODE_PLUS) }
        minus_button.setOnClickListener { insertChar(KeyEvent.KEYCODE_MINUS) }
        output_button.setOnClickListener { insertChar(KeyEvent.KEYCODE_PERIOD) }
        input.setOnClickListener { insertChar(KeyEvent.KEYCODE_COMMA) }
        start_loop_button.setOnClickListener { insertChar(KeyEvent.KEYCODE_LEFT_BRACKET) }
        end_loop_button.setOnClickListener { insertChar(KeyEvent.KEYCODE_RIGHT_BRACKET) }
        space_button.setOnClickListener { insertChar(KeyEvent.KEYCODE_SPACE) }
        breakpoint_button.setOnClickListener { insertChar(KeyEvent.KEYCODE_BACKSLASH) }
        backspace_button.setOnClickListener { insertChar(KeyEvent.KEYCODE_DEL) }
        forward_button.setOnClickListener { insertChar(KeyEvent.KEYCODE_DPAD_RIGHT) }
        backward_button.setOnClickListener { insertChar(KeyEvent.KEYCODE_DPAD_LEFT) }
        enter_button.setOnClickListener { insertChar(KeyEvent.KEYCODE_ENTER) }

    }

    private fun insertChar(keyCode: Int) {
        editor.dispatchKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_DOWN, keyCode, 0))
        editor.dispatchKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_UP, keyCode, 0))
    }

    private fun matchBraces(start: Int) {
        editor.text.removeSpan(startBrace)
        editor.text.removeSpan(endBrace)
        startBrace = ForegroundColorSpan(resources.getColor(R.color.colorAccent))
        endBrace = ForegroundColorSpan(resources.getColor(R.color.colorAccent))
        if (editor.text[start] == '[' || editor.text[start] == ']') {
            editor.text.setSpan(startBrace, start, start + 1, 0)
            highlightMatchingBrace(start)
        } else if (editor.text[maxOf(start - 1, 0)] == '[' || editor.text[maxOf(start - 1, 0)] == ']') {
            editor.text.setSpan(startBrace, maxOf(start - 1, 0), start, 0)
            highlightMatchingBrace(maxOf(start - 1, 0))
        }
    }

    private fun highlightMatchingBrace(pos: Int) {
        var level = 0
        val selected = editor.text[pos]

        if (selected == '[') {
            for (i in pos until editor.text.length) {
                if (editor.text[i] == '[') {
                    level++
                } else if (editor.text[i] == ']') {
                    level--
                }
                if (level == 0) {
                    editor.text.setSpan(endBrace, i, i + 1, 0)
                    return
                }
            }
        } else {
            for (i in pos downTo 0) {
                if (editor.text[i] == '[') {
                    level++
                } else if (editor.text[i] == ']') {
                    level--
                }
                if (level == 0) {
                    editor.text.setSpan(endBrace, i, i + 1, 0)
                    return
                }
            }
        }
    }


    override fun onPositiveClick(dialog: DialogFragment, launchType: ConfigDialog.ConfigDialogType, program: Program) {
        this.program = program
        if (launchType == ConfigDialog.ConfigDialogType.RUN) {
            program.source = editor.text.toString()
            startActivity(Runner.createIntent(this, program, false))
        }
        thread {
            if (program.uid == 0L) {
                program.uid = dao.insert(program)
            } else {
                dao.update(program)
            }
        }

    }

    override fun onNegativeClick(dialog: DialogFragment, launchType: ConfigDialog.ConfigDialogType) {
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        fun createIntent(context: Context, id: Long = -1): Intent {
            val intent = Intent(context, Editor::class.java)
            intent.putExtra(context.getString(R.string.parcel_program), id)
            return intent
        }

    }

}