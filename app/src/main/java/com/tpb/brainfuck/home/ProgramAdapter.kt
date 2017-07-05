package com.tpb.brainfuck.home

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.tpb.brainfuck.R
import com.tpb.brainfuck.db.Program
import com.tpb.brainfuck.db.ProgramDao
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.card_program.view.*
import java.util.*

/**
 * Created by theo on 30/06/17.
 */
class ProgramAdapter(dao: ProgramDao, val handler: ProgramTouchHandler) : RecyclerView.Adapter<ProgramAdapter.ProgramHolder>() {

    var programs: MutableList<Program> = ArrayList()
    var recycler: RecyclerView? = null

    init {
        dao.getAllPrograms().observeOn(AndroidSchedulers.mainThread()).subscribe({
            if (it.size - programs.size == 1) {
                programs = it.reversed().toMutableList()
                recycler?.scrollToPosition(0)
                notifyItemInserted(0)
            } else {
                programs = it.reversed().toMutableList()
                notifyDataSetChanged()
            }
        })

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recycler = recyclerView
        val dismisser = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                handler.remove(programs[viewHolder.adapterPosition])
                notifyItemRemoved(viewHolder.adapterPosition)
            }

            override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean = true
        }
        ItemTouchHelper(dismisser).attachToRecyclerView(recyclerView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramHolder {
        return ProgramHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_program, parent, false))
    }

    override fun onBindViewHolder(holder: ProgramHolder, position: Int) {
        val prog = programs[position]
        holder.title.text = prog.name
        holder.description.text = prog.description
        holder.itemView.setOnClickListener { handler.open(prog, holder.itemView) }
        holder.runButton.setOnClickListener { handler.run(prog) }

    }

    override fun getItemCount(): Int {
        return programs.size
    }

    class ProgramHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.text_title
        val description: TextView = view.text_description
        val runButton: ImageButton = view.quick_run
    }

    interface ProgramTouchHandler {

        fun run(program: Program)

        fun open(program: Program, view: View)

        fun remove(program: Program)

    }

}

