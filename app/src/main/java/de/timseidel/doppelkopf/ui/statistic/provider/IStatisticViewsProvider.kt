package de.timseidel.doppelkopf.ui.statistic.provider

import de.timseidel.doppelkopf.ui.statistic.views.IStatisticViewWrapper

interface IStatisticViewsProvider {

    fun getStatisticItems(isBockrundeEnabled: Boolean): List<IStatisticViewWrapper>
}