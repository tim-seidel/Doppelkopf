package de.timseidel.doppelkopf.contracts

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.model.PlayerAndFaction
import de.timseidel.doppelkopf.util.PlayerGameResult

interface IGameController {

     fun createGame(players: List<PlayerAndFaction>, winningFaction: Faction, winningPoints: Int, tacken: Int): Game

     fun addGame(game: Game)

     fun removeGame(game: Game)

     fun getGames(): List<Game>

     fun getGameAsPlayerResults(game: Game) : List<PlayerGameResult>

     fun getGamesAsPlayerResults() : List<List<PlayerGameResult>>

     fun getGamesOfPlayer(pId: String): List<Game>
}