package de.timseidel.doppelkopf.ui.session.statistic

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

class SessionStatisticListAdapter(
    context: Context,
    objects: List<IStatisticViewWrapper>
) : ArrayAdapter<IStatisticViewWrapper>(context, 0, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getItem(position)!!.getView(parent.context)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)!!.getItemType()
    }

    override fun getViewTypeCount(): Int {
        return IStatisticViewWrapper.ITEM_TYPE_COUNT
    }
}