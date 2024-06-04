package de.timseidel.doppelkopf.model.statistic.session

import de.timseidel.doppelkopf.model.GameResult
import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.model.statistic.StatisticEntry

data class SessionMemberStatistic(
    val member: Member,
    val general: StatisticEntry = StatisticEntry(),
    val re: StatisticEntry = StatisticEntry(),
    val contra: StatisticEntry = StatisticEntry(),
    val solo: StatisticEntry = StatisticEntry(),
    val bock: StatisticEntry = StatisticEntry(),

    val gameResultHistory: MutableList<GameResult> = mutableListOf(),

    val partners: Map<String, MemberToMemberSessionStatistic>,
    val opponents: Map<String, MemberToMemberSessionStatistic>
)