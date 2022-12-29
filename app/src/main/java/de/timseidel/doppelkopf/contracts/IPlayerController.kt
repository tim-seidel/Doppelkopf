package de.timseidel.doppelkopf.contracts

import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.model.PlayerAndFaction

interface IPlayerController {

    fun createPlayer(name: String): Player

    fun createPlayers(names: List<String>): List<Player>

    fun addPlayer(player: Player)

    fun addPlayers(p: List<Player>)

    fun removePlayer(player: Player)

    fun getPlayerByName(name: String): Player?

    fun getPlayers(): List<Player>

    fun getPlayersAsFaction(): List<PlayerAndFaction>

    fun validateNames(names: List<String>): Boolean

    fun getPlayerNames(): List<String>

    fun reset()
}