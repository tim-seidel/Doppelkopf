package de.timseidel.doppelkopf.contracts.statistic

import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.model.statistic.PlayerStatistic

interface IPlayerStatisticsCalculator {
    fun calculatePlayerStatistic(
        player: Player,
        otherPlayers: List<Player>,
        games: List<Game>
    ): PlayerStatistic

    fun calculatePlayerStatistics(
        players: List<Player>,
        games: List<Game>
    ): List<PlayerStatistic>
}