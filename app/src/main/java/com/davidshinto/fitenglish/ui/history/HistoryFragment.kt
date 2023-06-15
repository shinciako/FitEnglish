package com.davidshinto.fitenglish.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.davidshinto.fitenglish.MainActivity
import com.davidshinto.fitenglish.databinding.FragmentHistoryBinding
import com.davidshinto.fitenglish.db.Session
import com.davidshinto.fitenglish.db.SessionDatabase
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: HistorySessionRecyclerViewAdapter
    private lateinit var sessionList: List<Session>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            setupDb()
        }
        setupRv()
        return binding.root
    }

    private suspend fun setupDb() {
        val dao = SessionDatabase.getInstance((activity as MainActivity).application).sessionDao()
        sessionList = dao.getAllSessions()
        adapter.setList(sessionList)
    }

    private fun setupRv() {
        val rvGames = binding.rvSessions
        rvGames.layoutManager = LinearLayoutManager(this.context)
        adapter = HistorySessionRecyclerViewAdapter()
        rvGames.adapter = adapter
    }
}