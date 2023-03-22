package de.timseidel.doppelkopf.contracts

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.GameType
import de.timseidel.doppelkopf.model.PlayerAndFaction
import de.timseidel.doppelkopf.model.GameResult

interface IGameController {

     fun createGame(players: List<PlayerAndFaction>, winningFaction: Faction, winningPoints: Int, tacken: Int, isBockrunde: Boolean, gameType: GameType): Game

     fun addGame(game: Game)

     fun removeGame(game: Game)

     fun getGames(): List<Game>

     fun getGameAsPlayerResults(game: Game) : List<GameResult>

     fun getGamesAsPlayerResults() : List<List<GameResult>>

     fun getGamesOfPlayer(pId: String): List<Game>

     fun reset()
}