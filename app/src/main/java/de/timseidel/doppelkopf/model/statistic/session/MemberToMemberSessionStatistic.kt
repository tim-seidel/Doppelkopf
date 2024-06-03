package de.timseidel.doppelkopf.model.statistic.session

import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.model.statistic.StatisticEntry

data class MemberToMemberSessionStatistic(
    val member: Member,
    val general: StatisticEntry = StatisticEntry(),
    val re: StatisticEntry = StatisticEntry(),
    val contra: StatisticEntry = StatisticEntry()
)