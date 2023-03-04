package com.davidshinto.fitenglish.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.davidshinto.fitenglish.databinding.FragmentGameConfBinding

class GameConfFragment : Fragment() {
    private var _binding : FragmentGameConfBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: GameConfViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(GameConfViewModel::class.java)
        _binding = FragmentGameConfBinding.inflate(inflater, container, false)
        return binding.root
    }

}