package com.davidshinto.fitenglish.ui.finder

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.davidshinto.fitenglish.R
import com.davidshinto.fitenglish.databinding.FragmentDashboardBinding
import com.davidshinto.fitenglish.utils.Word
import com.davidshinto.fitenglish.utils.WordAdapter
import com.davidshinto.fitenglish.utils.WordList
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class FinderFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        root = view

        root.findViewById<SearchView>(R.id.search_view)
            ?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    filterData(newText)
                    return true
                }
            })

        filterData("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun filterData(query: String?) {
        if (query.isNullOrBlank()) {
            updateUI(WordList.wordList)
        } else {

            val queriedWordList = mutableListOf<Word>()
            for(word in WordList.wordList)
            {
                if(word.engName.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT)) ||
                    word.polName.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT)))
                    queriedWordList.add(word)
            }

            updateUI(queriedWordList)
        }
    }

    private fun updateUI(dataList: List<Word>) {
        val recyclerView = root.findViewById<RecyclerView>(R.id.rv_wordList)
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())

        if (dataList.isEmpty()) {
            recyclerView?.adapter = null
        } else {
            recyclerView?.adapter = WordAdapter(dataList)
        }
    }
}