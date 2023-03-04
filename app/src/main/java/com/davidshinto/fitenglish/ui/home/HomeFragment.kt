package com.davidshinto.fitenglish.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.davidshinto.fitenglish.R
import com.davidshinto.fitenglish.databinding.FragmentHomeBinding
import com.davidshinto.fitenglish.startAnimation

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val animation =
            AnimationUtils.loadAnimation(this.context, R.anim.circle_explosion_anim).apply {
                duration = 900
                interpolator = AccelerateDecelerateInterpolator()
            }
        binding.fab.setOnClickListener{
            binding.fab.isVisible = false
            binding.circle.isVisible = true
            binding.circle.startAnimation(animation){
                this.context?.let { it1 ->
                    root.setBackgroundColor(it1.getColor(R.color.teal_200))
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