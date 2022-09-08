package com.example.challenge2

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.challenge2.databinding.FragmentAddEditNoteBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"

class AddEditNoteFragment : Fragment() {
    private var param1: Int? = null
    private var mNote: Note? = null
    private lateinit var binding: FragmentAddEditNoteBinding
    private lateinit var mNoteDao: NoteDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getInt(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddEditNoteBinding.inflate(inflater, container, false)
        val view = binding.root
        mNoteDao = (requireActivity().application as ChallengeApplication).noteDatabase

        val id = param1
        if (id != null) {
            binding.pageTitle.text = getString(R.string.edit_note)
            GlobalScope.launch(Dispatchers.IO) {
                mNote = mNoteDao.getNote(id)
                fillEditText()
            }

            binding.saveButton.setOnClickListener {
                if (binding.editTitle.text.isNotEmpty() && binding.editText.text.isNotEmpty()) {
                    if (mNote != null) {
                        mNote!!.title = binding.editTitle.text.toString()
                        mNote!!.text = binding.editText.text.toString()
                    } else {
                        mNote = Note(
                            null,
                            binding.editTitle.text.toString().trim(),
                            binding.editText.text.toString().trim()
                        )
                    }

                    GlobalScope.launch(Dispatchers.IO) {
                        mNoteDao.update(mNote!!)
                    }

                    closeFragment("edit")
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.fields_cannot_be_empty),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            binding.pageTitle.text = getString(R.string.add_note)
            binding.saveButton.setOnClickListener {
                if (binding.editTitle.text.isNotEmpty() && binding.editText.text.isNotEmpty()) {
                    mNote = Note(
                        null,
                        binding.editTitle.text.toString().trim(),
                        binding.editText.text.toString().trim()
                    )

                    GlobalScope.launch(Dispatchers.IO) {
                        mNoteDao.insert(mNote!!)
                    }

                    closeFragment("add")
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.fields_cannot_be_empty),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        return view
    }

    private fun closeFragment(stack: String) {
        Toast.makeText(requireContext(), getString(R.string.saved_to_notes), Toast.LENGTH_SHORT)
            .show()

        requireActivity().currentFocus?.let { view ->
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
        requireActivity().supportFragmentManager.popBackStack(
            stack,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    private fun fillEditText() {
        if (mNote != null) {
            binding.editTitle.setText(mNote!!.title)
            binding.editText.setText(mNote!!.text)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(noteId: Int) =
            AddEditNoteFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, noteId)
                }
            }
    }
}