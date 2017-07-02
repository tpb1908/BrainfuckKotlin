package com.tpb.brainfuck

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_editor.*
import kotlin.concurrent.thread

/**
 * Created by theo on 01/07/17.
 */
class Editor : AppCompatActivity() {

    var isKeyBoardLocked: Boolean = false
    var hasProgramChanged: Boolean = false
    lateinit var program: Program
    lateinit var dao: ProgramDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)
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

        lock_keyboard_button.setOnClickListener { v ->
            isKeyBoardLocked.not()
            if (isKeyBoardLocked) {
                lock_keyboard_button.setImageResource(R.drawable.ic_lock_outline_white)
                (v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(v.windowToken, 0)
            } else {
                lock_keyboard_button.setImageResource(R.drawable.ic_lock_open_white)
            }
        }

        run_button.setOnClickListener {

        }

        quick_run_button.setOnClickListener {

        }

        save_button.setOnClickListener {

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

    companion object {

        fun createIntent(context: Context, id: Long = -1): Intent {
            val intent = Intent(context, Editor::class.java)
            intent.putExtra(context.getString(R.string.parcel_program), id)
            return intent
        }

    }

}