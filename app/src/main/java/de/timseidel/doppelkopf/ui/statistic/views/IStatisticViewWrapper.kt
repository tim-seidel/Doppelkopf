package de.timseidel.doppelkopf.ui.statistic.views

import android.content.Context
import android.view.View

interface IStatisticViewWrapper {

    companion object {
        const val ITEM_TYPE_COUNT = 4

        const val ITEM_TYPE_STAT_TEXT = 0
        const val ITEM_TYPE_CHART_LINE = 1
        const val ITEM_TYPE_CHART_PIE = 2
        const val ITEM_TYPE_CHART_COLUMN = 2

        const val COLOR_POSITIVE_DARK = "26a69a"    //Primary
        const val COLOR_POSITIVE_LIGHT = "4db6ac"   //Accent
        const val COLOR_NEGATIVE_DARK = "f44336"    //Primary
        const val COLOR_NEGATIVE_LIGHT = "e57373"   //Accent

        const val COLOR_NEURAL = "607d8b"
    }

    fun getItemType(): Int

    fun getView(context: Context): View
}