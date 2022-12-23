package de.timseidel.doppelkopf.ui.session.statistic.views

import android.content.Context
import android.view.View
import de.timseidel.doppelkopf.ui.session.statistic.IStatisticViewWrapper

class SimpleTextStatisticViewWrapper(private val title: String, private val description: String, private val value: String):
    IStatisticViewWrapper {

    override fun getItemType(): Int {
        return IStatisticViewWrapper.ITEM_TYPE_STAT_TEXT
    }

    override fun getView(context: Context): View {
        val textStatView = SimpleTextStatisticView(context)

        textStatView.setTitle(title)
        textStatView.setDescription(description)
        textStatView.setValue(value)

        return textStatView
    }
}