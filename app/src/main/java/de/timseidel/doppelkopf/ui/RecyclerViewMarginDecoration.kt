package de.timseidel.doppelkopf.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewMarginDecoration(private var marginHorizontal: Int, private var marginVertical: Int) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = marginHorizontal
        outRect.right = marginHorizontal
        outRect.top = marginVertical
        outRect.bottom = marginVertical
    }
}