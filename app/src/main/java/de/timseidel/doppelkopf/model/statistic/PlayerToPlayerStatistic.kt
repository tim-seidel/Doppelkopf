package de.timseidel.doppelkopf.model.statistic

import de.timseidel.doppelkopf.model.Player

data class PlayerToPlayerStatistic(
    val player: Player,
    val general: StatisticEntry = StatisticEntry(),
    val re: StatisticEntry = StatisticEntry(),
    val contra: StatisticEntry = StatisticEntry()
)