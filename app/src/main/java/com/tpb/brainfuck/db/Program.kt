package com.tpb.brainfuck.db

import android.arch.persistence.room.*
import io.mironov.smuggler.AutoParcelable

/**
 * Created by theo on 30/06/17.
 */
@Entity data class Program (
        @PrimaryKey(autoGenerate = true) var uid: Long = 0,
        var name: String = "",
        var description: String = "",
        var source: String = "",
        var outSuffix: String = "",
        var memoryCapacity: Int = 10000,
        var minVal: Int = Int.MIN_VALUE,
        var maxVal: Int = Int.MAX_VALUE,
        var valueUnderflowBehaviour: ValueUnderflowBehaviour = ValueUnderflowBehaviour.ERROR,
        var valueOverflowBehaviour: ValueOverflowBehaviour = ValueOverflowBehaviour.ERROR,
        var pointerUnderflowBehaviour: PointerUnderflowBehaviour = PointerUnderflowBehaviour.ERROR,
        var pointerOverflowBehaviour: PointerOverflowBehaviour = PointerOverflowBehaviour.ERROR,
        var input: String = ""


) : AutoParcelable



enum class ValueOverflowBehaviour {
    ERROR, WRAP, CAP
}

enum class ValueUnderflowBehaviour {
    ERROR, WRAP, CAP
}

enum class PointerOverflowBehaviour {
    ERROR, WRAP, EXPAND
}

enum class PointerUnderflowBehaviour {
    ERROR, WRAP
}
