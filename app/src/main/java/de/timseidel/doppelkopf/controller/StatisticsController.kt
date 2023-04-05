package de.timseidel.doppelkopf.controller

import de.timseidel.doppelkopf.contracts.ISessionController
import de.timseidel.doppelkopf.contracts.IStatisticsController
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.model.statistic.group.GroupStatistics
import de.timseidel.doppelkopf.model.statistic.group.GroupStatisticsCalculator
import de.timseidel.doppelkopf.model.statistic.session.SessionStatistics
import de.timseidel.doppelkopf.model.statistic.session.SessionStatisticsCalculator

class StatisticsController : IStatisticsController {

    private var groupStatistics: GroupStatistics? = null

    override fun getCachedGroupStatistics(): GroupStatistics? {
        return groupStatistics
    }

    override fun calculateGroupStatistics(
        members: List<Member>,
        sessions: List<ISessionController>
    ): GroupStatistics {
        val sessionStatistics = mutableListOf<SessionStatistics>()

        sessions.forEach { session ->
            val singleSessionStatistics =
                SessionStatisticsCalculator().calculateSessionStatistics(
                    session.getPlayerController().getPlayers(),
                    session.getGameController().getGames()
                )
            sessionStatistics.add(singleSessionStatistics)
        }

        val stats = GroupStatisticsCalculator().calculateGroupStatistics(
            members,
            sessionStatistics
        )

        //TODO: Vllt. ueberschreiben, damit jeder das aktuellste Objekt hat
        groupStatistics = stats

        return stats
    }

    override fun reset() {
        groupStatistics = null
    }


}