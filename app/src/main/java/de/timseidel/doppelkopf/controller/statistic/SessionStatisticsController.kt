package de.timseidel.doppelkopf.controller.statistic

import de.timseidel.doppelkopf.contracts.statistic.IPlayerStatisticsCalculator
import de.timseidel.doppelkopf.contracts.statistic.ISessionStatisticsCalculator
import de.timseidel.doppelkopf.contracts.statistic.ISessionStatisticsController
import de.timseidel.doppelkopf.model.statistic.SessionStatisticsCalculator

class SessionStatisticsController : ISessionStatisticsController {

    private val _sessionStatisticsCalculator: SessionStatisticsCalculator =
        SessionStatisticsCalculator()

    override fun getPlayerStatisticsCalculator(): IPlayerStatisticsCalculator {
        return _sessionStatisticsCalculator
    }

    override fun getSessionStatisticsCalculator(): ISessionStatisticsCalculator {
        return _sessionStatisticsCalculator
    }
}