package com.davidshinto.fitenglish.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.davidshinto.fitenglish.R
import com.davidshinto.fitenglish.databinding.FragmentHomeBinding
import com.davidshinto.fitenglish.startAnimation

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val animation =
            AnimationUtils.loadAnimation(this.context, R.anim.elipse_explosion_anim).apply {
                duration = 1000
                interpolator = AccelerateDecelerateInterpolator()
            }
        binding.fab.setOnClickListener{
            binding.fab.isVisible = false
            binding.elipse.isVisible = true
            binding.elipse.startAnimation(animation){
                this.context?.let { _ ->
                    it.findNavController().navigate(R.id.action_navigation_home_to_navigation_game_conf)
                }
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}