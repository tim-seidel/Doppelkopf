package de.timseidel.doppelkopf.model.statistic

import de.timseidel.doppelkopf.model.Player

data class PlayerStatistic(
    val player: Player,
    val general: StatisticEntry = StatisticEntry(),
    val re: StatisticEntry = StatisticEntry(),
    val contra: StatisticEntry = StatisticEntry(),
    val solo: StatisticEntry = StatisticEntry(),

    val partners: Map<String, PlayerToPlayerStatistic>,
    val opponents: Map<String, PlayerToPlayerStatistic>
)