package de.timseidel.doppelkopf.ui.session.statistic.provider

import de.timseidel.doppelkopf.ui.session.statistic.IStatisticViewWrapper

interface IStatisticViewsProvider {

    fun getStatisticItems(): List<IStatisticViewWrapper>
}