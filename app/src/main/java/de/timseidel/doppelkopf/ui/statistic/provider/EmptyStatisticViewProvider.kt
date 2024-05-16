package de.timseidel.doppelkopf.ui.statistic.provider

import de.timseidel.doppelkopf.ui.statistic.views.IStatisticViewWrapper
import de.timseidel.doppelkopf.ui.statistic.views.SimpleTextStatisticViewWrapper

class EmptyStatisticViewProvider : IStatisticViewsProvider {

    override fun getStatisticItems(isBockrundeEnabled: Boolean): List<IStatisticViewWrapper> {
        return listOf(
            SimpleTextStatisticViewWrapper(
                "Keine Statistiken verfügbar",
                "Aktuell gibt es noch keine Statistiken hierzu. Spiele mehr Spiele, um Statistiken zu erhalten",
                ""
            )
        )
    }
}