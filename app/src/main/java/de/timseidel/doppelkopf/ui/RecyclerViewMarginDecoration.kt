package de.timseidel.doppelkopf.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewMarginDecoration(_margin: Int, _columns: Int) : RecyclerView.ItemDecoration() {
    private val margin: Int = _margin
    private val columns : Int = _columns

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = margin
        outRect.right = margin
        outRect.top = margin
        outRect.bottom = margin
    }
}