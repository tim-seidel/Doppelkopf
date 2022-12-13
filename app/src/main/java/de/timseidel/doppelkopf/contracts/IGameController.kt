package de.timseidel.doppelkopf.contracts

import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.Game
import de.timseidel.doppelkopf.model.PlayerAndFaction

interface IGameController {

     fun createGame(players: List<PlayerAndFaction>, winningFaction: Faction, tacken: Int, score: Int): Game

     fun addGame(game: Game)

     fun removeGame(game: Game)

     fun getGames(): List<Game>
}