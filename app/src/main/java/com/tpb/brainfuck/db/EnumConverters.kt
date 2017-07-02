package com.tpb.brainfuck.db

import android.arch.persistence.room.TypeConverter

/**
 * Created by theo on 02/07/17.
 */
class EnumConverters {
    @TypeConverter
    fun fromValueUnderflowString(value: String): ValueUnderflowBehaviour {
        return when (value) {
            "wrap" -> ValueUnderflowBehaviour.WRAP
            "cap" -> ValueUnderflowBehaviour.CAP
            else -> ValueUnderflowBehaviour.ERROR
        }
    }

    @TypeConverter
    fun toValueUnderflowString(behaviour: ValueUnderflowBehaviour): String {
        return when (behaviour) {
            ValueUnderflowBehaviour.WRAP -> "wrap"
            ValueUnderflowBehaviour.CAP -> "cap"
            else -> "error"
        }

    }

    @TypeConverter
    fun fromValueOverflowString(value: String): ValueOverflowBehaviour {
        return when (value) {
            "wrap" -> ValueOverflowBehaviour.WRAP
            "cap" -> ValueOverflowBehaviour.CAP
            else -> ValueOverflowBehaviour.ERROR
        }
    }

    @TypeConverter
    fun toValueOverflowString(behaviour: ValueOverflowBehaviour): String {
        return when (behaviour) {
            ValueOverflowBehaviour.WRAP -> "wrap"
            ValueOverflowBehaviour.CAP -> "cap"
            else -> "error"
        }
    }

    @TypeConverter
    fun fromPointerUnderflowString(value: String): PointerUnderflowBehaviour {
        return when (value) {
            "wrap" -> PointerUnderflowBehaviour.WRAP
            else -> PointerUnderflowBehaviour.ERROR
        }
    }

    @TypeConverter
    fun toPointerUnderflowString(behaviour: PointerUnderflowBehaviour): String {
        return when (behaviour) {
            PointerUnderflowBehaviour.WRAP -> "wrap"
            else -> "error"
        }
    }

    @TypeConverter
    fun toPointerOverflowString(behaviour: PointerOverflowBehaviour): String {
        return when (behaviour) {
            PointerOverflowBehaviour.WRAP -> "wrap"
            PointerOverflowBehaviour.EXPAND -> "expand"
            else -> "error"
        }
    }

    @TypeConverter
    fun fromPointerOverflowString(value: String): PointerOverflowBehaviour {
        return when (value) {
            "wrap" -> PointerOverflowBehaviour.WRAP
            "expand" -> PointerOverflowBehaviour.EXPAND
            else -> PointerOverflowBehaviour.ERROR
        }
    }
}