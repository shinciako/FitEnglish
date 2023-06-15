package com.davidshinto.fitenglish.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.davidshinto.fitenglish.*
import com.davidshinto.fitenglish.databinding.FragmentGameConfBinding
import com.davidshinto.fitenglish.utils.*
import kotlin.math.abs
import kotlin.properties.Delegates


class GameConfFragment : Fragment() {
    private var _binding: FragmentGameConfBinding? = null
    private val binding get() = _binding!!
    private lateinit var rvGameMode: RecyclerView
    private lateinit var adapter: GameConfAdapter
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private var width by Delegates.notNull<Int>()
    private var widthProvider: WidthProvider? = null
    private lateinit var selectedCategory: String

    private lateinit var layoutManager: CenterZoomLayoutManager
    private val snapHelper = SnapHelperOneByOne()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameConfBinding.inflate(inflater, container, false)
        getDeviceWidth()
        setupRv()
        setupSpinner(CategoryList.categoryList)
        setupSliders()
        setupSubmitBtn()
        return binding.root
    }


    private fun getDeviceWidth() {
        width = widthProvider?.getWidth()!!
    }

    private fun setupRv() {
        rvGameMode = binding.rvGameMode
        setupAdapter()
        setupLayoutManager()
        setupSnapHelper()
        setupScrollListener()
        setupInitialRvView()
    }

    private fun setupAdapter() {
        adapter = GameConfAdapter()
        rvGameMode.adapter = adapter
    }

    private fun setupLayoutManager() {
        layoutManager =
            CenterZoomLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        layoutManager.scrollToPosition(adapter.itemCount / 2)
        rvGameMode.layoutManager = layoutManager
    }

    private fun setupSnapHelper() {
        snapHelper.attachToRecyclerView(rvGameMode)
    }


    private fun setupScrollListener() {
        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                recyclerView.post {
                    selectMiddleItem(rvGameMode.layoutManager as CenterZoomLayoutManager)
                }
            }
        }
        rvGameMode.addOnScrollListener(scrollListener)
    }

    private fun selectMiddleItem(layoutManager: CenterZoomLayoutManager) {
        val center = (width / 2).toFloat()
        var minDistance = Float.MAX_VALUE
        var closestIndex = -1
        for (i in 0 until rvGameMode.childCount) {
            val child = rvGameMode.getChildAt(i)
            val childCenter =
                (layoutManager.getDecoratedLeft(child) + layoutManager.getDecoratedRight(child)) / 2f
            val distance = abs(center - childCenter)
            if (distance < minDistance) {
                minDistance = distance
                closestIndex = rvGameMode.getChildAdapterPosition(child)
            }
        }
        if (closestIndex != -1) {
            adapter.currentItem = closestIndex
            binding.tvMode.text = adapter.getCurrentGameMode().name
        }
    }

    private fun setupInitialRvView() {
        binding.rvGameMode.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                binding.rvGameMode.removeOnLayoutChangeListener(this)
                val padding = rvGameMode.width / 2 - layoutManager.getChildAt(0)!!.width / 2
                rvGameMode.setPadding(padding, 0, padding, 0)
                val centerView = snapHelper.findSnapView(layoutManager) ?: return
                val distance = snapHelper.calculateDistanceToFinalSnap(layoutManager, centerView)
                if (distance != null) {
                    binding.rvGameMode.scrollBy(distance[0], distance[1])
                }
            }
        })
    }


    private fun setupSpinner(categories: List<String>) {
        val spinner = binding.spnCategory
        val arrayAdapter =
            CategorySpinnerAdapter(requireContext(), categories, spinner) { category: String ->
                categorySelected(category)
            }
        spinner.adapter = arrayAdapter
    }

    private fun categorySelected(category: String) {
        selectedCategory = category
    }

    private fun setupSliders() {
        binding.sliderDistance.setLabelFormatter { value: Float ->
            "${value / 1000} km"
        }
        binding.sliderDistanceAfter.setLabelFormatter { value: Float ->
            "${value.toInt()} m"
        }
    }

    private fun setupSubmitBtn() {
        binding.btnSubmit.setOnClickListener {
            val mode = adapter.getCurrentGameMode()
            val category = selectedCategory
            val distance = binding.sliderDistance.value.toInt()
            val distanceAfter = binding.sliderDistanceAfter.value.toInt()
            val questions = binding.sliderQuestions.value.toInt()
            val game = Game(0, mode, category, distance, distanceAfter, questions)
            when (mode) {
                Mode.Flash -> {
                    val action =
                        GameConfFragmentDirections.
                        actionNavigationGameConfToFlashGameActivity(game)
                    it.findNavController().navigate(action)
                }
                Mode.Match -> {
                    val action =
                        GameConfFragmentDirections.
                        actionNavigationGameConfToMatchingGameActivity(game)
                    it.findNavController().navigate(action)
                }
                Mode.Writer -> {
                    val action = GameConfFragmentDirections.
                    actionNavigationGameConfToWriterGameActivity(game)
                    it.findNavController().navigate(action)
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is WidthProvider) {
            widthProvider = context
        } else {
            throw java.lang.RuntimeException("$context must implement WidthProvider")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        rvGameMode.removeOnScrollListener(scrollListener)
    }
}