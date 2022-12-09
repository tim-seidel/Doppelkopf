package de.timseidel.doppelkopf.controller

import de.timseidel.doppelkopf.contracts.IGameController
import de.timseidel.doppelkopf.model.Game

class GameController : IGameController {

    private val games = mutableListOf<Game>()

    override fun createGame(): Game {
        TODO("Not yet implemented")
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