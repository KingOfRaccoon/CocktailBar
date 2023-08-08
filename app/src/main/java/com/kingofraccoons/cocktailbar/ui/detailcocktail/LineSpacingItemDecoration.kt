package com.kingofraccoons.cocktailbar.ui.detailcocktail

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


class LineSpacingItemDecoration(
    private val drawable: Drawable
) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        parent.adapter?.let { adapter ->
            outRect.bottom = when (parent.getChildAdapterPosition(view)) {
                RecyclerView.NO_POSITION,
                adapter.itemCount - 1 -> 0
                else -> (16 * view.context.resources.displayMetrics.density).toInt() * 2
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        parent.children.forEach { view ->
            val position = parent.getChildAdapterPosition(view)
                .let { if (it == RecyclerView.NO_POSITION) return else it }
            if (parent.adapter != null && position < parent.adapter!!.itemCount - 1) {
                val left = (view.right + view.left) / 2 - drawable.intrinsicWidth
                val top = view.bottom + (16 * view.context.resources.displayMetrics.density).toInt()
                val bottom = top + drawable.intrinsicHeight
                val right = (view.right + view.left) / 2 + drawable.intrinsicWidth
                drawable.bounds = Rect(left, top, right, bottom)
                c.save()
                drawable.draw(c)
                c.restore()
            }
        }
    }
}