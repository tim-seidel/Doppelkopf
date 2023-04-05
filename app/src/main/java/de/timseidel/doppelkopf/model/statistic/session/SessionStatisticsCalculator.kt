package de.timseidel.doppelkopf.model.statistic.session

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.GameResult
import de.timseidel.doppelkopf.model.GameType
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.model.PlayerAndFaction
import de.timseidel.doppelkopf.model.statistic.SimpleStatisticEntry
import de.timseidel.doppelkopf.model.statistic.StatisticEntry
import de.timseidel.doppelkopf.util.GameUtil

class SessionStatisticsCalculator {

    fun calculateSessionStatistics(players: List<Player>, games: List<Game>): SessionStatistics {
        val stats = calculateGeneralSessionStatistics(games)

        val playerStatistics = calculateAllPlayerStatistics(players, games)
            .sortedBy { playerStatistic -> playerStatistic.player.name }

        stats.playerStatistics.addAll(playerStatistics)

        return stats
    }

    fun calculateGeneralSessionStatistics(games: List<Game>): SessionStatistics {
        val stats = SessionStatistics()

        games.forEach { g ->
            if (g.winningFaction != Faction.NONE) {
                val winnerResult =
                    GameResult(
                        g.winningFaction,
                        true,
                        g.tacken,
                        g.winningPoints,
                        g.isBockrunde,
                        g.gameType
                    )
                val loserResult =
                    GameResult(
                        if (g.winningFaction == Faction.RE) Faction.CONTRA else Faction.RE,
                        false,
                        -1 * g.tacken,
                        240 - g.winningPoints,
                        g.isBockrunde,
                        g.gameType
                    )

                addGame(stats.general.total, winnerResult) //TODO abs(tacken)?
                addGame(stats.general.wins, winnerResult)
                addGame(stats.general.loss, loserResult)

                stats.gameResultHistoryWinner.add(winnerResult)
                stats.gameResultHistoryLoser.add(loserResult)

                if (winnerResult.faction == Faction.RE) {
                    addGame(stats.re.total, winnerResult)
                    addGame(stats.re.wins, winnerResult)

                    addGame(stats.contra.total, loserResult)
                    addGame(stats.contra.loss, loserResult)
                } else {
                    addGame(stats.contra.total, winnerResult)
                    addGame(stats.contra.wins, winnerResult)

                    addGame(stats.re.total, loserResult)
                    addGame(stats.re.loss, loserResult)
                }

                if (g.gameType == GameType.SOLO) {
                    if (g.winningFaction == Faction.RE) {
                        addGame(stats.solo.wins, winnerResult)
                        addGame(stats.solo.total, winnerResult)
                    } else {
                        addGame(stats.solo.total, loserResult)
                        addGame(stats.solo.loss, loserResult)
                    }
                }
            }
        }

        return stats
    }

    fun calculateSinglePlayerStatistics(
        player: Player,
        allPlayers: List<Player>,
        games: List<Game>
    ): PlayerStatistic {

        val stats = PlayerStatistic(
            player = player,
            partners = getOtherPlayers(player, allPlayers).associateBy(
                { it.id },
                { PlayerToPlayerStatistic(it) }),
            opponents = getOtherPlayers(player, allPlayers).associateBy(
                { it.id },
                { PlayerToPlayerStatistic(it) }
            )
        )

        calculateOwnPlayerStatistics(stats, games)
        calculatePlayerPartnerStatistics(stats, games)

        return stats
    }

    fun calculateAllPlayerStatistics(
        players: List<Player>,
        games: List<Game>
    ): List<PlayerStatistic> {
        val stats = mutableListOf<PlayerStatistic>()
        players.forEach { player ->
            stats.add(calculateSinglePlayerStatistics(player, players, games))
        }
        return stats
    }

    private fun getOtherPlayers(player: Player, allPlayers: List<Player>): List<Player> {
        return allPlayers.filter { p -> p.id != player.id }
    }

    private fun calculateOwnPlayerStatistics(stats: PlayerStatistic, games: List<Game>) {
        games.forEach { g ->
            val result = GameUtil.getPlayerResult(stats.player, g)

            when (result.faction) {
                Faction.RE -> {
                    addGameResult(stats.general, result)
                    addGameResult(stats.re, result)

                    if (GameUtil.isPlayerPlayingSolo(stats.player, g)) {
                        addGameResult(stats.solo, result)
                    }
                }
                Faction.CONTRA -> {
                    addGameResult(stats.general, result)
                    addGameResult(stats.contra, result)
                }
                else -> {}
            }

            stats.gameResultHistory.add(result)
        }
    }

    private fun calculatePlayerPartnerStatistics(stats: PlayerStatistic, games: List<Game>) {
        games.forEach { g ->
            val result = GameUtil.getPlayerResult(stats.player, g)

            if (result.faction != Faction.NONE) {
                val participants =
                    g.players.filter { paf -> paf.faction != Faction.NONE && paf.player.id != stats.player.id }
                addPlayerPartnerStatisticForGame(stats, result, participants)
            }
        }
    }

    private fun addPlayerPartnerStatisticForGame(
        stats: PlayerStatistic,
        result: GameResult,
        participants: List<PlayerAndFaction>
    ) {
        participants.forEach { partner ->
            if (partner.faction == result.faction) {
                addGameResult(stats.partners[partner.player.id]?.general, result)
                if (partner.faction == Faction.RE) {
                    addGameResult(stats.partners[partner.player.id]?.re, result)
                } else if (partner.faction == Faction.CONTRA) {
                    addGameResult(stats.partners[partner.player.id]?.contra, result)
                }
            } else {
                addGameResult(stats.opponents[partner.player.id]?.general, result)
                if (partner.faction == Faction.RE) addGameResult(
                    stats.opponents[partner.player.id]?.contra, result
                )
                else if (partner.faction == Faction.CONTRA) addGameResult(
                    stats.opponents[partner.player.id]?.re, result
                )
            }
        }
    }

    private fun addGameResult(
        stats: StatisticEntry?,
        gameResult: GameResult
    ) {
        if (stats != null && gameResult.faction != Faction.NONE) {
            addGame(stats.total, gameResult)
            if (gameResult.isWinner) {
                addGame(stats.wins, gameResult)

            } else {
                addGame(stats.loss, gameResult)
            }
        }
    }

    private fun addGame(stats: SimpleStatisticEntry, game: GameResult) {
        stats.games += 1
        stats.tacken += game.tacken
        stats.points += game.points
    }
}
