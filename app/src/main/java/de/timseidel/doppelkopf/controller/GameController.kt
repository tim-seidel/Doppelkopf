package de.timseidel.doppelkopf.controller

import de.timseidel.doppelkopf.contracts.IGameController
import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.GameResult
import de.timseidel.doppelkopf.model.GameType
import de.timseidel.doppelkopf.model.PlayerAndFaction
import de.timseidel.doppelkopf.util.GameUtil
import de.timseidel.doppelkopf.util.IdGenerator

class GameController : IGameController {

    private val games = mutableListOf<Game>()

    override fun createGame(
        players: List<PlayerAndFaction>,
        winningFaction: Faction,
        winningPoints: Int,
        tacken: Int,
        isBockrunde: Boolean,
        gameType: GameType
    ): Game {
        return Game(
            IdGenerator.generateIdWithTimestamp("game"),
            System.currentTimeMillis(),
            players,
            winningFaction,
            winningPoints,
            tacken,
            isBockrunde,
            gameType
        )
    }

    override fun addGame(game: Game) {
        games.add(game)
    }

    override fun updateGame(gameId: String, updatedGame: Game) {
        val game = games.find { g -> g.id == gameId }
        if (game != null) {
            game.timestamp = updatedGame.timestamp
            game.players.forEach { p ->
                val updatedPlayer = updatedGame.players.find { up -> up.player.id == p.player.id }
                if (updatedPlayer != null) {
                    p.faction = updatedPlayer.faction
                }
            }
            game.winningFaction = updatedGame.winningFaction
            game.winningPoints = updatedGame.winningPoints
            game.tacken = updatedGame.tacken
            game.isBockrunde = updatedGame.isBockrunde
            game.gameType = updatedGame.gameType
            game.soloType = updatedGame.soloType
        }
        games.indexOf(games.find { g -> g.id == gameId }).let { index ->
            if (index != -1) {
                games[index] = updatedGame
            }
        }
    }

    override fun removeGame(game: Game) {
        games.remove(game)
    }

    override fun getGame(gameId: String): Game? {
        return games.find { g -> g.id == gameId }
    }

    override fun getGames(): List<Game> {
        return games.toList()
    }

    override fun getGameAsPlayerResults(game: Game): List<GameResult> {
        val results = mutableListOf<GameResult>()

        game.players.forEach { p ->
            results.add(
                GameResult(
                    game.id,
                    p.faction,
                    p.faction == game.winningFaction,
                    GameUtil.getFactionTacken(p.faction, game),
                    GameUtil.getFactionPoints(
                        p.faction,
                        game.winningFaction,
                        game.winningPoints
                    ),
                    game.isBockrunde,
                    game.gameType
                )
            )
        }

        return results
    }

    override fun getGamesAsPlayerResults(): List<List<GameResult>> {
        val results = mutableListOf<List<GameResult>>()
        games.forEach { g ->
            results.add(getGameAsPlayerResults(g))
        }
        return results
    }

    override fun getGamesOfPlayer(playerId: String): List<Game> {
        val pGames = mutableListOf<Game>()
        games.forEach { g ->
            if (g.players.any { p -> p.player.id == playerId && p.faction != Faction.NONE }) pGames.add(
                g
            )
        }

        return pGames
    }

    override fun reset() {
        games.clear()
    }
}