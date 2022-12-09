package de.timseidel.doppelkopf.contracts

import de.timseidel.doppelkopf.model.Game

interface IGameController {

     fun createGame(): Game

     fun addGame(game: Game)

     fun removeGame(game: Game)

     fun getGames(): List<Game>
}