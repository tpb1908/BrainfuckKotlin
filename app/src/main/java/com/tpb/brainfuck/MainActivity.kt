package com.tpb.brainfuck

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    lateinit var adapter: ProgramAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dao = Application.db.programDao()

        adapter = ProgramAdapter(dao)

        program_recycler.adapter = adapter
        program_recycler.layoutManager = LinearLayoutManager(this)
        program_recycler.bindFloatingActionButtion(fab)


        fab.setOnClickListener {
            thread {
                val p = Program(name = "test program" + System.currentTimeMillis())
                dao.insert(p)
            }
        }

//        thread {
//            // dao.deleteAllPrograms()
//            Log.i("Test", "Running on thread " + p.toString())
//            Log.i("Insertion", "Returned " + dao.insert(p))
//            p.description = "Modified description"
//            Log.i("Test", "Program is now  " + p.toString())
//            dao.update(p)
//
//        }
    }

}
