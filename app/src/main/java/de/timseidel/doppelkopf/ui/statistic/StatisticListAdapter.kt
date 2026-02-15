package de.timseidel.doppelkopf.ui.statistic

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import de.timseidel.doppelkopf.ui.statistic.views.IStatisticViewWrapper

class StatisticListAdapter(
    context: Context,
    objects: List<IStatisticViewWrapper>
) : ArrayAdapter<IStatisticViewWrapper>(context, 0, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = requireNotNull(getItem(position)) {
            "Statistic item at position $position is missing."
        }
        return item.getView(parent.context)
    }

    override fun getItemViewType(position: Int): Int {
        val item = requireNotNull(getItem(position)) {
            "Statistic item type at position $position is missing."
        }
        return item.getItemType()
    }

    override fun getViewTypeCount(): Int {
        return IStatisticViewWrapper.ITEM_TYPE_COUNT
    }
}
