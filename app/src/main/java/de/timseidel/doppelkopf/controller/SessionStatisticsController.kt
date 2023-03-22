package de.timseidel.doppelkopf.controller

import de.timseidel.doppelkopf.contracts.ISessionStatisticsController
import de.timseidel.doppelkopf.model.statistic.session.SessionStatisticsCalculator

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