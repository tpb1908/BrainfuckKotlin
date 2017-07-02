package com.tpb.brainfuck

import android.app.Application
import android.arch.persistence.room.Room
import com.tpb.brainfuck.db.Database
import com.tpb.brainfuck.db.ProgramMigrations

/**
 * Created by theo on 01/07/17.
 */
class Application : Application() {

    companion object {
        lateinit var db : Database
    }

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(this, Database::class.java, "bfdb").addMigrations(ProgramMigrations.Migration_1_2).build()
    }
}