package com.tpb.brainfuck

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.editor.*

/**
 * Created by theo on 01/07/17.
 */
class Editor : AppCompatActivity() {

    var isKeyBoardLocked: Boolean = false
    var hasProgramChanged: Boolean = false
    lateinit var program: Program

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)
        val b = intent.extras
        if(b != null) {
            program = b.getParcelable("prog")?: Program()
            editor.setText(program.source)
            title = String.format(getString(R.string.title_editing), program.name)
        } else {
            setTitle(R.string.title_editor)
        }

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

        quick_run_button.setOnClickListener {
        }


    }
}