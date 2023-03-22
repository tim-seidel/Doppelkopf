package de.timseidel.doppelkopf.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewMarginDecoration(var _marginHorizontal: Int, var _marginVertical: Int) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = _marginHorizontal
        outRect.right = _marginHorizontal
        outRect.top = _marginVertical
        outRect.bottom = _marginVertical
    }
}