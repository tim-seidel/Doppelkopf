package de.timseidel.doppelkopf.controller

import de.timseidel.doppelkopf.contracts.IGameController
import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.PlayerAndFaction
import de.timseidel.doppelkopf.util.DokoUtil
import de.timseidel.doppelkopf.util.IdGenerator
import de.timseidel.doppelkopf.model.PlayerGameResult

class GameController : IGameController {

    private val games = mutableListOf<Game>()

    override fun createGame(
        players: List<PlayerAndFaction>,
        winningFaction: Faction,
        winningPoints: Int,
        tacken: Int,
        isBockrunde: Boolean
    ): Game {
        return Game(
            IdGenerator.generateIdWithTimestamp("game"),
            System.currentTimeMillis(),
            players,
            winningFaction,
            winningPoints,
            tacken,
            isBockrunde
        )
    }

    override fun addGame(game: Game) {
        games.add(game)
    }

    override fun removeGame(game: Game) {
        games.remove(game)
    }

    override fun getGames(): List<Game> {
        return games.toList()
    }

    override fun getGameAsPlayerResults(game: Game): List<PlayerGameResult> {
        val results = mutableListOf<PlayerGameResult>()

        game.players.forEach { p ->
            results.add(
                PlayerGameResult(
                    p.faction,
                    p.faction == game.winningFaction,
                    DokoUtil.getFactionTacken(p.faction, game),
                    DokoUtil.getFactionPoints(p.faction, game.winningFaction, game.winningPoints)
                )
            )
        }

        return results
    }

    override fun getGamesAsPlayerResults(): List<List<PlayerGameResult>> {
        val results = mutableListOf<List<PlayerGameResult>>()
        games.forEach { g ->
            results.add(getGameAsPlayerResults(g))
        }
        return results
    }

    override fun getGamesOfPlayer(pId: String): List<Game> {
        val pGames = mutableListOf<Game>()
        games.forEach { g ->
            if (g.players.any { p -> p.player.id == pId && p.faction != Faction.NONE }) pGames.add(g)
        }

        return pGames
    }

    override fun reset() {
        games.clear()
    }
}