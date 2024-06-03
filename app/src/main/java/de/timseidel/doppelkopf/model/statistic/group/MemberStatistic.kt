package de.timseidel.doppelkopf.model.statistic.group

import de.timseidel.doppelkopf.model.GameResult
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.model.statistic.StatisticEntry
import de.timseidel.doppelkopf.model.statistic.session.MemberSessionStatistic

data class MemberStatistic(
    val member: Member,
    val general: StatisticEntry = StatisticEntry(),
    val re: StatisticEntry = StatisticEntry(),
    val contra: StatisticEntry = StatisticEntry(),
    val solo: StatisticEntry = StatisticEntry(),

    val partners: Map<String, MemberToMemberStatistic>,
    val opponents: Map<String, MemberToMemberStatistic>,

    val gameResultHistory: MutableList<GameResult> = mutableListOf(),

    val sessionStatistics: MutableList<MemberSessionStatistic> = mutableListOf()
)