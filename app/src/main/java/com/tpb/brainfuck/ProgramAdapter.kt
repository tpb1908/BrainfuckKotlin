package com.tpb.brainfuck

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.tpb.brainfuck.db.Program
import com.tpb.brainfuck.db.ProgramDao
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.program_card.view.*
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
            } else if (programs.size - it.size == 1) {
                val pos = programs.indexOf(programs.minus(it).first())
                programs.removeAt(pos)
                notifyItemRemoved(pos)
            } else {
                programs = it.reversed().toMutableList()
                notifyDataSetChanged()
            }
        })

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recycler = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramHolder {
        return ProgramHolder(LayoutInflater.from(parent.context).inflate(R.layout.program_card, parent, false))
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

