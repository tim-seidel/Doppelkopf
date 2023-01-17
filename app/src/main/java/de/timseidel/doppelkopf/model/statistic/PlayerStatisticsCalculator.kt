package de.timseidel.doppelkopf.model.statistic

import de.timseidel.doppelkopf.contracts.IPlayerStatisticsCalculator
import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.model.PlayerAndFaction
import de.timseidel.doppelkopf.util.DokoUtil
import de.timseidel.doppelkopf.model.PlayerGameResult

//TODO: Maybe save lists and be able to add new games instead of complete recalculations
class PlayerStatisticsCalculator : IPlayerStatisticsCalculator {

    override fun calculatePlayerStatistic(
        player: Player,
        otherPlayers: List<Player>,
        games: List<Game>
    ): PlayerStatistic {

        val stats = PlayerStatistic(
            player = player,
            partners = createOpponentStatisticObjects(player, otherPlayers),
            opponents = createOpponentStatisticObjects(player, otherPlayers)
        )

        calculateOwnPlayerStatistics(stats, games)
        calculatePlayerPartnerStatistics(stats, games)

        return stats
    }

    override fun calculatePlayerStatistic(
        players: List<Player>,
        games: List<Game>
    ): List<PlayerStatistic> {
        val stats = mutableListOf<PlayerStatistic>()
        players.forEach { player ->
            val otherPlayers = players.filter { p -> p.id != player.id }
            stats.add(calculatePlayerStatistic(player, otherPlayers, games))
        }
        return stats
    }

    private fun createOpponentStatisticObjects(
        current: Player,
        players: List<Player>
    ): Map<String, PlayerToPlayerStatistic> {
        val otherStats = mutableMapOf<String, PlayerToPlayerStatistic>()
        players.forEach { p ->
            if (p.id != current.id) {
                otherStats[p.id] = PlayerToPlayerStatistic(player = p)
            }
        }

        return otherStats
    }

    private fun calculateOwnPlayerStatistics(stats: PlayerStatistic, games: List<Game>) {
        games.forEach { g ->
            val result = DokoUtil.getPlayerResult(stats.player, g)

            when (result.faction) {
                Faction.RE -> {
                    addGameResult(result, stats.general)
                    addGameResult(result, stats.re)

                    if (DokoUtil.isPlayerPlayingSolo(stats.player, g)) {
                        addGameResult(result, stats.solo)
                    }
                }
                Faction.CONTRA -> {
                    addGameResult(result, stats.general)
                    addGameResult(result, stats.contra)
                }
                else -> {}
            }

            stats.gameResultHistory.add(result)
        }
    }

    private fun calculatePlayerPartnerStatistics(stats: PlayerStatistic, games: List<Game>) {
        games.forEach { g ->
            val result = DokoUtil.getPlayerResult(stats.player, g)

            if (result.faction != Faction.NONE) {
                val participants =
                    g.players.filter { paf -> paf.faction != Faction.NONE && paf.player.id != stats.player.id }
                addPlayerPartnerStatisticForGame(stats, result, participants)
            }
        }
    }

    private fun addPlayerPartnerStatisticForGame(
        stats: PlayerStatistic,
        result: PlayerGameResult,
        participants: List<PlayerAndFaction>
    ) {
        participants.forEach { partner ->
            if (partner.faction == result.faction) {
                addGameResult(result, stats.partners[partner.player.id]?.general)
                if (partner.faction == Faction.RE) {
                    addGameResult(result, stats.partners[partner.player.id]?.re)
                } else if (partner.faction == Faction.CONTRA) {
                    addGameResult(result, stats.partners[partner.player.id]?.contra)
                }
            } else {
                addGameResult(result, stats.opponents[partner.player.id]?.general)
                if (partner.faction == Faction.RE) addGameResult(
                    result,
                    stats.opponents[partner.player.id]?.contra
                )
                else if (partner.faction == Faction.CONTRA) addGameResult(
                    result,
                    stats.opponents[partner.player.id]?.re
                )
            }
        }
    }

    private fun addGameResult(
        playerGameResult: PlayerGameResult,
        stats: StatisticEntry?
    ) {
        if (stats != null && playerGameResult.faction != Faction.NONE) {
            stats.total.games += 1
            stats.total.tacken += playerGameResult.tacken
            stats.total.points += playerGameResult.points
            if (playerGameResult.isWinner) {
                stats.wins.games += 1
                stats.wins.tacken += playerGameResult.tacken
                stats.wins.points += playerGameResult.points
            } else {
                stats.loss.games += 1
                stats.loss.tacken += playerGameResult.tacken
                stats.loss.points += playerGameResult.points
            }
        }
    }
}