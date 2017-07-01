package com.tpb.brainfuck

import android.arch.persistence.room.*
import io.mironov.smuggler.AutoParcelable
import io.reactivex.Flowable

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
        var maxVal: Int = Int.MAX_VALUE


) : AutoParcelable

@Dao interface ProgramDao {

    @Query("SELECT * FROM program")
    fun getAllPrograms(): Flowable<List<Program>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(program: Program): Long

    @Update
    fun update(program: Program): Int

    @Delete
    fun delete(program: Program): Int

    @Query("DELETE FROM program")
    fun deleteAllPrograms()

}