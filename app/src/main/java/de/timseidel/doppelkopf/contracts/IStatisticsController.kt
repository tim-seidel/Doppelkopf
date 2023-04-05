package de.timseidel.doppelkopf.contracts

import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.model.statistic.group.GroupStatistics

interface IStatisticsController {

    fun getCachedGroupStatistics(): GroupStatistics?

    fun calculateGroupStatistics(members: List<Member>, sessions: List<ISessionController>): GroupStatistics

    fun reset()
}