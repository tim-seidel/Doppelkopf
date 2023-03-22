package de.timseidel.doppelkopf.model.statistic.session

import de.timseidel.doppelkopf.model.GameResult
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.model.statistic.StatisticEntry

data class PlayerStatistic(
    val player: Player,
    val general: StatisticEntry = StatisticEntry(),
    val re: StatisticEntry = StatisticEntry(),
    val contra: StatisticEntry = StatisticEntry(),
    val solo: StatisticEntry = StatisticEntry(),
    val bock: StatisticEntry = StatisticEntry(),

    val gameResultHistory: MutableList<GameResult> = mutableListOf(),

    val partners: Map<String, PlayerToPlayerStatistic>,
    val opponents: Map<String, PlayerToPlayerStatistic>
)