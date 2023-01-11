package de.timseidel.doppelkopf.contracts

import de.timseidel.doppelkopf.model.*

interface IGameController {

     fun createGame(players: List<PlayerAndFaction>, winningFaction: Faction, winningPoints: Int, tacken: Int, isBockrunde: Boolean, gameType: GameType): Game

     fun addGame(game: Game)

     fun removeGame(game: Game)

     fun getGames(): List<Game>

     fun getGameAsPlayerResults(game: Game) : List<PlayerGameResult>

     fun getGamesAsPlayerResults() : List<List<PlayerGameResult>>

     fun getGamesOfPlayer(pId: String): List<Game>

     fun reset()
}