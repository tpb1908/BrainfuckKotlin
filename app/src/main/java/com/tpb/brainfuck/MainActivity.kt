package com.tpb.brainfuck

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.tpb.brainfuck.db.Program
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ProgramAdapter.ProgramTouchHandler {

    lateinit var adapter: ProgramAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dao = Application.db.programDao()

        adapter = ProgramAdapter(dao, this)

        program_recycler.adapter = adapter
        program_recycler.layoutManager = LinearLayoutManager(this)
        program_recycler.bindFloatingActionButtion(fab)


        fab.setOnClickListener {
            startActivity(Editor.createIntent(this))
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

    override fun run(program: Program) {
        startActivity(Runner.createIntent(this, program))
    }

    override fun open(program: Program) {
        startActivity(Editor.createIntent(this, program.uid))
    }

    override fun remove(program: Program) {
    }
}
