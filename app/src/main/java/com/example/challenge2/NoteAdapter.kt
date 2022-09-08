package com.example.challenge2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(private val notes: List<Note>) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {
        var onEditItemClick: ((Int) -> Unit)? = null
    var onShareItemClick: ((Note) -> Unit)? = null
    var onItemLongClick: ((Note, Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mNote = notes[position]

        holder.title.text = mNote.title
        holder.text.text = mNote.text
    }

    override fun getItemCount() = notes.size

    inner class ViewHolder(_itemView: View) : RecyclerView.ViewHolder(_itemView) {
        val title: TextView = _itemView.findViewById(R.id.item_title)
        val text: TextView = _itemView.findViewById(R.id.item_text)
        private val edit: ImageView = _itemView.findViewById(R.id.editButton)
        private val share: ImageView = _itemView.findViewById(R.id.shareButton)

        init {
            edit.setOnClickListener {
                onEditItemClick?.invoke(notes[adapterPosition].noteId!!)
            }
            share.setOnClickListener {
                onShareItemClick?.invoke(notes[adapterPosition])
            }
            _itemView.setOnLongClickListener {
                onItemLongClick?.invoke(notes[adapterPosition], adapterPosition)
                return@setOnLongClickListener true
            }
        }
    }
}