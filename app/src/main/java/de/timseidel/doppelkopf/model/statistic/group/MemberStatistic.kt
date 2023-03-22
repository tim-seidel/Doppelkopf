package de.timseidel.doppelkopf.model.statistic.group

import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.model.PlayerGameResult
import de.timseidel.doppelkopf.model.statistic.session.PlayerStatistic
import de.timseidel.doppelkopf.model.statistic.StatisticEntry

data class MemberStatistic(
    val member: Member,
    val general: StatisticEntry = StatisticEntry(),
    val re: StatisticEntry = StatisticEntry(),
    val contra: StatisticEntry = StatisticEntry(),
    val solo: StatisticEntry = StatisticEntry(),

    val partners: Map<String, MemberToMemberStatistic>,
    val opponents: Map<String, MemberToMemberStatistic>,

    val gameResultHistory: MutableList<PlayerGameResult> = mutableListOf(),

    val sessionStatistics: MutableList<PlayerStatistic> = mutableListOf()
)