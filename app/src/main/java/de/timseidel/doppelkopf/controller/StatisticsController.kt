package de.timseidel.doppelkopf.controller

import de.timseidel.doppelkopf.contracts.ISessionController
import de.timseidel.doppelkopf.contracts.IStatisticsController
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.model.StatisticStatus
import de.timseidel.doppelkopf.model.statistic.group.GroupStatistics
import de.timseidel.doppelkopf.model.statistic.group.GroupStatisticsCalculator
import de.timseidel.doppelkopf.model.statistic.session.SessionStatistics
import de.timseidel.doppelkopf.model.statistic.session.SessionStatisticsCalculator

class StatisticsController : IStatisticsController {

    private var groupStatistics: GroupStatistics = GroupStatistics()
    private var status: StatisticStatus = StatisticStatus.EMPTY

    override fun getCachedGroupStatistics(): GroupStatistics {
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
                    session.getSession().id,
                    session.getSession().members,
                    session.getGameController().getGames()
                )
            sessionStatistics.add(singleSessionStatistics)
        }

        val stats = GroupStatisticsCalculator().calculateGroupStatistics(
            members,
            sessionStatistics
        )

        groupStatistics = stats

        status = if (members.isEmpty() || sessionStatistics.isEmpty()) {
            StatisticStatus.EMPTY
        } else {
            StatisticStatus.CACHED
        }
        return stats
    }

    override fun reset() {
        groupStatistics = GroupStatistics()
        status = StatisticStatus.EMPTY
    }

    override fun invalidate() {
        status = StatisticStatus.INVALIDATED
    }

    override fun getStatus(): StatisticStatus {
        return status
    }

    override fun isCachedStatisticsAvailable(): Boolean {
        return status == StatisticStatus.CACHED
    }
}