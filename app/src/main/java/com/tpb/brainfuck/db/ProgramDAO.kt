package com.tpb.brainfuck.db

import android.arch.persistence.room.*
import io.reactivex.Flowable

/**
 * Created by theo on 02/07/17.
 */

@Dao interface ProgramDao {

    @Query("SELECT * FROM program")
    fun getAllPrograms(): Flowable<List<Program>>

    @Query("SELECT * FROM program WHERE uid = :arg0 LIMIT 1")
    fun getProgram(id: Long): Program

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(program: Program): Long

    @Update
    fun update(program: Program): Int

    @Delete
    fun delete(program: Program): Int

    @Query("DELETE FROM program")
    fun deleteAllPrograms()



}