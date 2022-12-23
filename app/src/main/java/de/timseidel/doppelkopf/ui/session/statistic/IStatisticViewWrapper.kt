package de.timseidel.doppelkopf.ui.session.statistic

import android.content.Context
import android.view.View

interface IStatisticViewWrapper {

    companion object{
        const val ITEM_TYPE_COUNT = 4

        const val ITEM_TYPE_STAT_TEXT = 0
        const val ITEM_TYPE_CHART_LINE = 1
        const val ITEM_TYPE_CHART_PIE = 2
        const val ITEM_TYPE_CHART_COLUMN = 2

        const val COLOR_POSITIVE_DARK = "22CC22"
        const val COLOR_POSITIVE_LIGHT = "AAFF64"
        const val COLOR_NEGATIVE_DARK = "FF8C9D"
        const val COLOR_NEGITIVE_LIGHT = "FFA0AA"
    }

    fun getItemType(): Int

    fun getView(context: Context): View
}