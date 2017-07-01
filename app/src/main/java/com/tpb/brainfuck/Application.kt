package com.tpb.brainfuck

import android.app.Application
import android.arch.persistence.room.Room

/**
 * Created by theo on 01/07/17.
 */
class Application : Application() {

    companion object {
        lateinit var db : Database
    }

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(this, Database::class.java, "bfdb").build()
    }
}