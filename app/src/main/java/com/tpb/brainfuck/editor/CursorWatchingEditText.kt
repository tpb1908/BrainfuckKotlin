package com.tpb.brainfuck.editor

import android.content.Context
import android.support.v7.widget.AppCompatEditText
import android.util.AttributeSet

/**
 * Created by theo on 07/07/17.
 */
class CursorWatchingEditText : AppCompatEditText {

    var selectionChangeListener: SelectionChangeListener? = null

    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle)



    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        selectionChangeListener?.onSelectionChanged(selStart, selEnd)

    }

    interface SelectionChangeListener {

        fun onSelectionChanged(start: Int, end: Int)

    }

}