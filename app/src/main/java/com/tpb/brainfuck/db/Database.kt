package com.tpb.brainfuck.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters

/**
 * Created by theo on 30/06/17.
 */

@Database(entities = arrayOf(Program::class), version = 1)
@TypeConverters(EnumConverters::class)
abstract class Database : RoomDatabase() {

    abstract fun programDao(): ProgramDao

}