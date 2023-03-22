package de.timseidel.doppelkopf.model.statistic.session

import de.timseidel.doppelkopf.model.PlayerGameResult
import de.timseidel.doppelkopf.model.statistic.StatisticEntry

data class SessionStatistics(
    val general: StatisticEntry = StatisticEntry(),
    val re: StatisticEntry = StatisticEntry(),
    val contra: StatisticEntry = StatisticEntry(),
    val solo: StatisticEntry = StatisticEntry(),

    val gameResultHistoryWinner: MutableList<PlayerGameResult> = mutableListOf(),
    val gameResultHistoryLoser: MutableList<PlayerGameResult> = mutableListOf(),

    val playerStatistics: MutableList<PlayerStatistic> = mutableListOf()
)