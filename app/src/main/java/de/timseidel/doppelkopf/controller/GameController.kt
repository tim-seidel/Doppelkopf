package de.timseidel.doppelkopf.controller

import de.timseidel.doppelkopf.contracts.IGameController
import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.PlayerAndFaction

class GameController : IGameController {

    private val games = mutableListOf<Game>()

    override fun createGame(
        players: List<PlayerAndFaction>,
        winningFaction: Faction,
        tacken: Int,
        score: Int
    ): Game {
        return Game(
            players = players,
            winningFaction = winningFaction,
            winningPoints = score,
            tacken = tacken
        )
    }

    override fun addGame(game: Game) {
        games.add(game)
    }

    override fun removeGame(game: Game) {
        games.remove(game)
    }

    override fun getGames(): List<Game> {
        return games
    }
}