package de.timseidel.doppelkopf.contracts

import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.model.statistic.PlayerStatistic
interface IPlayerStatisticsCalculator {
    fun calculatePlayerStatistic(
        player: Player,
        otherPlayers: List<Player>,
        games: List<Game>
    ): PlayerStatistic

    fun calculatePlayerStatistic(
        players: List<Player>,
        games: List<Game>
    ): List<PlayerStatistic>
}