package de.timseidel.doppelkopf.contracts.statistic

import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.statistic.SessionStatistics

interface ISessionStatisticsCalculator {
    fun calculateSessionStatistics(games: List<Game>): SessionStatistics
}