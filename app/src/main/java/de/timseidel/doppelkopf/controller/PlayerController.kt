package de.timseidel.doppelkopf.controller

import de.timseidel.doppelkopf.contracts.IPlayerController
import de.timseidel.doppelkopf.model.Faction
import de.timseidel.doppelkopf.model.Player
import de.timseidel.doppelkopf.model.PlayerAndFaction
import de.timseidel.doppelkopf.util.IdGenerator

class PlayerController : IPlayerController {

    private val players = mutableListOf<Player>()

    override fun createPlayer(name: String): Player {
        val trimmedName = name.trim()
        val nonEmptyName = trimmedName.ifEmpty { "Player" }
        return Player(IdGenerator.generateIdWithTimestamp("player") + "_$nonEmptyName", nonEmptyName)
    }

    override fun createPlayers(names: List<String>): List<Player> {
        if (!validateNames(names)) throw Exception("Invalid name list (check for duplicates or empty name)")

        val plys = mutableListOf<Player>()
        for (name in names)
            plys.add(createPlayer(name))

        return plys
    }

    override fun addPlayer(player: Player) {
        players.add(player)
    }

    override fun addPlayers(p: List<Player>) {
        players.addAll(p)
    }

    override fun removePlayer(player: Player) {
        players.removeIf { p -> p.name == player.name }
    }

    override fun getPlayerByName(name: String): Player? {
        return players.firstOrNull { p -> p.name == name }
    }

    override fun getPlayers(): List<Player> {
        return players.toList()
    }

    override fun getPlayersAsFaction(): List<PlayerAndFaction> {
        val asFaction: MutableList<PlayerAndFaction> = mutableListOf()

        players.forEach { p ->
            asFaction.add(PlayerAndFaction(p, Faction.NONE))
        }

        return asFaction
    }

    override fun validateNames(names: List<String>): Boolean {
        if (names.size != names.distinct().count()) return false
        for (name in names)
            if (name.trim().isEmpty()) return false
        return true
    }
}