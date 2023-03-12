package com.davidshinto.fitenglish.ui.home

import android.content.Context
import android.graphics.Rect
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
import com.davidshinto.fitenglish.CenterZoomLayoutManager
import com.davidshinto.fitenglish.SnapHelperOneByOne
import com.davidshinto.fitenglish.WidthProvider
import com.davidshinto.fitenglish.databinding.FragmentGameConfBinding
import kotlin.math.abs
import kotlin.properties.Delegates


class GameConfFragment : Fragment() {
    private var _binding: FragmentGameConfBinding? = null
    private val binding get() = _binding!!
    private lateinit var gameModeRv: RecyclerView
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
        return binding.root
    }

    private fun getDeviceWidth() {
        width = widthProvider?.getWidth()!!
    }

    private fun setupRv() {
        gameModeRv = binding.gameModeRv
        adapter = GameConfAdapter()
        gameModeRv.adapter = adapter
        val layoutManager =
            CenterZoomLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        gameModeRv.layoutManager = layoutManager
        val snapHelper = SnapHelperOneByOne()
        snapHelper.attachToRecyclerView(gameModeRv)
        initScrollListener()
        gameModeRv.addOnScrollListener(scrollListener)
        centerItem(layoutManager)
    }

    private fun initScrollListener() {
        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                recyclerView.post {
                    selectMiddleItem(gameModeRv.layoutManager as CenterZoomLayoutManager)
                }
            }
        }
    }

    private fun selectMiddleItem(layoutManager: CenterZoomLayoutManager) {
        val center = (width / 2).toFloat()
        var minDistance = Float.MAX_VALUE
        var closestIndex = -1
        for (i in 0 until gameModeRv.childCount) {
            val child = gameModeRv.getChildAt(i)
            val childCenter =
                (layoutManager.getDecoratedLeft(child) + layoutManager.getDecoratedRight(child)) / 2f
            val distance = abs(center - childCenter)
            if (distance < minDistance) {
                minDistance = distance
                closestIndex = gameModeRv.getChildAdapterPosition(child)
            }
        }
        if (closestIndex != -1) {
            binding.tvMode.text = closestIndex.toString()
        }
    }

    private fun centerItem(layoutManager: LayoutManager) {
        gameModeRv.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                gameModeRv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val padding = gameModeRv.width / 2 - layoutManager.getChildAt(0)!!.width / 2
                gameModeRv.setPadding(padding, 0, padding, 0)
                layoutManager.scrollToPosition(1)
            }
        })
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
        gameModeRv.removeOnScrollListener(scrollListener)
    }
}