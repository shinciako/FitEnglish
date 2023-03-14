package com.davidshinto.fitenglish.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.davidshinto.fitenglish.*
import com.davidshinto.fitenglish.databinding.FragmentGameConfBinding
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameConfBinding.inflate(inflater, container, false)
        getDeviceWidth()
        setupRv()
        setupSpinner()
        setupSliders()
        binding.btnSubmit.setOnClickListener{
            val mode = adapter.getCurrentGameMode()
        }
        return binding.root
    }

    private fun getDeviceWidth() {
        width = widthProvider?.getWidth()!!
    }

    private fun setupRv() {
        rvGameMode = binding.rvGameMode
        setupAdapter()
        val layoutManager = setupLayoutManagerToRecyclerViewAndReturnLayoutManager()
        setupSnapHelper()
        setupScrollListener()
        centerItem(layoutManager)
    }

    private fun setupAdapter() {
        adapter = GameConfAdapter()
        rvGameMode.adapter = adapter
    }

    private fun setupLayoutManagerToRecyclerViewAndReturnLayoutManager(): LayoutManager {
        val layoutManager =
            CenterZoomLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        rvGameMode.layoutManager = layoutManager
        return layoutManager
    }

    private fun setupSnapHelper() {
        val snapHelper = SnapHelperOneByOne()
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
            binding.tvMode.text = closestIndex.toString()
        }
    }

    private fun centerItem(layoutManager: LayoutManager) {
        rvGameMode.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                rvGameMode.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val padding = rvGameMode.width / 2 - layoutManager.getChildAt(0)!!.width / 2
                rvGameMode.setPadding(padding, 0, padding, 0)
                layoutManager.scrollToPosition(1)
            }
        })
    }

    private fun setupSpinner() {
        val dummyCategory = arrayOf(
            Category(0, "Food"),
            Category(1, "Drink"),
            Category(2, "IT terms")
        )
        val spinner = binding.spnCategory
        val arrayAdapter = CategorySpinnerAdapter(requireContext(), dummyCategory, spinner)
        spinner.adapter = arrayAdapter
    }

    private fun setupSliders(){
        binding.sliderDistance.setLabelFormatter { value: Float ->
            "$value km"
        }
        binding.sliderDistanceAfter.setLabelFormatter { value: Float ->
            "${value.toInt()} m"
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