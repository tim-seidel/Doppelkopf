package de.timseidel.doppelkopf.ui.session.statistic.provider

import de.timseidel.doppelkopf.ui.session.statistic.IStatisticViewWrapper
import de.timseidel.doppelkopf.ui.session.statistic.views.SimpleTextStatisticViewWrapper

class EmptyStatisticViewProvider : IStatisticViewsProvider {

    override fun getStatisticItems(): List<IStatisticViewWrapper> {
        return listOf(
            SimpleTextStatisticViewWrapper(
                "Keine Statistiken verfügbar",
                "Aktuell gibt es noch keine Statistiken hierzu. Spiele mehr Spiele, um Statistiken zu erhalten",
                ""
            )
        )
    }
}