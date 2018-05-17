package com.tpb.brainfuck.db

import android.arch.persistence.room.*
import io.reactivex.Flowable

/**
 * Created by theo on 02/07/17.
 */

@Dao interface ProgramDao {

    @Query("SELECT * FROM program")
    fun getAllPrograms(): Flowable<List<Program>>

    @Query("SELECT * FROM program WHERE uid = :id LIMIT 1")
    fun getProgram(id: Long): Program

    @Query("SELECT * FROM program WHERE name LIKE :name AND source LIKE :source LIMIT 1")
    fun getProgramIfExists(name: String, source: String): Program?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(program: Program): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(programs: List<Program>)

    @Update
    fun update(program: Program): Int

    @Delete
    fun delete(program: Program): Int

    @Query("DELETE FROM program")
    fun deleteAllPrograms()



}