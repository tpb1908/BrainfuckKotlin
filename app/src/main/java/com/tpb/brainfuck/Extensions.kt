package com.tpb.brainfuck

import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView

/**
 * Created by theo on 01/07/17.
 */

fun RecyclerView.bindFloatingActionButtion(fab: FloatingActionButton) {
    this.setOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy > 0 && fab.isShown) {
                fab.hide()
            } else if (dy < 0 && !fab.isShown) {
                fab.show()
            }
        }
    })
}

fun TextView.addSimpleTextChangedListener(listener: (e : Editable) -> Unit) {
    this.addTextChangedListener(object: TextWatcher {
        override fun afterTextChanged(p0: Editable) {
            listener(p0)
        }

        override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
        }
    })
}

fun String.occurrencesOf(sub: String): Int {
    var count = 0
    var last = 0
    while (last != -1) {
        last = this.indexOf(sub, last)
        if (last != -1) {
            count++
            last += sub.length
        }
    }
    return count
}

fun String.occurrencesOf(ch: Char): Int {
    return this.count { it == ch }
}