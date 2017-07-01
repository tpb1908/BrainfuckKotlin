package com.tpb.brainfuck

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

/**
 * Created by theo on 30/06/17.
 */

@Database(entities = arrayOf(Program::class), version = 1)
abstract class Database : RoomDatabase() {
    abstract fun programDao(): ProgramDao
}