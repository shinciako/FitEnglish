package com.davidshinto.fitenglish.utils

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import kotlin.math.abs


class CenterZoomLayoutManager : LinearLayoutManager {
    private val shrinkAmount = 0.5f
    private val shrinkDistance = 0.8f

    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    )

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)
        shrink()
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: Recycler, state: RecyclerView.State): Int {
        val orientation = orientation
        return if (orientation == HORIZONTAL) {
            val scrolled = super.scrollHorizontallyBy(dx, recycler, state)
            shrink()
            scrolled
        } else {
            0
        }
    }

    private fun shrink() {
        val midpoint = width / 2f
        val d0 = 0f
        val d1 = shrinkDistance * midpoint
        val s0 = 1f
        val s1 = 1f - shrinkAmount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val childMidpoint = (getDecoratedRight(child!!) + getDecoratedLeft(child)) / 2f
            val d = d1.coerceAtMost(abs(midpoint - childMidpoint))
            val scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0)
            child.scaleX = scale
            child.scaleY = scale
        }
    }
}