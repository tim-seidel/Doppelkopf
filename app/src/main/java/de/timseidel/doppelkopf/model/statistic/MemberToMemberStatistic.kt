package de.timseidel.doppelkopf.model.statistic

import de.timseidel.doppelkopf.model.Member

data class MemberToMemberStatistic(
    val member: Member,
    val general: StatisticEntry = StatisticEntry(),
    val re: StatisticEntry = StatisticEntry(),
    val contra: StatisticEntry = StatisticEntry()
)