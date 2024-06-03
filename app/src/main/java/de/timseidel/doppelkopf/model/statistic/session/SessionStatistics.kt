package de.timseidel.doppelkopf.model.statistic.session

import de.timseidel.doppelkopf.model.GameResult
import de.timseidel.doppelkopf.model.statistic.StatisticEntry

data class SessionStatistics(
    val id : String,
    val general: StatisticEntry = StatisticEntry(),
    val re: StatisticEntry = StatisticEntry(),
    val contra: StatisticEntry = StatisticEntry(),
    val solo: StatisticEntry = StatisticEntry(),

    val gameResultHistoryWinner: MutableList<GameResult> = mutableListOf(),
    val gameResultHistoryLoser: MutableList<GameResult> = mutableListOf(),

    val memberSessionStatistics: MutableList<MemberSessionStatistic> = mutableListOf()
)