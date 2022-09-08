package com.example.challenge2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.challenge2.databinding.FragmentNotesListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NotesListFragment : Fragment() {
    private lateinit var binding: FragmentNotesListBinding
    private lateinit var mNoteDao: NoteDao
    private var notesList: MutableList<Note>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotesListBinding.inflate(inflater, container, false)
        val view = binding.root
        mNoteDao = (requireActivity().application as ChallengeApplication).noteDatabase

        GlobalScope.launch(Dispatchers.IO) {
            notesList = mNoteDao.getAll()
            Log.d("myLog", notesList.toString())
            displayNotes()
        }

        binding.fab.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                ).replace(R.id.fragment_container, AddEditNoteFragment())
                .addToBackStack("add")
                .commit()
        }
        return view
    }

    private fun displayNotes() {
        if (notesList != null) {
            val mLayoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
            binding.rvNotes.layoutManager = mLayoutManager

            val noteAdapter = NoteAdapter(notesList!!)
            binding.rvNotes.adapter = noteAdapter

            noteAdapter.onEditItemClick = { noteId ->
                requireActivity().supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out
                    ).replace(R.id.fragment_container, AddEditNoteFragment.newInstance(noteId))
                    .addToBackStack("edit")
                    .commit()
            }
            noteAdapter.onShareItemClick = { note ->
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, note.text)
                startActivity(shareIntent)
            }
            noteAdapter.onItemLongClick = { note, position ->
                val alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setMessage(getString(R.string.delete_confirm))
                alertDialog.setPositiveButton("Sil") { _, _ ->
                    GlobalScope.launch(Dispatchers.IO) {
                        mNoteDao.delete(note)
                    }
                    notesList!!.removeAt(position)
                    noteAdapter.notifyItemRemoved(position)
                }
                alertDialog.setNegativeButton(getString(R.string.cancel)) { _, _ -> }
                alertDialog.create().show()
            }
        }
        else {
            binding.noNotes.visibility = View.VISIBLE
        }
    }
}