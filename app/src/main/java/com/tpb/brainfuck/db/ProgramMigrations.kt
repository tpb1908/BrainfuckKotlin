package com.tpb.brainfuck.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

/**
 * Created by theo on 02/07/17.
 */
class ProgramMigrations {

    companion object {

        val Migration_1_2 = object:  Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE program ADD valueUnderflowBehaviour VARCHAR, valueOverflowBehaviour VARCHAR, pointerUnderflowBehaviour VARCHAR, pointerOverflowBehaviour VARCHAR;")
            }
        }
    }

}