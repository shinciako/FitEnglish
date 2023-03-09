package com.davidshinto.fitenglish.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.davidshinto.fitenglish.CenterZoomLayoutManager
import com.davidshinto.fitenglish.SnapHelperOneByOne
import com.davidshinto.fitenglish.databinding.FragmentGameConfBinding


class GameConfFragment : Fragment() {
    private var _binding : FragmentGameConfBinding? = null
    private val binding get() = _binding!!
    private lateinit var gameModeRv : RecyclerView
    private lateinit var adapter : GameConfAdapter
    private lateinit var scrollListener: RecyclerView.OnScrollListener


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameConfBinding.inflate(inflater, container, false)
        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                recyclerView.post {
                    selectMiddleItem(gameModeRv.layoutManager as CenterZoomLayoutManager)
                }
            }
        }
        setupRv()
        return binding.root
    }

    private fun setupRv(){
        gameModeRv = binding.gameModeRv
        adapter = GameConfAdapter()
        gameModeRv.adapter = adapter
        val layoutManager =
            CenterZoomLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        gameModeRv.layoutManager = layoutManager

        //scroll to mid elem
//        val middleOfTheList = gameModeRv.adapter!!.itemCount / 2
        gameModeRv.scrollToPosition(1)
        val snapHelper = SnapHelperOneByOne()
        snapHelper.attachToRecyclerView(gameModeRv)
        gameModeRv.addOnScrollListener(scrollListener as RecyclerView.OnScrollListener)
    }


    private fun selectMiddleItem(layoutManager: CenterZoomLayoutManager) {
        if(_binding==null)
            return
        val firstVisibleIndex = layoutManager.findFirstVisibleItemPosition()
        val lastVisibleIndex = layoutManager.findLastVisibleItemPosition()
        val visibleIndexes = listOf(firstVisibleIndex..lastVisibleIndex).flatten()

        for (i in visibleIndexes) {
            val vh = gameModeRv.findViewHolderForLayoutPosition(i)
            if (vh?.itemView == null) {
                continue
            }
            val location = IntArray(2)
            vh.itemView.getLocationOnScreen(location)
            val x = location[0]
            val halfWidth = vh.itemView.width * .5
            val rightSide = x + halfWidth
            val leftSide = x - halfWidth
            Log.i("CVS", "Works $halfWidth")
            val isInMiddle = 1080 * .25 in leftSide..rightSide
            if (isInMiddle) {
                binding.tvMode.text = i.toString()
                Log.i("CV", "Works $i")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        gameModeRv.removeOnScrollListener(scrollListener)
    }
}