package de.timseidel.doppelkopf.model.statistic.group

import de.timseidel.doppelkopf.model.Member
import de.timseidel.doppelkopf.model.statistic.StatisticEntry

data class MemberToMemberStatistic(
    val member: Member,
    val general: StatisticEntry = StatisticEntry(),
    val re: StatisticEntry = StatisticEntry(),
    val contra: StatisticEntry = StatisticEntry()
)