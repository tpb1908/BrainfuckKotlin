package com.tpb.brainfuck

import android.content.Context
import android.os.Build
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.TextView
import java.util.*

/**
 * Created by theo on 01/07/17.
 */

/**
 * Adds an [RecyclerView.OnScrollListener] to show or hide the FloatingActionButton when the RecyclerView scrolls up
 * or down respectively
 */
fun RecyclerView.bindFloatingActionButton(fab: FloatingActionButton) = this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dy > 0 && fab.isShown) {
            fab.hide()
        } else if (dy < 0 && !fab.isShown) {
            fab.show()
        }
    }
})

/**
 * Calls the given function on [TextWatcher.afterTextChanged]
 */
inline fun TextView.addSimpleTextChangedListener(crossinline listener: (e : Editable) -> Unit) = this.addTextChangedListener(object: TextWatcher {
    override fun afterTextChanged(p0: Editable) {
        listener(p0)
    }

    override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
    }
})



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

fun String.occurrencesOf(ch: Char): Int = this.count { it == ch }

fun Context.getCurrentLocale(): Locale {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return this.resources.configuration.locales.get(0)
    } else {
        return resources.configuration.locale
    }
}


fun Any.debug(message: String) {
    Log.d(this::class.java.simpleName, message)
}

fun Any.debug(message: String, tr: Throwable) {
    Log.d(this::class.java.simpleName, message, tr)
}


fun Any.error(message: String) {
    Log.e(this::class.java.simpleName, message)
}

fun Any.error(message: String, tr: Throwable) {
    Log.e(this::class.java.simpleName, message, tr)
}

fun Any.info(message: String) {
    Log.i(this::class.java.simpleName, message)
}

fun Any.info(message: String, tr: Throwable) {
    Log.i(this::class.java.simpleName, message, tr)
}

fun Any.verbose(message: String) {
    Log.v(this::class.java.simpleName, message)
}


fun Any.verbose(message: String, tr: Throwable) {
    Log.v(this::class.java.simpleName, message, tr)
}

fun Any.warn(message: String) {
    Log.w(this::class.java.simpleName, message)
}


fun Any.warn(message: String, tr: Throwable) {
    Log.w(this::class.java.simpleName, message, tr)
}

fun Any.warn(tr: Throwable) {
    Log.w(this::class.java.simpleName, tr)
}

fun Any.wtf(message: String) {
    Log.wtf(this::class.java.simpleName, message)
}


fun Any.wtf(message: String, tr: Throwable) {
    Log.wtf(this::class.java.simpleName, message, tr)
}

fun Any.wtf(tr: Throwable) {
    Log.wtf(this::class.java.simpleName, tr)
}
