package de.timseidel.doppelkopf.model.statistic.session

import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.model.statistic.StatisticEntry

data class PlayerToPlayerStatistic(
    val player: Player,
    val general: StatisticEntry = StatisticEntry(),
    val re: StatisticEntry = StatisticEntry(),
    val contra: StatisticEntry = StatisticEntry()
)