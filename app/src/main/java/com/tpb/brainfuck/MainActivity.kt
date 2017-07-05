package com.tpb.brainfuck

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.tpb.brainfuck.db.Program
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), ProgramAdapter.ProgramTouchHandler {

    lateinit var adapter: ProgramAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(Application.themeId)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dao = Application.db.programDao()

        adapter = ProgramAdapter(dao, this)
        program_recycler.adapter = adapter
        program_recycler.layoutManager = LinearLayoutManager(this)
        program_recycler.bindFloatingActionButton(fab)

        fab.setOnClickListener { startActivity(Editor.createIntent(this)) }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        if (Application.themeId == R.style.AppTheme) {
            menu?.add(0, R.id.menu_item_switch_theme,  Menu.NONE, getString(R.string.menu_item_dark_theme))
        } else {
            menu?.add(0, R.id.menu_item_switch_theme,  Menu.NONE, getString(R.string.menu_item_light_theme))
        }
        menu?.add(0, R.id.menu_item_restore_defaults, Menu.NONE, getString(R.string.menu_item_restore_defaults))
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_item_switch_theme) {
            Application.toggleTheme(this)
            recreate()
        } else if (item?.itemId == android.R.id.home) {
            onBackPressed()
        } else if(item?.itemId == R.id.menu_item_restore_defaults) {
            Application.restoreDefaultValues()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun run(program: Program) {
        startActivity(Runner.createIntent(this, program, true))
    }

    override fun open(program: Program, view: View) {
        startActivity(Editor.createIntent(this, program.uid))
    }

    override fun remove(program: Program) {
        thread { Application.db.programDao().delete(program) }
        Snackbar.make(coordinator, "Deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO") { thread { Application.db.programDao().insert(program) } }
                .show()
    }
}
