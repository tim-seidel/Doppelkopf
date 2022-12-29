package de.timseidel.doppelkopf.model.statistic

import de.timseidel.doppelkopf.model.PlayerGameResult

data class SessionStatistics(
    val general: StatisticEntry = StatisticEntry(),
    val re: StatisticEntry = StatisticEntry(),
    val contra: StatisticEntry = StatisticEntry(),
    val solo: StatisticEntry = StatisticEntry(),

    val gameResultHistoryWinner: MutableList<PlayerGameResult> = mutableListOf(),
    val gameResultHistoryLoser: MutableList<PlayerGameResult> = mutableListOf(),
    val gameResultHistoryRe: MutableList<PlayerGameResult> = mutableListOf(),
    val gameResultHistoryContra: MutableList<PlayerGameResult> = mutableListOf()
)