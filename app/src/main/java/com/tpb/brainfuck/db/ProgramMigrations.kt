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

        val Migration_2_3 = object: Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE program ADD input VARCHAR;")
            }
        }

        val Migration_3_4 = object: Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE program ADD emptyInputBehaviour VARCHAR;")
            }
        }
    }

}