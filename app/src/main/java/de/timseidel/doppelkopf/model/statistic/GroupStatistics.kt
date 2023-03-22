package de.timseidel.doppelkopf.model.statistic

data class GroupStatistics(
    val general: StatisticEntry = StatisticEntry(),
    val re: StatisticEntry = StatisticEntry(),
    val contra: StatisticEntry = StatisticEntry(),
    val solo: StatisticEntry = StatisticEntry(),

    val sessionStatistics: MutableList<SessionStatistics> = mutableListOf(),
    val memberStatistics : MutableList<MemberStatistic> = mutableListOf()
)